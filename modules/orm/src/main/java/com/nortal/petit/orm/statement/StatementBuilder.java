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

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.beanmapper.Property;
import com.nortal.petit.orm.BeanPropertyConverter;
import com.nortal.petit.orm.DefaultBeanPropertyConverter;
import com.nortal.petit.orm.statement.clause.Limit;
import com.nortal.petit.orm.statement.clause.Order;
import com.nortal.petit.orm.statement.clause.OrderSql;
import com.nortal.petit.orm.statement.clause.SelectClause;
import com.nortal.petit.orm.statement.clause.SetClause;
import com.nortal.petit.orm.statement.clause.SqlPart;
import com.nortal.petit.orm.statement.clause.SqlPropertyParam;
import com.nortal.petit.orm.statement.clause.Where;
import com.nortal.petit.orm.statement.clause.WhereClause;
import com.nortal.petit.orm.statement.interceptor.StatementInterceptor;

/**
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com)
 * @created 18.03.2013
 * 
 * @author Aleksei Lissitsin
 * 
 */
// TODO: maybe we should create sub builder for every CRUD operation
public abstract class StatementBuilder implements SelectClause<StatementBuilder>, SetClause,
        WhereClause<StatementBuilder> {
    private BeanPropertyConverter converter = new DefaultBeanPropertyConverter();

    private String table;
    private String alias;

    private List<String> select;
    private List<String> setBy;
    private List<Object> setWith;
    private List<String> group;

    private OrderSql orderSql;

    private SqlPart where;

    private Function<String, String> propertyNameMapper = Function.identity();

    private StatementInterceptor interceptor;

    public void setPropertyNameMapper(Function<String, String> propertyNameMapper) {
        this.propertyNameMapper = propertyNameMapper;
    }

    public void setConverter(BeanPropertyConverter converter) {
        this.converter = converter;
    }

    public StatementBuilder table(String table) {
        this.table = table;
        return this;
    }

    public StatementBuilder alias(String alias) {
        this.alias = alias;
        return this;
    }

    public StatementBuilder setBy(List<String> properties) {
        this.setBy = properties;
        return this;
    }

    protected List<String> getSetBy() {
        return setBy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public StatementBuilder setBy(String... properties) {
        return setBy(Arrays.asList(properties));
    }

    @Override
    @SuppressWarnings("unchecked")
    public StatementBuilder setWith(Object... params) {
        setWith = Arrays.asList(params);
        return this;
    }

    // === SELECT ===
    @Override
    public StatementBuilder select(String... columns) {
        this.select = Arrays.asList(columns);
        return this;
    }

    public boolean isSetSelect() {
        return select != null && !select.isEmpty();
    }
    
    public String getSelectClause() {
        Stream<String> stream;
        if (select != null && !select.isEmpty()) {
          stream = select.stream().map(propertyNameMapper);
        } else {
          stream = Stream.of("*");
        }
        if (StringUtils.isNotEmpty(alias)) {
          stream = stream.map(s -> alias + "." + s);
        }
        return stream.collect(joining(", ", "SELECT ", ""));
    }

    // ##SELECT##

    // === WHERE ===

    /**
     * Replaces current where clause.
     */
    public StatementBuilder setWhere(SqlPart where) {
        this.where = where;
        return this;
    }

    /**
     * Adds a where condition. If there already was a where condition, joins
     * them with an and.
     */
    public StatementBuilder where(SqlPart where) {
        if (this.where == null) {
            setWhere(where);
        } else {
            setWhere(Where.and(this.where, where));
        }
        return this;
    }

    /**
     * Convenience method for adding and AND clause with equals operations
     * 
     * @param property
     * @param value
     */
    public StatementBuilder where(String property, Object value) {
        return where(Where.eq(property, value));
    }

    public String getWhereClause() {
        return where == null ? null : "WHERE " + where.sql(propertyNameMapper);
    }

    // ##WHERE##

    // === GROUP ===
    public StatementBuilder group(String... columns) {
        this.group = Arrays.asList(columns);
        return this;
    }

    public String getGroupClause() {
        if (group != null && !group.isEmpty()) {
            return "GROUP BY " + StringUtils.join(group, ", ");
        } else {
            return null;
        }
    }

    // ##GROUP##

    // === ORDER ===
    /**
     * Replaces current order clause.
     */
    public StatementBuilder setOrder(OrderSql orderSql) {
        this.orderSql = orderSql;
        return this;
    }

    /**
     * Adds orders to the current order clause.
     */
    public StatementBuilder order(OrderSql orderSql) {
        if (this.orderSql == null) {
            setOrder(orderSql);
        } else {
            this.orderSql.add(orderSql);
        }
        return this;
    }

    /**
     * Adds orders to the current order clause.
     */
    public StatementBuilder order(Order... orderClauses) {
        if (this.orderSql == null) {
            this.orderSql = OrderSql.order(orderClauses);
        } else {
            this.orderSql.add(orderClauses);
        }
        return this;
    }

    /**
     * Adds asc orders to the current order clause.
     */
    public StatementBuilder asc(String... properties) {
        for (String p : properties) {
            order(Order.asc(p));
        }
        return this;
    }

    /**
     * Adds desc orders to the current order clause.
     */
    public StatementBuilder desc(String... properties) {
        for (String p : properties) {
            order(Order.desc(p));
        }
        return this;
    }

    public String getOrderClause() {
        return orderSql == null ? null : orderSql.sql(propertyNameMapper);
    }

    // ##ORDER##

    public String getFromClause() {
        StringBuilder clause = new StringBuilder("FROM ");
        if (StringUtils.isNotEmpty(table)) {
            clause.append(table);
            if (StringUtils.isNotEmpty(alias)) {
                clause.append(" ").append(alias);
            }
        } else {
            throw new IllegalStateException("from clause unspecified");
        }
        return clause.toString();
    }

    public Object[] getParams(Function<String, Object> paramMapper) {
        List<Object> params = new ArrayList<Object>();

        if (setWith != null && !setWith.isEmpty()) {
            params.addAll(SqlPropertyParam.resolvePropParams(setWith, paramMapper));
        }

        if (where != null) {
            params.addAll(where.params(paramMapper));
        }

        return params.stream().map(converter).toArray();
    }

    public String getLoad() {
        return Stream.of(getSelectClause(), getFromClause(), getWhereClause(), getGroupClause(), getOrderClause())
                     .filter(t -> t != null)
                     .collect(joining(" "));
    }

    public abstract String limitSql(Limit limit);

    public String countSql() {
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM (");
        sb.append(getLoad()).append(") t");
        return sb.toString();
    }

    public String getInsert() {
        StringBuilder expression = new StringBuilder("INSERT INTO ").append(table).append(" (");
        expression.append(setBy.stream().map(propertyNameMapper).collect(joining(", ")));
        expression.append(") VALUES (").append(StringUtils.repeat("?", ", ", setBy.size())).append(")");
        return expression.toString();
    }

    public String getUpdate() {
        StringBuilder expression = new StringBuilder("UPDATE ").append(table).append(" ");
        if (StringUtils.isNotEmpty(alias)) {
            expression.append(alias).append(" ");
        }
        expression.append("SET ");
        expression.append(setBy.stream().map(propertyNameMapper).collect(joining("=?, ", "", "=? ")));
        expression.append(getWhereClause());
        return expression.toString();
    }

    public String getDelete() {
        StringBuilder expression = new StringBuilder("DELETE FROM ").append(table).append(" ");
        if (StringUtils.isNotEmpty(alias)) {
            expression.append(alias).append(" ");
        }
        expression.append(getWhereClause());
        return expression.toString();
    }

    public <B> List<String> getWritableProps(BeanMapping<B> mapping, StatementType stmtType) {
        List<String> res = new ArrayList<String>();
        for (Property<B, Object> p : mapping.props().values()) {
            if (!p.readOnly()) {
                res.add(p.name());
            }
        }
        return res;
    }

    public void setInterceptor(StatementInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public StatementInterceptor getInterceptor() {
        return this.interceptor;
    }
}
