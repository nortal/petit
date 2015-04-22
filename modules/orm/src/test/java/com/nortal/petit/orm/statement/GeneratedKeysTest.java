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
import static org.hamcrest.Matchers.equalTo;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.nortal.petit.orm.MockrunnerBaseTest;
import com.nortal.petit.orm.statement.model.SimpleBean;
import com.nortal.petit.orm.statement.model.SimpleStringBean;

public class GeneratedKeysTest extends MockrunnerBaseTest {

    @Before
    public void setup() {
        super.setup();
    }
    
    @Test
    public void test__generatedLongIsMappedBackOK() {
        prepareResultsetReturningLong();
        
        SimpleBean sb = new SimpleBean();
        sb.setDescription("test description");
        ss.insert(sb);
        
        assertThat(sb.getId(), equalTo(Long.valueOf(11L)));
    }

    @Test
    public void test__generatedBigDecimalIsMappedBackOK() {
        prepareResultsetReturningBigDecimal();
        
        SimpleBean sb = new SimpleBean();
        sb.setDescription("test description");
        ss.insert(sb);
        
        assertThat(sb.getId(), equalTo(Long.valueOf(11L)));
    }
    
    @Test
    public void test__generatedStringIsMappedBackOK() {
        prepareResultsetReturningString();
        
        SimpleStringBean ssb = new SimpleStringBean();
        ssb.setDescription("test description");
        ss.insert(ssb);
        
        assertThat(ssb.getKood(), equalTo("1234"));
    }
    
    private void prepareResultsetReturningLong() {
        MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
        PreparedStatementResultSetHandler statementHandler = connection.getPreparedStatementResultSetHandler();
        MockResultSet keysResult = statementHandler.createResultSet();
        keysResult.addColumn("id");
        keysResult.addRow(new Object[] {Long.valueOf(11L)});
        
        statementHandler.prepareGeneratedKeys("insert", keysResult);
    }
    
    private void prepareResultsetReturningBigDecimal() {
        MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
        PreparedStatementResultSetHandler statementHandler = connection.getPreparedStatementResultSetHandler();
        MockResultSet keysResult = statementHandler.createResultSet();
        keysResult.addColumn("id");
        keysResult.addRow(new Object[] {BigDecimal.valueOf(11L)});
        
        statementHandler.prepareGeneratedKeys("insert", keysResult);
    }
    
    private void prepareResultsetReturningString() {
        MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
        PreparedStatementResultSetHandler statementHandler = connection.getPreparedStatementResultSetHandler();
        MockResultSet keysResult = statementHandler.createResultSet();
        keysResult.addColumn("kood");
        keysResult.addRow(new Object[] {"1234"});
        
        statementHandler.prepareGeneratedKeys("insert", keysResult);
    }
}
