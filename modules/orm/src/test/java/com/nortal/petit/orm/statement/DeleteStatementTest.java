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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.nortal.petit.orm.statement.clause.Where;
import com.nortal.petit.orm.statement.fixture.UpdateStatementFixture;
import com.nortal.petit.orm.statement.fixture.UpdateStatementFixture.UpdateStmtBean;

public class DeleteStatementTest {
    
    @Test
    public void test__sqlCreatedEqual() {
        UpdateStmtBean bean = UpdateStatementFixture.getDefaultBean();
        bean.setDescription("DESC");
        
        DeleteStatement<UpdateStmtBean> update1 = new DeleteStatement<UpdateStmtBean>(new JdbcTemplate(), new OracleStatementBuilder(), bean);
        update1.setBy("description").where(Where.eq("id", 1L));
        
        DeleteStatement<UpdateStmtBean> update2 = new DeleteStatement<UpdateStmtBean>(new JdbcTemplate(), new OracleStatementBuilder(), UpdateStmtBean.class);
        update2.setBy("description").where(Where.eq("id", 1L)).setBeans(Arrays.asList(bean));
        
        System.out.println(update1.getSqlWithParams());
        System.out.println(update2.getSqlWithParams());
        
        MatcherAssert.assertThat(update1.getSqlWithParams(), Matchers.equalTo(update2.getSqlWithParams()));
    }
    
    @Test
    public void test__impliedSqlCreatedEqual() {
        UpdateStmtBean bean = UpdateStatementFixture.getDefaultBean();
        bean.setId(1L);
        bean.setDescription("DESC");
        
        
        DeleteStatement<UpdateStmtBean> update1 = new DeleteStatement<UpdateStmtBean>(new JdbcTemplate(), new OracleStatementBuilder(), bean);
        update1.setBy("description");
        
        DeleteStatement<UpdateStmtBean> update2 = new DeleteStatement<UpdateStmtBean>(new JdbcTemplate(), new OracleStatementBuilder(), UpdateStmtBean.class);
        update2.setBy("description").setBeans(Arrays.asList(bean));

        DeleteStatement<UpdateStmtBean> update3 = new DeleteStatement<UpdateStmtBean>(new JdbcTemplate(), new OracleStatementBuilder(), bean);
        update3.setBy("description").where(Where.eq("id", 1L));
        
        
        System.out.println(update1.getSqlWithParams());
        System.out.println(update2.getSqlWithParams());
        System.out.println(update3.getSqlWithParams());
        
        MatcherAssert.assertThat(update1.getSqlWithParams(), Matchers.equalTo(update2.getSqlWithParams()));
        MatcherAssert.assertThat(update1.getSqlWithParams(), Matchers.equalTo(update3.getSqlWithParams()));
    }

    @Test
    public void test__impliedSqlCreatedEqualForList() {
        List<UpdateStmtBean> beans = new ArrayList<>();
        UpdateStmtBean bean1 = UpdateStatementFixture.getDefaultBean();
        bean1.setId(1L);
        bean1.setDescription("DESC");
        beans.add(bean1);
        
        UpdateStmtBean bean2 = UpdateStatementFixture.getDefaultBean();
        bean2.setId(2L);
        bean2.setDescription("DESC");
        beans.add(bean2);
        
        DeleteStatement<UpdateStmtBean> update1 = new DeleteStatement<UpdateStmtBean>(new JdbcTemplate(), new OracleStatementBuilder(), beans.toArray(new UpdateStmtBean[]{}));
        update1.setBy("description");
        
        DeleteStatement<UpdateStmtBean> update2 = new DeleteStatement<UpdateStmtBean>(new JdbcTemplate(), new OracleStatementBuilder(), UpdateStmtBean.class);
        update2.setBy("description").setBeans(beans);

        
        System.out.println(update1.getSqlWithParams());
        System.out.println(update2.getSqlWithParams());
        
        MatcherAssert.assertThat(update1.getSqlWithParams(), Matchers.equalTo(update2.getSqlWithParams()));
    }
    
}
