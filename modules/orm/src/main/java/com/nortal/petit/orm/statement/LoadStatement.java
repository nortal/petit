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

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.DefaultLobHandler;

import com.nortal.petit.beanmapper.RestrictedBeanMapping;
import com.nortal.petit.converter.config.ConverterConfig;
import com.nortal.petit.orm.BeanMapper;
import com.nortal.petit.orm.statement.clause.Limit;
import com.nortal.petit.orm.statement.clause.Order;
import com.nortal.petit.orm.statement.clause.OrderSql;
import com.nortal.petit.orm.statement.clause.SelectClause;
import com.nortal.petit.orm.statement.clause.SqlPart;
import com.nortal.petit.orm.statement.clause.WhereClause;

/**
 * @author Aleksei Lissitsin <aleksei.lissitsin@webmedia.ee>
 * @date 13.02.2013 - Lauri Lättemäe (lauri.lattemae@nortal.com) Made abstract
 *       because of limiting load result
 */
public class LoadStatement<B> extends SimpleStatement<B> implements SelectClause<LoadStatement<B>>,
        WhereClause<LoadStatement<B>> {
    public LoadStatement(JdbcOperations jdbcTemplate, StatementBuilder statementBuilder, Class<B> beanClass) {
        super.init(jdbcTemplate, statementBuilder, beanClass);
    }

    @Override
    public LoadStatement<B> select(String... properties) {
        getStatementBuilder().select(properties);
        updateMapper(new BeanMapper<B>(new RestrictedBeanMapping<B>(getMapping(), properties),
                ConverterConfig.instance().getPropertyReader()));
        return this;
    }

    @Override
    public LoadStatement<B> where(String property, Object value) {
        getStatementBuilder().where(property, value);
        return this;
    }

    @Override
    public LoadStatement<B> where(SqlPart where) {
        getStatementBuilder().where(where);
        return this;
    }

    public LoadStatement<B> order(OrderSql orderSql) {
        getStatementBuilder().order(orderSql);
        return this;
    }

    public LoadStatement<B> order(Order... orderClauses) {
        getStatementBuilder().order(orderClauses);
        return this;
    }

    @Override
    protected void prepare() {
        getStatementBuilder().setPropertyNameMapper(StatementUtil.getPropertyNameMapper(getMapping(), true));
        // If no specific select is set select all props
        if (!getStatementBuilder().isSetSelect()) {
            getStatementBuilder().select(StatementUtil.toStringArray(getMapping().props().keySet()));
        }
        super.setSql(getStatementBuilder().getLoad());
    }

    /**
     * Return the first item from matching list.
     * Returns <code>null</code> if no result.
     * 
     * @return
     */
    public B first() {
        try {
            prepare();
            return getJdbcTemplate().queryForObject(getStatementBuilder().limitSql(new Limit(0, 1)), getMapper(),
                    getParams(null));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Query for single object. Will return <code>null</code> if not element
     * found.
     * Throws {@link IncorrectResultSizeDataAccessException} if more than one
     * element returned.
     * 
     * @return
     */
    public B single() {
        try {
            prepare();
            return getJdbcTemplate().queryForObject(getSql(), getMapper(), getParams(null));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Query for single object.
     * Throws {@link IncorrectResultSizeDataAccessException} if not exactly one
     * element returned.
     * 
     * @return
     */
    public B require() {
        prepare();
        return getJdbcTemplate().queryForObject(getSql(), getMapper(), getParams(null));
    }

    public List<B> range(int start, int limit) {
        prepare();
        return getJdbcTemplate().query(getStatementBuilder().limitSql(new Limit(start, limit)), getMapper(),
                getParams(null));
    }

    public List<B> all() {
        return getJdbcTemplate().query(getSql(), getMapper(), getParams(null));
    }

    public byte[] getBlobAsBytes() {
        return (byte[]) getJdbcTemplate().queryForObject(getSql(), getParams(null), new RowMapper<byte[]>() {
            @Override
            public byte[] mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new DefaultLobHandler().getBlobAsBytes(rs, 1);
            }
        });
    }

    public InputStream getBlobAsBinaryStream(final String columnName) {
        return (InputStream) getJdbcTemplate().queryForObject(getSql(), getParams(null), new RowMapper<InputStream>() {
            @Override
            public InputStream mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new DefaultLobHandler().getBlobAsBinaryStream(rs, columnName);
            }
        });
    }

    @Override
    protected StatementType getStatementType() {
        return StatementType.SELECT;
    }
}
