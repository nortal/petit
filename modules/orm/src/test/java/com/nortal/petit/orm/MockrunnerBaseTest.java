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

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.nortal.petit.core.dialect.OracleSqlDialect;
import com.nortal.petit.core.dialect.SqlDialect;
import com.nortal.petit.orm.DefaultBeanPropertyConverter;
import com.nortal.petit.orm.StatementConfiguration;
import com.nortal.petit.orm.StatementSupport;
import com.nortal.petit.orm.statement.OracleStatementBuilder;
import com.nortal.petit.orm.statement.StatementBuilder;

/**
 * Sets up {@link StatementSupport} enhanced with mockrunner capabilities.
 * Call {@link #setup()} before executing tests to initialize {@link StatementSupport}
 * 
 * @author Alrik Peets
 */
public class MockrunnerBaseTest extends BasicJDBCTestCaseAdapter {

    protected StatementSupport ss;
    
    public void setup() {
        ss = new StatementSupport(new MockStatementConfiguration());
    }
    
    
    private class MockStatementConfiguration implements StatementConfiguration {
        @Override
        public JdbcTemplate getJdbcTemplate() {
            return new JdbcTemplate(getJDBCMockObjectFactory().getMockDataSource());
        }

        @Override
        public JdbcOperations getJdbcOperations() {
            return new JdbcTemplate(getJDBCMockObjectFactory().getMockDataSource());
        }

        @Override
        public SqlDialect getSqlDialect() {
            return new OracleSqlDialect();
        }

        @Override
        public StatementBuilder getStatementBuilder() {
            StatementBuilder statementBuilder = new OracleStatementBuilder();
            statementBuilder.setConverter(new DefaultBeanPropertyConverter());
            return statementBuilder;
        }

    }

}
