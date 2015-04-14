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
package com.nortal.petit.orm.mapper;

import static com.nortal.petit.orm.statement.clause.Where.eq;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.core.dialect.OracleSqlDialect;
import com.nortal.petit.core.dialect.SqlDialect;
import com.nortal.petit.orm.DefaultBeanPropertyConverter;
import com.nortal.petit.orm.StatementConfiguration;
import com.nortal.petit.orm.StatementSupport;
import com.nortal.petit.orm.mapper.fixture.MapperTestBean;
import com.nortal.petit.orm.statement.LoadStatement;
import com.nortal.petit.orm.statement.OracleStatementBuilder;
import com.nortal.petit.orm.statement.StatementBuilder;
import com.nortal.petit.orm.statement.clause.Where;

@RunWith(MockitoJUnitRunner.class)
public class BeanMapperTest extends BasicJDBCTestCaseAdapter {

    private StatementSupport ss;
    private MapperTestBean mtb;

    @Before
    public void setup() {
        ss = new StatementSupport(new MockStatementConfiguration());

        prepareResultSet();
    }

    // @Test
    public void test__beanMapperSimpleMapper() {
        MapperTestBean loaded = ss.loadById(MapperTestBean.class, 1L);

        // System.out.println(getExecutedSQLStatements());

        verifyNumberPreparedStatements(1);

        assertThat(loaded, is(notNullValue()));
        assertThat(loaded.getId(), equalTo(1L));
        assertThat(loaded.getOptional(), is(nullValue()));
    }

    // @Test
    public void test__beanMapperExtendedMapper() {
        LoadStatement<MapperTestBean> loadStm = ss.loadStm(MapperTestBean.class).where(eq("id", 1L));
        loadStm.getMapper().add("optional", "optional");
        MapperTestBean single = loadStm.single();

        verifyNumberPreparedStatements(1);

        assertThat(single, is(notNullValue()));
        assertThat(single.getId(), equalTo(1L));
        assertThat(single.getOptional(), equalTo("Optional result"));
    }

    @Test
    public void test__thatExtendedIsLimitedToSingleStatementExecutions() {
        MapperTestBean loaded = ss.loadStm(MapperTestBean.class).where(eq("id", 1L)).single();
        assertThat(loaded.getOptional(), is(nullValue()));

        LoadStatement<MapperTestBean> loadStm = ss.loadStm(MapperTestBean.class).where(eq("id", 1L));
        loadStm.getMapper().add("optional", "optional");
        MapperTestBean loaded2 = loadStm.single();
        assertThat(loaded2.getOptional(), equalTo("Optional result"));

        MapperTestBean loaded3 = ss.loadById(MapperTestBean.class, 1L);
        assertThat(loaded3.getOptional(), is(nullValue()));
    }

    @Test
    public void test__thatBeanMappingsAreNestedCorrectly() {
        LoadStatement<MapperTestBean> loadStm = ss.loadStm(MapperTestBean.class).where(eq("id", 1L));
//        MapperTestBean mtb = loadStm.single();
//        assertThat(mtb.getOptional(), is(nullValue()));
//        assertThat(mtb.getDescription(), is(notNullValue()));
        
        loadStm.getMapper().add("optional", "optional");
        MapperTestBean mtb2 = loadStm.single();
        assertThat(mtb2.getOptional(), is(notNullValue()));
        assertThat(mtb2.getDescription(), is(notNullValue()));
        
        loadStm.select("id", "optional");
        MapperTestBean mtb3 = loadStm.single();
        System.out.println(getExecutedSQLStatements());

        assertThat(mtb3.getId(), is(notNullValue()));
        assertThat(mtb3.getDescription(), is(nullValue()));
        assertThat(mtb3.getOptional(), is(notNullValue()));
        
    }
    
    
    private void prepareResultSet()
    {
        MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
        PreparedStatementResultSetHandler statementHandler = connection.getPreparedStatementResultSetHandler();
        MockResultSet result = statementHandler.createResultSet();
        result.addColumn("id");
        result.addColumn("description");
        result.addColumn("optional");
        result.addRow(new Object[] { 1L, "Bean description", "Optional result" });
        statementHandler.prepareResultSet("select", result, new Object[] { 1L });
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
