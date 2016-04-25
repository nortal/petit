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
package com.nortal.petit.core;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import com.nortal.petit.core.dialect.SqlDialect;
import com.nortal.petit.core.model.Id;
import com.nortal.petit.core.sql.SqlBuilder;

/**
 * Helper class for using various Database related functionalities.
 * Uses the {@link JdbcTemplate} class from {@link SqlConfiguration}
 * 
 * @author Alrik Peets
 */
public class SqlSupport {

    private SqlConfiguration sqlConfiguration;

    public SqlSupport(SqlConfiguration sqlConfiguration) {
        this.sqlConfiguration = sqlConfiguration;
    }

    protected JdbcOperations getJdbcOperations() {
        return sqlConfiguration.getJdbcOperations();
    }

    protected JdbcTemplate getJdbcTemplate() {
        return sqlConfiguration.getJdbcTemplate();
    }

    protected SqlDialect getSqlDialect() {
        return sqlConfiguration.getSqlDialect();
    }

    // ==================== SQL Helper methods =======================

    public int update(String sql, Object... params) {
        return getJdbcTemplate().update(sql, params);
    }

    public int update(SqlBuilder sqlBuilder) {
        return getJdbcTemplate().update(sqlBuilder.getSql(), sqlBuilder.getParams());
    }

