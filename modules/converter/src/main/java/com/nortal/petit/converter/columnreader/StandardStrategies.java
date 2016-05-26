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
package com.nortal.petit.converter.columnreader;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.nortal.petit.converter.provider.SimpleContainer;
import com.nortal.petit.core.model.Id;

public class StandardStrategies extends SimpleContainer<Type, ColumnReader<?>> {
    public static StandardStrategies instance = new StandardStrategies();

    {
    	map.put(Long.class, new LongColumnRetrievalStrategy());
    	map.put(Id.class, new IdColumnRetrievalStrategy<Object>());
    	map.put(String.class, new StringColumnRetrievalStrategy());
    	map.put(Date.class, new DateColumnRetrievalStrategy());
    	map.put(Timestamp.class, new TimestampColumnRetrievalStrategy());
    	map.put(Boolean.class, new BooleanColumnRetrievalStrategy());
    	map.put(Integer.class, new IntegerColumnRetrievalStrategy());
    	map.put(BigDecimal.class, new BigDecimalColumnRetrievalStrategy());
    	map.put(Double.class, new DoubleColumnRetrievalStrategy());
    	map.putAll(new PrimitiveStrategies().getAll());
    }
    
    public static Long getLong(ResultSet rs, int index) throws SQLException {
        long val = rs.getLong(index);
        return rs.wasNull() ? null : val;
    }

    public static Integer getInteger(ResultSet rs, int index) throws SQLException {
        int val = rs.getInt(index);
        return rs.wasNull() ? null : val;
    }
    
    public static Boolean getBoolean(ResultSet rs, int index) throws SQLException {
        boolean val = rs.getBoolean(index);
        return rs.wasNull() ? null : val;
    }

    public static Double getDouble(ResultSet rs, int index) throws SQLException {
        double val = rs.getDouble(index);
        return rs.wasNull() ? null : val;
    }

    public static Date getDate(ResultSet rs, int index) throws SQLException {
        Long milliseconds = getMilliseconds(rs, index);
        return milliseconds == null ? null : new Date(milliseconds);
    }

    public static Timestamp getTimestamp(ResultSet rs, int index) throws SQLException {
        Long milliseconds = getMilliseconds(rs, index);
        return milliseconds == null ? null : new Timestamp(milliseconds);
    }

    private static Long getMilliseconds(ResultSet rs, int index) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(index);
        return timestamp == null ? null : timestamp.getTime();
    }

    public static class IdColumnRetrievalStrategy<T> implements ColumnReader<Id<T>> {
        @Override
        public Id<T> getColumnValue(ResultSet rs, int index) throws SQLException {
            return Id.create(rs.getLong(index));
        }
    }

    private static class LongColumnRetrievalStrategy implements ColumnReader<Long> {
        @Override
        public Long getColumnValue(ResultSet rs, int index) throws SQLException {
            return getLong(rs, index);
        }
    }

    private static class IntegerColumnRetrievalStrategy implements ColumnReader<Integer> {
        @Override
        public Integer getColumnValue(ResultSet rs, int index) throws SQLException {
            return getInteger(rs, index);
        }
    }

    private static class StringColumnRetrievalStrategy implements ColumnReader<String> {
        @Override
        public String getColumnValue(ResultSet rs, int index) throws SQLException {
            return rs.getString(index);
        }
    }

    private static class DateColumnRetrievalStrategy implements ColumnReader<Date> {
        @Override
        public Date getColumnValue(ResultSet rs, int index) throws SQLException {
            return getDate(rs, index);
        }
    }

    private static class TimestampColumnRetrievalStrategy implements ColumnReader<Timestamp> {
        @Override
        public Timestamp getColumnValue(ResultSet rs, int index) throws SQLException {
            return getTimestamp(rs, index);
        }
    }

    private static class BooleanColumnRetrievalStrategy implements ColumnReader<Boolean> {
        @Override
        public Boolean getColumnValue(ResultSet rs, int index) throws SQLException {
            return getBoolean(rs, index);
        }
    }

    private static class DoubleColumnRetrievalStrategy implements ColumnReader<Double> {
        @Override
        public Double getColumnValue(ResultSet rs, int index) throws SQLException {
            return getDouble(rs, index);
        }
    }

    private static class BigDecimalColumnRetrievalStrategy implements ColumnReader<BigDecimal> {
        @Override
        public BigDecimal getColumnValue(ResultSet rs, int index) throws SQLException {
            return rs.getBigDecimal(index);
        }
    }

    public static class NopColumnRetrievalStrategy implements ColumnReader<Object> {
        @Override
        public Object getColumnValue(ResultSet rs, int index) throws SQLException {
            return null;
        }
    }
}
