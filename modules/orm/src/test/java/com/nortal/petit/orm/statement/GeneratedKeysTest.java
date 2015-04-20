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