    /**
     * Queries for a single string object.
     * 
     * @return null when no result is provided, exception is thrown when more
     *         that 1 result is provided
     */
    public <T> T getObject(String sql, RowMapper<T> rowMapper, Object... args) {
        try {
            return getJdbcTemplate().queryForObject(sql, rowMapper, args);
        } catch (IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0) {
                return null;
            }
            // else
            throw e;
        }
    }

    public <T> T getObject(SqlBuilder builder, RowMapper<T> rowMapper) {
        return getObject(builder.getSql(), rowMapper, builder.getParams());
    }

    public String getString(String sql, Object... args) {
        return getPrimitiveObject(sql, String.class, args);
    }

    public String getString(SqlBuilder sqlBuilder) {
        return getString(sqlBuilder.getSql(), sqlBuilder.getParams());
    }

    public String getStringById(String tableName, String columnName, long id) {
        return getColumnById(tableName, columnName, id, String.class);
    }

    public Date getDateById(String tableName, String columnName, long id) {
        return getColumnById(tableName, columnName, id, Date.class);
    }

    public Long getLongById(String tableName, String columnName, long id) {
        return getColumnById(tableName, columnName, id, Long.class);
    }

    public <T> T getColumnById(String tableName, String columnName, long id, Class<T> clazz) {
        return getPrimitiveObject("SELECT " + columnName + " FROM " + tableName + " WHERE id = ?", clazz, id);
    }

    public boolean getBoolean(SqlBuilder sqlBuilder) {
        return getBoolean(sqlBuilder.getSql(), sqlBuilder.getParams());
    }

    public Boolean getBooleanOrNull(String sql, Object... args) {
        return getPrimitiveObject(sql, Boolean.class, args);
    }

    public boolean getBoolean(String sql, Object... args) {
        Boolean value = getPrimitiveObject(sql, Boolean.class, args);

        return value != null && value;
    }

    /**
     * Queries for a single date object.
     * 
     * @return null when no result is provided, exception is thrown when more
     *         that 1 result is provided
     */
    public Date getDate(SqlBuilder builder) {
        return getDate(builder.getSql(), builder.getParams());
    }

    /**
     * Queries for a single date object.
     * 
     * @return null when no result is provided, exception is thrown when more
     *         that 1 result is provided
     */
    public Date getDate(String sql, Object... args) {
        return getPrimitiveObject(sql, Date.class, args);
    }

    public Timestamp getTimestamp(String sql, Object... args) {
        return getPrimitiveObject(sql, Timestamp.class, args);
    }

    /**
     * Queries for a single long object.
     * 
     * @return null when no result is provided, exception is thrown when more
     *         that 1 result is provided
     */
    public Long getLong(String sql, Object... args) {
        return getPrimitiveObject(sql, Long.class, args);
    }

    public Long getLong(SqlBuilder sb) {
        return getLong(sb.getSql(), sb.getParams());
    }

    public <T> Id<T> getId(SqlBuilder sb) {
        return getId(sb.getSql(), sb.getParams());
    }

    /**
     * @param clazz
     *            passed here to match type param of Id
     */
    public <T> Id<T> getId(String sql, Object... args) {
        return Id.create(getPrimitiveObject(sql, Long.class, args));
    }

    /**
     * Queries for a single Integer object.
     * 
     * @return null when no result is provided, exception is thrown when more
     *         that 1 result is provided
     */
    public Integer getInteger(String sql, Object... args) {
        return getPrimitiveObject(sql, Integer.class, args);
    }

    public Integer getInteger(SqlBuilder sb) {
        return getInteger(sb.getSql(), sb.getParams());
    }

    public BigDecimal getBigDecimal(SqlBuilder sb) {
        return getBigDecimal(sb.getSql(), sb.getParams());
    }

    /**
     * Queries for a single {@link BigDecimal} object.
     * 
     * @return null when no result is provided, exception is thrown when more
     *         that 1 result is provided
     */
    public BigDecimal getBigDecimal(String sql, Object... args) {
        return getPrimitiveObject(sql, BigDecimal.class, args);
    }

    /**
     * @param <K>
     *            type of key
     * @param <V>
     *            type of value
     * @param sql
     *            query must return result set of two columns, where first will
     *            be treated as key
     * @return map reflecting result of given query
     */
    public <K, V> Map<K, V> getMap(final Class<K> keyType, final Class<V> valueType, String sql, Object... args) {
        return getJdbcTemplate().query(sql, args, new ResultSetExtractor<Map<K, V>>() {
            @Override
            public Map<K, V> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<K, V> result = new LinkedHashMap<K, V>();
                while (rs.next()) {
                    K key = (K) JdbcUtils.getResultSetValue(rs, 1, keyType);
                    V value = (V) JdbcUtils.getResultSetValue(rs, 2, valueType);
                    result.put(key, value);
                }
                return result;
            }
        });
    }

    public <K, V> Map<K, V> getMap(final Class<K> keyType, final Class<V> valueType, SqlBuilder sql) {
        return getMap(keyType, valueType, sql.getSql(), sql.getParams());
    }

    public <T> List<T> find(SqlBuilder builder, RowMapper<T> mapper) {
        return find(builder.getSql(), mapper, builder.getParams());
    }

    public <T> List<T> find(String sql, RowMapper<T> rm, Object... args) {
        return getJdbcTemplate().query(sql, rm, args);
    }

    public void find(SqlBuilder builder, RowCallbackHandler rch) {
        find(builder.getSql(), rch, builder.getParams());
    }

    public void find(String sql, RowCallbackHandler rch, Object... args) {
        getJdbcTemplate().query(sql, args, rch);
    }

    /**
     * Inserts data into db and returns id generated for row
     * 
     * @param sql
     *            Insert sql
     * @param idColumn
     *            Primary key column name
     * @param params
     *            Parameters inserted
     * @return
     */
    public <B> B insertReturningId(String sql, String idColumn, Object... params) {
        return getSqlDialect().insertReturningId(getJdbcOperations(), sql, idColumn, params);
    }

    public <T> List<T> queryForList(SqlBuilder sql, Class<T> clazz) {
        return getJdbcTemplate().queryForList(sql.getSql(), clazz, sql.getParams());
    }

    /**
     * If columns are omitted, then neither SELECT clause nor FROM is prepended.
     * If customWhere == null then no where clause is appended
     */
    public static SqlBuilder createSqlBuilder(String columns, String from, SqlBuilder customWhere, String orderBy) {
        SqlBuilder sb = new SqlBuilder("");
        if (StringUtils.isNotBlank(columns)) {
            sb.append("SELECT ");
            sb.append(columns);
            sb.append(" FROM ");
        }
        sb.append(from);
        if (customWhere != null) {
            sb.appendWhere(customWhere.getSql());
            sb.addParams(customWhere.getParams());
        }
        if (StringUtils.isNotBlank(orderBy)) {
            sb.appendOrderBy(orderBy);
        }
        return sb;
    }

    /**
     * @return null when no result is provided, exception is thrown when more
     *         that 1 result is provided
     */
    private <T> T getPrimitiveObject(String sql, Class<T> clazz, Object... args) {
        try {
            return getJdbcTemplate().queryForObject(sql, clazz, args);
        } catch (IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0) {
                return null;
            }
            // else
            throw e;
        }
    }

}
