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

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcOperations;

import com.nortal.petit.core.util.ArgPreparedStatementSetter;

/**
 * Oracle dialect-specific sql method implementations.
 * 
 * @author <a href="mailto:toomas.parna@nortal.com">Toomas Pärna</a>
 */
public class OracleSqlDialect implements SqlDialect {

    protected final static Logger LOG = Logger.getLogger(OracleSqlDialect.class);

    @Override
    public String getSysdateBetweenClause(String startDateField, String endDateField) {
        return " sysdate between " + startDateField + " and coalesce(" + endDateField + ", sysdate)";
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> B insertReturningId(JdbcOperations jdbcOperations, String sql, String idColumn, final Object... params) {
        final String actualSql = new StringBuilder("BEGIN ").append(sql)
                .append(" RETURNING " + idColumn + " INTO ?; END;").toString();
        try {
            return (B) jdbcOperations.execute(new CallableStatementCreator() {
                @Override
                public CallableStatement createCallableStatement(Connection con) throws SQLException {
                    CallableStatement cs = con.prepareCall(actualSql);
                    ArgPreparedStatementSetter.setValues(cs, params, 1);
                    cs.registerOutParameter(params.length + 1, Types.DECIMAL);
                    return cs;
                }
            }, new CallableStatementCallback<B>() {
                @Override
                public B doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                    cs.execute();
                    BigDecimal bd = cs.getBigDecimal(params.length + 1);
                    return (B) Long.valueOf(bd.longValue());
                }
            });
        } catch (RuntimeException e) {
            LOG.error("Error processing SQL '" + sql + "' with parameters: " + StringUtils.join(params, "; "));
            throw e;
        }
    }
}
