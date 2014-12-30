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

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.util.Assert;

import com.google.common.base.Function;
import com.nortal.petit.beanmapper.BeanMapper;
import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.beanmapper.BeanMappings;
import com.nortal.petit.beanmapper.Property;
import com.nortal.petit.orm.DefaultResultSetReader;

/**
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com)
 * @created 30.04.2013
 * @param <B>
 */
public abstract class SimpleStatement<B> {

    private JdbcOperations jdbcTemplate;
    protected BeanMapping<B> mapping;
    private StatementBuilder statementBuilder;
    private String sql;

    protected void init(JdbcOperations jdbcTemplate, StatementBuilder statementBuilder, Class<B> beanClass) {
        Assert.notNull(beanClass, "SimpleStatement.init: bean class is mandatory");
        Assert.notNull(jdbcTemplate, "SimpleStatement.init: jdbcTemplate is mandatory");
        Assert.notNull(statementBuilder, "SimpleStatement.init: sqlBuilder is mandatory");

        this.jdbcTemplate = jdbcTemplate;
        this.statementBuilder = statementBuilder;
        this.mapping = BeanMappings.get(beanClass);

        this.statementBuilder.table(mapping.table());
    }

    protected JdbcOperations getJdbcTemplate() {
        return jdbcTemplate;
    }

    protected StatementBuilder getStatementBuilder() {
        return statementBuilder;
    }

    public BeanMapping<B> getMapping() {
        return mapping;
    }

    public BeanMapper<B> getMapper() {
        return new BeanMapper<B>(getMapping(), DefaultResultSetReader.instance());
    }

    protected void setSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        prepare();
        return sql;
    }

    public Object[] getParams(Function<String, Object> paramMapper) {
        return statementBuilder.getParams(paramMapper);
    }

    public String getSqlWithParams() {
        return getSqlWithParams(null);
    }

    /**
     * Returns statements sql with parameter values
     * 
     * @return
     */
    public String getSqlWithParams(Function<String, Object> paramMapper) {
        prepare();

        StringBuilder sb = new StringBuilder();
        String sql = getSql();
        Object[] params = getParams(paramMapper);
        // Replace placeholders in sql
        if (params.length > 0) {
            String[] sqlParts = sql.split("\\?");
            for (int i = 0; i < sqlParts.length; i++) {
                sb.append(sqlParts[i]);
                if (i < params.length) {
                    if (params[i] != null) {
                        sb.append("'").append(params[i]).append("'");
                    } else {
                        sb.append("null");
                    }
                }
            }
        } else {
            sb.append(sql);
        }
        sb.append(";");
        return sb.toString();
    }

    protected abstract void prepare();

    protected abstract StatementType getStatementType();

    private String getColumn(String name, boolean includeReadOnly) {
        Property<B, Object> p = getMapping().props().get(name);
        if (p == null) {
            Assert.notNull(p, "No property " + name + " found!");
        }
        if (includeReadOnly || !p.readOnly()) {
            return p.column();
        }
        return null;
    }

    protected Function<String, String> getPropertyNameMapper(final boolean includeReadOnly) {
        return new Function<String, String>() {
            public String apply(String name) {
                return getColumn(name, includeReadOnly);
            }
        };
    }
}
