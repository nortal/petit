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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.nortal.petit.beanmapper.BeanMappingFactoryTest;
import com.nortal.petit.beanmapper.BeanMappings;
import com.nortal.petit.beanmapper.BeanMappingFactoryTest.Bean;
import com.nortal.petit.beanmapper.BeanMappingFactoryTest.EmBean;
import com.nortal.petit.orm.statement.DeleteStatement;
import com.nortal.petit.orm.statement.InsertStatement;
import com.nortal.petit.orm.statement.LoadStatement;
import com.nortal.petit.orm.statement.MappingParamFunction;
import com.nortal.petit.orm.statement.OracleStatementBuilder;
import com.nortal.petit.orm.statement.PostgreStatementBuilder;
import com.nortal.petit.orm.statement.StatementUtil;
import com.nortal.petit.orm.statement.UpdateStatement;

/**
 * @author Aleksei Lissitsin
 * 
 */
public class StatementsTest {

    @BeforeClass
    public static void before() {
        StatementUtil.sort = true;
    }

    private <B> LoadStatement<B> load(Class<B> type) {
        return new LoadStatement<B>(new JdbcTemplate(), new OracleStatementBuilder(), type);
    }

    @Test
    public void testInsert2() {

        Bean b = new Bean();
        b.setSuperProp(1L);
        b.setSuperProp(5L);

        InsertStatement<Bean> i1 = insert(b);
        InsertStatement<Bean> i2 = new InsertStatement<BeanMappingFactoryTest.Bean>(new JdbcTemplate(),
                new PostgreStatementBuilder(), b);

        String sql1 = i1.getSql();
        String sql2 = i2.getSql();

        String sql3 = i1.getSqlWithParams();
        String sql4 = i2.getSqlWithParams();

        System.out.println("OHO");
    }

    @Test
    public void testLoad() {
        Assert.assertEquals("SELECT em_f_bar, over_foo, em_classover_kar, f_id_field, g_super_prop FROM bean",
                load(BeanMappingFactoryTest.Bean.class).getSql());

        LoadStatement<Bean> l2 = load(BeanMappingFactoryTest.Bean.class).where("idField", "3");
        Assert.assertEquals(
                "SELECT em_f_bar, over_foo, em_classover_kar, f_id_field, g_super_prop FROM bean WHERE f_id_field = ?",
                l2.getSql());
        Assert.assertArrayEquals(new Object[] { "3" }, l2.getParams(null));

        Assert.assertEquals(
                "SELECT em_f_bar, over_foo, em_classover_kar, f_id_field, g_super_prop FROM bean WHERE f_id_field = '3';",
                l2.getSqlWithParams());

    }

    @SuppressWarnings("unchecked")
    private <B> InsertStatement<B> insert(B bean) {
        return new InsertStatement<B>(new JdbcTemplate(), new OracleStatementBuilder(), bean);
    }

    @Test
    public void testInsert() {
        BeanMappingFactoryTest.Bean bean = new BeanMappingFactoryTest.Bean();
        bean.setIdField(2L);
        InsertStatement<Bean> i1 = insert(bean);
        Assert.assertEquals(
                "INSERT INTO bean (em_f_bar, over_foo, em_classover_kar, f_id_field, g_super_prop) VALUES (?, ?, ?, ?, ?)",
                i1.getSql());
        MappingParamFunction<Bean> function = new MappingParamFunction<Bean>(
                BeanMappings.get(BeanMappingFactoryTest.Bean.class));
        function.setBean(bean);
        Assert.assertArrayEquals(new Object[] { null, null, null, 2L, null }, i1.getParams(function));

        bean.setEmb(new EmBean());
        bean.getEmb().setKar(3L);

        Assert.assertArrayEquals(new Object[] { null, null, 3L, 2L, null }, i1.getParams(function));

        Assert.assertEquals(
                "INSERT INTO bean (em_f_bar, over_foo, em_classover_kar, f_id_field, g_super_prop) VALUES (null, null, '3', '2', null);\n",
                i1.getSqlWithParams());
    }

    @SuppressWarnings("unchecked")
    private <B> UpdateStatement<B> update(B bean) {
        return new UpdateStatement<B>(new JdbcTemplate(), new OracleStatementBuilder(), bean);
    }

    @Test
    public void testUpdate() {
        BeanMappingFactoryTest.Bean bean = new BeanMappingFactoryTest.Bean();
        bean.setIdField(2L);
        UpdateStatement<Bean> i1 = update(bean);
        Assert.assertEquals(
                "UPDATE bean SET em_f_bar=?, over_foo=?, em_classover_kar=?, f_id_field=?, g_super_prop=? WHERE f_id_field = ?",
                i1.getSql());
        MappingParamFunction<Bean> function = new MappingParamFunction<Bean>(
                BeanMappings.get(BeanMappingFactoryTest.Bean.class));
        function.setBean(bean);
        Assert.assertArrayEquals(new Object[] { null, null, null, 2L, null, 2L }, i1.getParams(function));

        bean.setEmb(new EmBean());
        bean.getEmb().setKar(3L);

        Assert.assertArrayEquals(new Object[] { null, null, 3L, 2L, null, 2L }, i1.getParams(function));

        Assert.assertEquals(
                "UPDATE bean SET em_f_bar=null, over_foo=null, em_classover_kar='3', f_id_field='2', g_super_prop=null WHERE f_id_field = '2';\n",
                i1.getSqlWithParams());
    }

    @Test
    public void testUpdateSetBy() {
        BeanMappingFactoryTest.Bean bean = new BeanMappingFactoryTest.Bean();
        bean.setIdField(3L);
        bean.setSuperProp(5L);
        UpdateStatement<Bean> b1 = update(bean);
        b1.setBy("superProp");

        Assert.assertEquals("UPDATE bean SET g_super_prop='5' WHERE f_id_field = '3';\n", b1.getSqlWithParams());
    }

    @SuppressWarnings("unchecked")
    private <B> DeleteStatement<B> delete(B bean) {
        return new DeleteStatement<B>(new JdbcTemplate(), new OracleStatementBuilder(), bean);
    }

    @Test
    public void testDelete() {
        BeanMappingFactoryTest.Bean bean = new BeanMappingFactoryTest.Bean();
        bean.setIdField(2L);
        DeleteStatement<Bean> i1 = delete(bean);
        Assert.assertEquals("DELETE FROM bean WHERE f_id_field = ?", i1.getSql());
        MappingParamFunction<Bean> function = new MappingParamFunction<Bean>(
                BeanMappings.get(BeanMappingFactoryTest.Bean.class));
        function.setBean(bean);
        Assert.assertArrayEquals(new Object[] { 2L }, i1.getParams(function));

        Assert.assertEquals("DELETE FROM bean WHERE f_id_field = '2';\n", i1.getSqlWithParams());

        bean.setEmb(new EmBean());
        bean.getEmb().setKar(3L);
        bean.setIdField(null);

        Assert.assertArrayEquals(new Object[] { null }, i1.getParams(function));
    }

}
