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
package com.nortal.petit.orm;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.nortal.petit.converter.columnreader.StandardStrategies;
import com.nortal.petit.converter.util.ColumnReaderRowMapper;
import com.nortal.petit.core.SqlSupport;
import com.nortal.petit.core.model.Id;
import com.nortal.petit.core.sql.SqlBuilder;

/**
 * @author Aleksei Lissitsin
 * 
 */
public class BeanSqlSupport {
    private SqlSupport sqlSupport;

    public BeanSqlSupport(SqlSupport sqlSupport) {
        this.sqlSupport = sqlSupport;
    }

    // =========== Convenience methods for using SqlBuilder =========

    /**
     * @param clazz
     *            bean entity class. A row mapper will be created in accordance
     *            with {@link BeanRowMapper}.
     * @return null when no result is provided, exception is thrown when more
     *         that 1 result is provided
     */
    public <T> T getObject(String sql, Class<T> clazz, Object... args) {
        return sqlSupport.getObject(sql, BeanMappers.get(clazz), args);
    }

    public <T> T getObject(SqlBuilder sql, Class<T> clazz) {
        return sqlSupport.getObject(sql.getSql(), BeanMappers.get(clazz), sql.getParams());
    }

    public <T> List<T> find(SqlBuilder builder, Class<T> clazz) {
        return sqlSupport.find(builder.getSql(), BeanMappers.get(clazz), builder.getParams());
    }

    public <T> List<Id<T>> findId(SqlBuilder sql) {
        return findId(sql.getSql(), sql.getParams());
    }

    /**
     * @param clazz
     *            passed here to match type param of Id
     */
    public <T> List<Id<T>> findId(String sql, Object... args) {
        RowMapper<Id<T>> rm = new ColumnReaderRowMapper<>(new StandardStrategies.IdColumnRetrievalStrategy<T>());

        return sqlSupport.find(sql, rm, args);
    }
}
