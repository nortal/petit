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

import org.hamcrest.Matchers;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.nortal.petit.orm.BeanMappers;
import com.nortal.petit.orm.statement.fixture.LoadStatementFixture.LoadStmtBean;

/**
 * 
 * 
 *
 */
//@RunWith(MockitoJUnitRunner.class)
public class LoadStatementTest {
    
//    @Mock
    private JdbcTemplate jdbcTemplate;
    
//    @InjectMocks
    private LoadStatement loadStmt;
    
    private <B> LoadStatement<B> load(JdbcTemplate jdbcTemplate, Class<B> type) {
        return new LoadStatement<B>(jdbcTemplate == null ? new JdbcTemplate() : jdbcTemplate, new OracleStatementBuilder(), type);
    }
    
    
    public void testSingleLoad() {
        Object[] params = new Object[]{};
        Mockito
            .when(jdbcTemplate.queryForObject("SELECT id, description FROM load_tbl", BeanMappers.get(LoadStmtBean.class), params))
            .thenReturn(new LoadStmtBean());
        
        LoadStatement<LoadStmtBean> load = load(jdbcTemplate, LoadStmtBean.class);
    }
    
}
