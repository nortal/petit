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
import org.junit.Test;

import com.google.common.base.Function;
import com.nortal.petit.orm.statement.OracleStatementBuilder;
import com.nortal.petit.orm.statement.StatementBuilder;
import com.nortal.petit.orm.statement.clause.Where;

/**
 * @author Aleksei Lissitsin
 * 
 */
public class StatementBuilderTest {

    private StatementBuilder sb() {
        return new OracleStatementBuilder();
    }

    private StatementBuilder createLoadSb1() {
        return sb().table("my_table").alias("mya").select("bla1", "bla2", "bla3").where("bla1", 2);
    }

    private Function<String, String> upper = new Function<String, String>() {

        @Override
        public String apply(String input) {
            return input.toUpperCase();
        }
    };

    @Test
    public void testLoad() {
        Assert.assertEquals("SELECT mya.bla1, mya.bla2, mya.bla3 FROM my_table mya", sb().table("my_table")
                .alias("mya").select("bla1", "bla2", "bla3").getLoad());

        Assert.assertEquals(
                "SELECT mya.bla1, mya.bla2, mya.bla3 FROM my_table mya GROUP BY bla3 ORDER BY bla2 ASC, bla1 DESC",
                sb().table("my_table").alias("mya").select("bla1", "bla2", "bla3").group("bla3").asc("bla2")
                        .desc("bla1").getLoad());

        Assert.assertEquals("SELECT mya.bla1, mya.bla2, mya.bla3 FROM my_table mya WHERE bla1 = ?", createLoadSb1()
                .getLoad());

        Assert.assertEquals("SELECT mya.bla1, mya.bla2, mya.bla3 FROM my_table mya WHERE bla1 = ? GROUP BY bla3",
                createLoadSb1().group("bla3").getLoad());

        Assert.assertEquals(
                "SELECT mya.bla1, mya.bla2, mya.bla3 FROM my_table mya WHERE bla1 = ? GROUP BY bla3 ORDER BY bla2 ASC, bla1 ASC",
                createLoadSb1().group("bla3").asc("bla2", "bla1").getLoad());

        Assert.assertEquals(
                "SELECT mya.bla1, mya.bla2, mya.bla3 FROM my_table mya WHERE bla1 = ? GROUP BY bla3 ORDER BY bla2 ASC, bla1 DESC",
                createLoadSb1().group("bla3").asc("bla2").desc("bla1").getLoad());

        StatementBuilder sb1 = createLoadSb1();
        sb1.setPropertyNameMapper(upper);

        Assert.assertEquals(
                "SELECT mya.BLA1, mya.BLA2, mya.BLA3 FROM my_table mya WHERE BLA1 = ? GROUP BY BLA3 ORDER BY BLA2 ASC, BLA1 DESC",
                sb1.group("BLA3").asc("bla2").desc("bla1").getLoad());

        Assert.assertEquals(
                "SELECT COUNT(*) FROM (SELECT mya.bla1, mya.bla2, mya.bla3 FROM my_table mya WHERE bla1 = ?) t",
                createLoadSb1().countSql());

    }

    @Test
    public void testInsert() {
        StatementBuilder sb = sb().table("my_table").setBy("bla1", "bla2", "bla3").setWith(1, 2, 3);
        Assert.assertEquals("INSERT INTO my_table (bla1, bla2, bla3) VALUES (?, ?, ?)", sb.getInsert());
        Assert.assertArrayEquals(new Object[] { 1, 2, 3 }, sb.getParams(null));
        sb.setPropertyNameMapper(upper);
        Assert.assertEquals("INSERT INTO my_table (BLA1, BLA2, BLA3) VALUES (?, ?, ?)", sb.getInsert());
    }

    @Test
    public void testUpdate() {
        StatementBuilder sb = sb().table("my_table").setBy("bla1", "bla2", "bla3").setWith(1, 2, 3)
                .where(Where.eq("bla4", "3").and().gte("bla5", "5"));
        Assert.assertEquals("UPDATE my_table SET bla1=?, bla2=?, bla3=? WHERE (bla4 = ? AND bla5 >= ?)", sb.getUpdate());
        sb.setPropertyNameMapper(upper);
        Assert.assertEquals("UPDATE my_table SET BLA1=?, BLA2=?, BLA3=? WHERE (BLA4 = ? AND BLA5 >= ?)", sb.getUpdate());
    }

    @Test
    public void testDelete() {
        StatementBuilder sb = sb().table("my_table").where(
                Where.eq("bla4", "3").and().gte("bla5", "5").eq("bla6", Where.prop("bla7")));
        Assert.assertEquals("DELETE FROM my_table WHERE (bla4 = ? AND bla5 >= ? AND bla6 = ?)", sb.getDelete());
        sb.setPropertyNameMapper(upper);
        Assert.assertEquals("DELETE FROM my_table WHERE (BLA4 = ? AND BLA5 >= ? AND BLA6 = ?)", sb.getDelete());
        Assert.assertArrayEquals(new Object[] { "3", "5", "ok" }, sb.getParams(new Function<String, Object>() {
            @Override
            public Object apply(String input) {
                if ("bla7".equals(input)) {
                    return "ok";
                } else {
                    return "nope";
                }
            }
        }));
    }

}
