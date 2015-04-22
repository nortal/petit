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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.nortal.petit.core.dialect.OracleSqlDialect;
import com.nortal.petit.core.dialect.SqlDialect;
import com.nortal.petit.orm.DefaultBeanPropertyConverter;
import com.nortal.petit.orm.MockrunnerBaseTest;
import com.nortal.petit.orm.StatementConfiguration;
import com.nortal.petit.orm.StatementSupport;
import com.nortal.petit.orm.statement.interceptor.StatementInterceptor;
import com.nortal.petit.orm.statement.model.SimpleBean;

public class InsertStatementInterceptorTest extends MockrunnerBaseTest {

	private TestStatementInterceptor interceptor = new TestStatementInterceptor();
	
	@Before
	public void setup() {
        ss = new StatementSupport(new MockInterceptorStatementConfiguration());
	}
	
	@Test
	public void test__thatInsertedBeansDataIsLogged() {
		prepareResultsetReturningMultipleKeys();
		
		List<SimpleBean> beans = new ArrayList<>();
		beans.add(new SimpleBean(null, "Esimene bean"));
		beans.add(new SimpleBean(null, "Teine bean"));
		beans.add(new SimpleBean(null, "Kolmas bean"));
		
		ss.insert(beans);
		
		assertThat(beans, is(not(empty())));
		assertThat(beans.size(), equalTo(3));
		SimpleBean sb2 = beans.get(1);
		assertThat(sb2, is(notNullValue()));
		assertThat(sb2.getId(), equalTo(12L));
		
		//Check interceptor result
		assertThat(interceptor.getInsertData(), is(not(empty())));
		assertThat(interceptor.getInsertData().size(), equalTo(3));
		
		StatementInfoHolder info = interceptor.getInsertData().get(0);
		assertThat(info.getTable(), equalTo("simple_table"));
		assertThat(info.getId(), equalTo((Object)11L));
		assertThat(info.getCurrentState(), is(notNullValue()));
	}

	private void prepareResultsetReturningMultipleKeys() {
        MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
        PreparedStatementResultSetHandler statementHandler = connection.getPreparedStatementResultSetHandler();
        MockResultSet keysResult = statementHandler.createResultSet();
        keysResult.addColumn("id");
        keysResult.addRow(new Object[] {BigDecimal.valueOf(11L)});
        keysResult.addRow(new Object[] {BigDecimal.valueOf(12L)});
        keysResult.addRow(new Object[] {BigDecimal.valueOf(13L)});
        
        statementHandler.prepareGeneratedKeys("insert", keysResult);
	}

    private class MockInterceptorStatementConfiguration implements StatementConfiguration {
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
            statementBuilder.setInterceptor(interceptor);
            return statementBuilder;
        }

    }
	
    private class TestStatementInterceptor implements StatementInterceptor {
    	List<StatementInfoHolder> insertData = new ArrayList<>();
    	List<StatementInfoHolder> updateData = new ArrayList<>();
    	
    	public List<StatementInfoHolder> getInsertData() {
    		return insertData;
    	}
    	
    	public List<StatementInfoHolder> getUpdateData() {
    		return updateData;
    	}
    	
		@Override
		public void afterUpdate(String table, Object id, Object[] currentState, Object[] previuosState, String[] columnNames) {
			updateData.add(new StatementInfoHolder(table, id, currentState, previuosState, columnNames));
		}

		@Override
		public void afterInsert(String table, Object id, Object[] currentState, String[] columnNames) {
			insertData.add(new StatementInfoHolder(table, id, currentState, null, columnNames));
		}
    	
    }

    private class StatementInfoHolder {
    	private String table;
    	private Object id;
    	private Object[] currentState;
    	private Object[] previousState;
    	private String[] columnNames;
    	
		public StatementInfoHolder(String table, Object id,
				Object[] currentState, Object[] previousState,
				String[] columnNames) {
			super();
			this.table = table;
			this.id = id;
			this.currentState = currentState;
			this.previousState = previousState;
			this.columnNames = columnNames;
		}
		public String getTable() {
			return table;
		}
		public void setTable(String table) {
			this.table = table;
		}
		public Object getId() {
			return id;
		}
		public void setId(Object id) {
			this.id = id;
		}
		public Object[] getCurrentState() {
			return currentState;
		}
		public void setCurrentState(Object[] currentState) {
			this.currentState = currentState;
		}
		public Object[] getPreviousState() {
			return previousState;
		}
		public void setPreviousState(Object[] previousState) {
			this.previousState = previousState;
		}
		public String[] getColumnNames() {
			return columnNames;
		}
		public void setColumnNames(String[] columnNames) {
			this.columnNames = columnNames;
		}
    }
    
}
