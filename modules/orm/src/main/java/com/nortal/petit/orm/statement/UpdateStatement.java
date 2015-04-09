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

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.nortal.petit.beanmapper.BeanMapping;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com)
 * @created 30.04.2013
 * @param <B>
 */
public class UpdateStatement<B> extends BeansStatement<B, UpdateStatement<B>> {

    public UpdateStatement(JdbcOperations jdbcTemplate, StatementBuilder statementBuilder, B... beans) {
        Assert.isTrue(ArrayUtils.isNotEmpty(beans), "UpdateStatement.construct: beans are mandatory");

        this.beans = Arrays.asList(beans);
        super.init(jdbcTemplate, statementBuilder, (Class<B>) beans[0].getClass());

        setBy(StatementUtil.toStringArray(getWritableProps(getMapping())));
    }

    public UpdateStatement(JdbcOperations jdbcTemplate, StatementBuilder statementBuilder, Class<B> beanClass) {
        Assert.isTrue(beanClass != null, "UpdateStatement.construct: beanClass is mandatory");
        super.init(jdbcTemplate, statementBuilder, beanClass);
    }

    @Override
    protected void prepare() {
        //If no manual where statement is added, add ID mapping if ID present.
        if (getStatementBuilder().getWhereClause() == null) {
            if (getMapping().id() != null) {
                whereBy(getMapping().id().name());
            }
        }
        
        super.prepare();
        prepareSet();
        setSql(getStatementBuilder().getUpdate());
    }
    @Override
    public void exec() {
        super.exec();
        if (!CollectionUtils.isEmpty(getBeans()) && getStatementBuilder().getInterceptor() != null) {
            invokeInterceptor();
        }
    }

    private void invokeInterceptor() {
        BeanMapping<B> mapping = getMapping();
        MappingParamFunction<B> paramFunction = new MappingParamFunction<B>(getMapping());
        for (B bean : getBeans()) {
            paramFunction.setBean(bean);
            List<String> columns = Lists.transform(setBy, getPropertyNameMapper(true));
            List<Object> params = Lists.transform(setBy, paramFunction);
            Object id = mapping.id().read(bean);
            getStatementBuilder().getInterceptor().afterUpdate(mapping.table(), id, params.toArray(), null, columns.toArray(new String[columns.size()]));
            System.out.println(bean);
        }
    }

    @Override
    protected StatementType getStatementType() {
        return StatementType.UPDATE;
    }
}
