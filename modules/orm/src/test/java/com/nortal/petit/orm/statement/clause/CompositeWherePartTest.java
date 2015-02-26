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
package com.nortal.petit.orm.statement.clause;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Functions;
import com.nortal.petit.orm.statement.clause.Where;
import com.nortal.petit.orm.statement.clause.WherePart;

/**
 * @author Aleksei Lissitsin
 * 
 */
public class CompositeWherePartTest {

    private void myAssert(String expected, WherePart part) {
        Assert.assertEquals(expected, part.sql(Functions.<String> identity()));
    }

    @Test
    public void test() {
        String sql1 = "(bla = ? AND (fla LIKE ? OR fla LIKE ?))";
        String sql1_1 = "(bla = ? OR fla LIKE ? OR fla LIKE ?)";

        myAssert(sql1, Where.eq("bla", 2).and(Where.like("fla", "34").or().like("fla", "45")));

        myAssert(sql1, Where.eq("bla", 2).and(Where.or(Where.like("fla", "34"), Where.like("fla", "45"))));

        myAssert(sql1, Where.and(Where.eq("bla", 2), Where.or(Where.like("fla", "34"), Where.like("fla", "45"))));

        myAssert(sql1, Where.and().eq("bla", 2).and(Where.or().like("fla", "34").like("fla", "45")));

        myAssert(sql1_1, Where.and().eq("bla", 2).or(Where.or().like("fla", "34").like("fla", "45")));

        myAssert(sql1_1, Where.eq("bla", 2).or().like("fla", "34").like("fla", "45"));

        String sql2 = "(bla = ? AND fla > ? AND rla BETWEEN ? AND ?  AND lla LIKE ?)";

        myAssert(sql2, Where.eq("bla", 2).and().gt("fla", "df").range("rla", 3, 4).like("lla", "foo"));
    }

    @Test
    public void test_ilike() {
        String likeSql = "col1 LIKE ?";
        String ilikeSql = "lower(col1) LIKE lower(?)";
        
        myAssert(likeSql, Where.like("col1", "val"));
        myAssert(ilikeSql, Where.ilike("col1", "val"));
    }
    
}
