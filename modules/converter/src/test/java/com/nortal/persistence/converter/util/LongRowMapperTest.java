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
package com.nortal.persistence.converter.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.nortal.petit.converter.util.LongRowMapper;

/**
 * @author Vassili Jakovlev (vassili.jakovlev@nortal.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class LongRowMapperTest {

    @Test
    public void mapNull() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.doReturn(true).when(resultSet).wasNull();

        Assert.assertEquals(null, new LongRowMapper().mapRow(resultSet, 0));
        Assert.assertEquals(null, new LongRowMapper(3).mapRow(resultSet, 0));
        Assert.assertEquals(null, new LongRowMapper("column-name").mapRow(resultSet, 0));
    }

    @Test
    public void mapZero() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.doReturn(1).when(resultSet).findColumn(Matchers.eq("column-name"));
        Mockito.doReturn(0L).when(resultSet).getLong(Matchers.eq(1));

        Assert.assertEquals(Long.valueOf(0), new LongRowMapper().mapRow(resultSet, 0));
        Assert.assertEquals(Long.valueOf(0), new LongRowMapper(1).mapRow(resultSet, 0));
        Assert.assertEquals(Long.valueOf(0), new LongRowMapper("column-name").mapRow(resultSet, 0));
    }

    @Test
    public void mapTen() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.doReturn(1).when(resultSet).findColumn(Matchers.eq("column-name"));
        Mockito.doReturn(10L).when(resultSet).getLong(Matchers.eq(1));

        Assert.assertEquals(Long.valueOf(10), new LongRowMapper().mapRow(resultSet, 0));
        Assert.assertEquals(Long.valueOf(10), new LongRowMapper(1).mapRow(resultSet, 0));
        Assert.assertEquals(Long.valueOf(10), new LongRowMapper("column-name").mapRow(resultSet, 0));
    }

}
