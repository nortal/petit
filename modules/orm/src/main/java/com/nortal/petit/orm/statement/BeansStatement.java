/**
 *   Copyright 2014 Nortal AS
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.nortal.petit.orm.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.orm.statement.clause.SqlPart;
import com.nortal.petit.orm.statement.clause.Where;

/**
 * @author Aleksei Lissitsin
 * 
 */
public abstract class BeansStatement<B, S extends BeansStatement<B, S>> extends SimpleStatement<B> {

    protected List<B> beans;

    protected List<String> setBy;

    protected List<B> getBeans() {
        return beans;
    }

    protected void setBeans(List<B> beans) {
        this.beans = beans;
    }

    @SuppressWarnings("unchecked")
    public S whereBy(String... properties) {
        for (String p : properties) {
            where(p, Where.prop(p));
        }
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S where(String property, Object value) {
        getStatementBuilder().where(property, value);
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S where(SqlPart where) {
        getStatementBuilder().where(where);
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setBy(String... properties) {
        this.setBy = Arrays.asList(properties);
        return (S) this;
    }

    protected List<String> getWritableProps(BeanMapping<B> mapping) {
        return getWritableProps(mapping, getStatementType());
    }

    protected List<String> getWritableProps(BeanMapping<B> mapping, StatementType stmtType) {
        return getStatementBuilder().getWritableProps(mapping, stmtType);
    }

    private Object[] prepareSetWith() {
        List<Object> res = new ArrayList<Object>();
        for (String p : setBy) {
            res.add(Where.prop(p));
        }
        return res.toArray();
    }

    protected void prepareSet() {
        Assert.isTrue(!(setBy == null || setBy.isEmpty()), "BeansStatement.prepareSet: setBy columns are mandatory");
        getStatementBuilder().setBy(setBy);
        getStatementBuilder().setWith(prepareSetWith());
    }

    @Override
    protected void prepare() {
        getStatementBuilder().setPropertyNameMapper(getPropertyNameMapper(true));
    }

    protected void execBatchUpdate() {
        String updateSql = getSql();
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        MappingParamFunction<B> paramFunction = new MappingParamFunction<B>(getMapping());
        for (B bean : getBeans()) {
            paramFunction.setBean(bean);
            batchArgs.add(getParams(paramFunction));
        }
        getJdbcTemplate().batchUpdate(updateSql, batchArgs);
    }

    public void exec() {
        if (!CollectionUtils.isEmpty(getBeans())) {
            execBatchUpdate();
        } else {
            getJdbcTemplate().update(getSql(), getParams(null));
        }
    }

    /**
     * Returns statements sql with parameter values
     * 
     * @return
     */
    @Override
    public String getSqlWithParams() {
        prepare();

        StringBuilder sb = new StringBuilder();
        if (!CollectionUtils.isEmpty(getBeans())) {
            MappingParamFunction<B> function = new MappingParamFunction<B>(getMapping());
            for (B bean : getBeans()) {
                function.setBean(bean);
                sb.append(super.getSqlWithParams(function)).append("\n");
            }
        } else {
            sb.append(super.getSqlWithParams()).append("\n");
        }
        return sb.toString();
    }
}
