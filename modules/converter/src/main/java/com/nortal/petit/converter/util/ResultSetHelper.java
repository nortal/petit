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
package com.nortal.petit.converter.util;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.nortal.petit.converter.Converter;
import com.nortal.petit.converter.ConverterFactory;
import com.nortal.petit.converter.ConverterGroup;
import com.nortal.petit.core.model.Id;

/**
 * Helper class for correct reception of certain types of data from the
 * database. It can also wrap a ResultSet object.
 * 
 * @author Aleksei Lissitsin <aleksei.lissitsin@webmedia.ee>
 */
// 11.02.2013 Lauri Lättemäe - removed support for Day
public class ResultSetHelper {
    public static String converterGroup = ConverterGroup.DEFAULT_GROUP;

    public static class ColumnPosition {
        private final boolean isNamed;
        private String name;
        private int index; // 1-based

        public ColumnPosition(String columnName) {
            isNamed = true;
            this.name = columnName;
        }

        public ColumnPosition(int index) {
            isNamed = false;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }
    }

    public enum Type {
        LONG(new LongColumnRetrievalStrategy()),
        ID(new IdColumnRetrievalStrategy<Object>()),
        INTEGER(new IntegerColumnRetrievalStrategy()),
        STRING(new StringColumnRetrievalStrategy()),
        DATE(new DateColumnRetrievalStrategy()),
        TIMESTAMP(new TimestampColumnRetrievalStrategy()),
        BOOLEAN(new BooleanColumnRetrievalStrategy()),
        DOUBLE(new DoubleColumnRetrievalStrategy()),
        BIG_DECIMAL(new BigDecimalColumnRetrievalStrategy());

        private ColumnRetrievalStrategy<?> columnRetrievalStrategy;

        private Type(ColumnRetrievalStrategy<?> columnRetrievalStrategy) {
            this.columnRetrievalStrategy = columnRetrievalStrategy;
        }

        ColumnRetrievalStrategy<?> getColumnRetrievalStrategy() {
            return columnRetrievalStrategy;
        }
    }

    protected static final Map<Class<?>, Type> CLASS_TO_TYPE = new HashMap<Class<?>, Type>(7);

    static {
        CLASS_TO_TYPE.put(Long.class, Type.LONG);
        CLASS_TO_TYPE.put(long.class, Type.LONG);
        CLASS_TO_TYPE.put(Id.class, Type.ID);
        CLASS_TO_TYPE.put(String.class, Type.STRING);
        CLASS_TO_TYPE.put(Date.class, Type.DATE);
        CLASS_TO_TYPE.put(Timestamp.class, Type.TIMESTAMP);
        CLASS_TO_TYPE.put(Boolean.class, Type.BOOLEAN);
        CLASS_TO_TYPE.put(boolean.class, Type.BOOLEAN);
        CLASS_TO_TYPE.put(Integer.class, Type.INTEGER);
        CLASS_TO_TYPE.put(BigDecimal.class, Type.BIG_DECIMAL);
        CLASS_TO_TYPE.put(Double.class, Type.DOUBLE);
    }

    private ResultSet rs;

    public ResultSetHelper(ResultSet rs) {
        this.rs = rs;
    }

    public static Long getLong(ResultSet rs, String column) throws SQLException {
        return getLong(rs, new ColumnPosition(column));
    }

    public static Long getLong(ResultSet rs, ColumnPosition column) throws SQLException {
        BigDecimal bd = getBigDecimal(rs, column);
        return bd == null ? null : Long.valueOf(bd.longValue());
    }

    public static Integer getInteger(ResultSet rs, ColumnPosition column) throws SQLException {
        BigDecimal bd = getBigDecimal(rs, column);
        return bd == null ? null : Integer.valueOf(bd.intValue());
    }

    public static String getString(ResultSet rs, ColumnPosition column) throws SQLException {
        return column.isNamed ? rs.getString(column.getName()) : rs.getString(column.getIndex());
    }

    public static Date getDate(ResultSet rs, ColumnPosition column) throws SQLException {
        Long milliseconds = getMilliseconds(rs, column);
        return milliseconds == null ? null : new Date(milliseconds);
    }

    public static Timestamp getTimestamp(ResultSet rs, ColumnPosition column) throws SQLException {
        Long milliseconds = getMilliseconds(rs, column);
        return milliseconds == null ? null : new Timestamp(milliseconds);
    }

    private static Long getMilliseconds(ResultSet rs, ColumnPosition column) throws SQLException {
        Timestamp timestamp = column.isNamed ? rs.getTimestamp(column.getName()) : rs.getTimestamp(column.getIndex());
        return timestamp == null ? null : timestamp.getTime();
    }

    public static Boolean getBoolean(ResultSet rs, ColumnPosition column) throws SQLException {
        String s = getString(rs, column);
        return s == null ? null : Boolean.valueOf("1".equals(s) || "t".equalsIgnoreCase(s));
    }

    public static Double getDouble(ResultSet rs, ColumnPosition column) throws SQLException {
        BigDecimal bd = getBigDecimal(rs, column);
        return bd == null ? null : Double.valueOf(bd.doubleValue());
    }

    public static BigDecimal getBigDecimal(ResultSet rs, ColumnPosition column) throws SQLException {
        return column.isNamed ? rs.getBigDecimal(column.getName()) : rs.getBigDecimal(column.getIndex());
    }

