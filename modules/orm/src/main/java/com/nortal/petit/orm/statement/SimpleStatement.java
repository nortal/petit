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
import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.beanmapper.BeanMappings;
import com.nortal.petit.converter.config.ConverterConfig;
import com.nortal.petit.orm.BeanMapper;
import com.nortal.petit.orm.BeanMappers;

/**
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com)
 * @author Alrik Peets
 * @created 30.04.2013
 * @param <B>
 */
public abstract class SimpleStatement<B> {

    private JdbcOperations jdbcTemplate;
    private BeanMapper<B> beanMapper;
    private StatementBuilder statementBuilder;
    private String sql;

    protected void init(JdbcOperations jdbcTemplate, StatementBuilder statementBuilder, BeanMapping<B> beanMapping) {
        Assert.notNull(beanMapping, "SimpleStatement.init: beanMapping is mandatory");
        Assert.notNull(jdbcTemplate, "SimpleStatement.init: jdbcTemplate is mandatory");
        Assert.notNull(statementBuilder, "SimpleStatement.init: sqlBuilder is mandatory");

        this.jdbcTemplate = jdbcTemplate;
        this.statementBuilder = statementBuilder;
        this.beanMapper = new BeanMapper<B>(beanMapping, ConverterConfig.instance().getPropertyReader());

        this.statementBuilder.table(getMapping().table());
    }

    protected void init(JdbcOperations jdbcTemplate, StatementBuilder statementBuilder, Class<B> beanClass) {
        Assert.notNull(beanClass, "SimpleStatement.init: bean class is mandatory");
        init(jdbcTemplate, statementBuilder, BeanMappings.get(beanClass));
    }

    protected JdbcOperations getJdbcTemplate() {
        return jdbcTemplate;
    }

    protected StatementBuilder getStatementBuilder() {
        return statementBuilder;
    }

    public BeanMapping<B> getMapping() {
        return getMapper().mapping();
    }

    public BeanMapper<B> getMapper() {
        return beanMapper;
    }

    protected void updateMapper(BeanMapper<B> beanMapper) {
        this.beanMapper = beanMapper;
    }

    /**
     * Specify which transient properties to include in the mapping.
     * 
     * @see BeanMappers#extended(Class, String...)
     * 
     *      This call is not cumulative.
     */
    public void setExtendedProperties(String... extendedProperties) {
        updateMapper(BeanMappers.extended(getMapping().type(), extendedProperties));
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

    protected Function<String, String> getPropertyNameMapper(final boolean includeReadOnly) {
        return StatementUtil.getPropertyNameMapper(getMapping(), includeReadOnly);
    }
}
