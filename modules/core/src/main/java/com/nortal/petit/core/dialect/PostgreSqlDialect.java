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
package com.nortal.petit.core.dialect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.nortal.petit.core.util.ArgPreparedStatementSetter;

/**
 * PostgreSQL dialect-specific sql method implementations.
 * 
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com)
 * 
 */
public class PostgreSqlDialect implements SqlDialect {
    @Override
    public String getSysdateBetweenClause(String startDateField, String endDateField) {
        return " now() BETWEEN coalesce(" + startDateField + ", now()) AND coalesce(" + endDateField + ", now()) ";
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> B insertReturningId(JdbcOperations jdbcOperations, final String sql, String idColumn,
            final Object... params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                Object[] queryParams = params.length == 1 && params[0] instanceof Object[] ? (Object[]) params[0]
                        : params;
                ArgPreparedStatementSetter.setValues(ps, queryParams, 1);
                return ps;
            }
        }, keyHolder);
        return (B) keyHolder.getKeys().get(idColumn);
    }
}
