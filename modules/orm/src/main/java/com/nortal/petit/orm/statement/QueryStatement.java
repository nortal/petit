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
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.nortal.petit.orm.ListQuery;
import com.nortal.petit.orm.statement.clause.OrderSql;
import com.nortal.petit.orm.statement.clause.SqlPart;

public class QueryStatement<B> extends SimpleStatement<B> {

    private boolean cacheResults;

    private String[] select;

    private ListQuery<B> query;

    public QueryStatement(ListQuery<B> query, StatementBuilder statementBuilder, JdbcTemplate jdbcTemplate) {
        this(false, query, statementBuilder, jdbcTemplate);
    }

    public QueryStatement(boolean cacheResults, ListQuery<B> query, StatementBuilder statementBuilder,
            JdbcTemplate jdbcTemplate) {
        init(jdbcTemplate, statementBuilder, query.getBeanClass());

        this.cacheResults = cacheResults;
        this.query = query;
        // by default select by all properties
        select(StatementUtil.toStringArray(getMapping().props().keySet()));
    }

    protected void init(JdbcTemplate jdbcTemplate, StatementBuilder statementBuilder, Class<B> beanClass) {
        super.init(jdbcTemplate, statementBuilder, beanClass);
    }

    public QueryStatement<B> select(String... properties) {
        this.select = properties;
        return this;
    }

    public QueryStatement<B> order(OrderSql orderSql) {
        getStatementBuilder().order(orderSql);
        return this;
    }

    public QueryStatement<B> where(String property, Object value) {
        getStatementBuilder().where(property, value);
        return this;
    }

    public QueryStatement<B> where(SqlPart where) {
        getStatementBuilder().where(where);
        return this;
    }

    public ListQuery<B> query() {
        prepare();

        String sql;
        if (query.getLimit() != null) {
            sql = getStatementBuilder().limitSql(query.getLimit());
        } else {
            sql = getSql();
        }

        // Esiteks kontrollida, kas päring on eelmisega sama ja võib-olla on
        // tulemus juba cache'tud
        query.initQueryResult(getSqlWithParams());
        if (cacheResults && query.getQueryResult().getResultList() != null) {
            // We retrieved a cache result
            return query;
        }

        // Vaja teha count päring
        Object[] params = getParams(null);
        long elementCount = getJdbcTemplate().queryForObject(getStatementBuilder().countSql(), Long.class, getParams(null));
        query.getQueryResult().setTotalCount(elementCount);

        if (query.getQueryResult().getTotalCount() > 0) {
            List<B> result = getJdbcTemplate().query(sql, getMapper(), getParams(null));
            query.getQueryResult().setResultList(result, query.getLimit());
        } else {
            query.getQueryResult().setResultList(new ArrayList<B>(), query.getLimit());
        }

        return query;
    }

    @Override
    protected void prepare() {
        getStatementBuilder().setPropertyNameMapper(getPropertyNameMapper(true));
        getStatementBuilder().select(select);
        getStatementBuilder().setWhere(query.getFilter());
        getStatementBuilder().setOrder(query.getOrder());
        super.setSql(getStatementBuilder().getLoad());
    }

    @Override
    protected StatementType getStatementType() {
        return StatementType.QUERY;
    }
}