    public static Object get(Type type, ResultSet rs, String column) throws SQLException {
        try {
            ColumnRetrievalStrategy<?> strategy = type == null ? new NopColumnRetrievalStrategy() : type
                    .getColumnRetrievalStrategy();

            return strategy.getColumnValue(rs, column);

        } catch (SQLException e) {
            SQLException exception = new SQLException("Failed to get data from column " + column);
            e.setNextException(exception);
            throw e;
        }
    }

    /**
     * Queries resultSet for data of given class. Should be used mainly for
     * single object queries. Consider using {@link #get(Type, ResultSet, String)} for lists of data(e.g. in
     * RowMapper). Returns null if given class is not supported.
     */
    public static Object getSimpleTyped(Class<?> clazz, ResultSet rs, String column) throws SQLException {
        return get(classToType(clazz), rs, column);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz, ResultSet rs, String column) throws SQLException {
        Converter<String, T> converter = ConverterFactory.getConverter(converterGroup, String.class, clazz);
        if (converter != null) {
            return converter.convert(rs.getString(column));
        }

        return (T) getSimpleTyped(clazz, rs, column);
    }

    /**
     * Converts Class value to supported Type value which can be used for faster
     * access to methods. Returns null if given class is not supported.
     */
    public static Type classToType(Class<?> clazz) {
        return CLASS_TO_TYPE.get(clazz);
    }

    /**
     * Converts Class value to supported Type value which can be used for faster
     * access to methods. Throws RuntimeException if given class is not
     * supported.
     */
    public static Type convertToType(Class<?> clazz) {
        Type type = classToType(clazz);
        if (type == null) {
            throw new IllegalArgumentException("Field of type " + clazz + " not supported!");
        }
        return type;
    }

    public Long getLong(String column) throws SQLException {
        return getLong(rs, new ColumnPosition(column));
    }

    public Integer getInteger(String column) throws SQLException {
        return getInteger(rs, new ColumnPosition(column));
    }

    public String getString(String column) throws SQLException {
        return getString(rs, new ColumnPosition(column));
    }

    public Double getDouble(String column) throws SQLException {
        return getDouble(rs, new ColumnPosition(column));
    }

    public Date getDate(String column) throws SQLException {
        return getDate(rs, new ColumnPosition(column));
    }

    public Timestamp getTimestamp(String column) throws SQLException {
        return getTimestamp(rs, new ColumnPosition(column));
    }

    public Boolean getBoolean(String column) throws SQLException {
        return getBoolean(rs, new ColumnPosition(column));
    }

    public BigDecimal getBigDecimal(String column) throws SQLException {
        return getBigDecimal(rs, new ColumnPosition(column));
    }

    public boolean next() throws SQLException {
        return rs.next();
    }

    private static abstract class ColumnRetrievalStrategy<T> {
        abstract T getColumnValue(ResultSet rs, ColumnPosition columnPosition) throws SQLException;

        T getColumnValue(ResultSet rs, String columnName) throws SQLException {
            return getColumnValue(rs, new ColumnPosition(columnName));
        }
    }

    public static class IdColumnRetrievalStrategy<T> extends ColumnRetrievalStrategy<Id<T>> {
        @Override
        public Id<T> getColumnValue(ResultSet rs, ColumnPosition column) throws SQLException {
            Long result = getLong(rs, column);
            return Id.create(result);
        }
    }

    private static class LongColumnRetrievalStrategy extends ColumnRetrievalStrategy<Long> {
        @Override
        public Long getColumnValue(ResultSet rs, ColumnPosition column) throws SQLException {
            return getLong(rs, column);
        }
    }

    private static class IntegerColumnRetrievalStrategy extends ColumnRetrievalStrategy<Integer> {
        @Override
        public Integer getColumnValue(ResultSet rs, ColumnPosition column) throws SQLException {
            return getInteger(rs, column);
        }
    }

    private static class StringColumnRetrievalStrategy extends ColumnRetrievalStrategy<String> {
        @Override
        public String getColumnValue(ResultSet rs, ColumnPosition column) throws SQLException {
            return getString(rs, column);
        }
    }

    private static class DateColumnRetrievalStrategy extends ColumnRetrievalStrategy<Date> {
        @Override
        public Date getColumnValue(ResultSet rs, ColumnPosition column) throws SQLException {
            return getDate(rs, column);
        }
    }

    private static class TimestampColumnRetrievalStrategy extends ColumnRetrievalStrategy<Timestamp> {
        @Override
        public Timestamp getColumnValue(ResultSet rs, ColumnPosition column) throws SQLException {
            return getTimestamp(rs, column);
        }
    }

    private static class BooleanColumnRetrievalStrategy extends ColumnRetrievalStrategy<Boolean> {
        @Override
        public Boolean getColumnValue(ResultSet rs, ColumnPosition column) throws SQLException {
            return getBoolean(rs, column);
        }
    }

    private static class DoubleColumnRetrievalStrategy extends ColumnRetrievalStrategy<Double> {
        @Override
        public Double getColumnValue(ResultSet rs, ColumnPosition column) throws SQLException {
            return getDouble(rs, column);
        }
    }

    private static class BigDecimalColumnRetrievalStrategy extends ColumnRetrievalStrategy<BigDecimal> {
        @Override
        public BigDecimal getColumnValue(ResultSet rs, ColumnPosition column) throws SQLException {
            return getBigDecimal(rs, column);
        }
    }

    private static class NopColumnRetrievalStrategy extends ColumnRetrievalStrategy<Object> {
        @Override
        public Object getColumnValue(ResultSet rs, ColumnPosition column) throws SQLException {
            return null;
        }
    }

}
