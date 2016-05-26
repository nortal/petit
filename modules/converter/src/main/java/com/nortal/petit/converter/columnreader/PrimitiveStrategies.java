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
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nortal.petit.converter.provider.SimpleContainer;

public class PrimitiveStrategies extends SimpleContainer<Type, ColumnReader<?>> {
    {
        map.put(long.class, new LongColumnRetrievalStrategy());
        map.put(int.class, new IntColumnRetrievalStrategy());
        map.put(short.class, new ShortColumnRetrievalStrategy());
        map.put(byte.class, new ByteColumnRetrievalStrategy());
        map.put(float.class, new FloatColumnRetrievalStrategy());
        map.put(double.class, new DoubleColumnRetrievalStrategy());
        map.put(boolean.class, new BooleanColumnRetrievalStrategy());
    }

    private static class LongColumnRetrievalStrategy implements ColumnReader<Long> {
        @Override
        public Long getColumnValue(ResultSet rs, int index) throws SQLException {
            return rs.getLong(index);
        }
    }

    private static class IntColumnRetrievalStrategy implements ColumnReader<Integer> {
        @Override
        public Integer getColumnValue(ResultSet rs, int index) throws SQLException {
            return rs.getInt(index);
        }
    }

    private static class ShortColumnRetrievalStrategy implements ColumnReader<Short> {
        @Override
        public Short getColumnValue(ResultSet rs, int index) throws SQLException {
            return rs.getShort(index);
        }
    }

    private static class ByteColumnRetrievalStrategy implements ColumnReader<Byte> {
        @Override
        public Byte getColumnValue(ResultSet rs, int index) throws SQLException {
            return rs.getByte(index);
        }
    }

    private static class FloatColumnRetrievalStrategy implements ColumnReader<Float> {
        @Override
        public Float getColumnValue(ResultSet rs, int index) throws SQLException {
            return rs.getFloat(index);
        }
    }

    private static class BooleanColumnRetrievalStrategy implements ColumnReader<Boolean> {
        @Override
        public Boolean getColumnValue(ResultSet rs, int index) throws SQLException {
            return rs.getBoolean(index);
        }
    }

    private static class DoubleColumnRetrievalStrategy implements ColumnReader<Double> {
        @Override
        public Double getColumnValue(ResultSet rs, int index) throws SQLException {
            return rs.getDouble(index);
        }
    }
}
