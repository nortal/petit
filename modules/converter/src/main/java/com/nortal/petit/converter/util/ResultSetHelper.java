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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.nortal.petit.converter.columnreader.ColumnReader;
import com.nortal.petit.converter.columnreader.StandardStrategies;

/**
 * Helper class for correct reception of certain types of data from the
 * database. It can also wrap a ResultSet object.
 * 
 * @author Aleksei Lissitsin <aleksei.lissitsin@webmedia.ee>
 */
public class ResultSetHelper {
    private static StandardStrategies standardStrategies = new StandardStrategies();

    private ResultSet rs;

    public ResultSetHelper(ResultSet rs) {
        this.rs = rs;
    }

    public static Object get(Type type, ResultSet rs, String column) throws SQLException {
        try {
            ColumnReader<?> strategy = standardStrategies.get(type);
            if (strategy == null) {
                return null;
            }
            return strategy.getColumnValue(rs, rs.findColumn(column));

        } catch (SQLException e) {
            SQLException exception = new SQLException("Failed to get data from column " + column);
            e.setNextException(exception);
            throw e;
        }
    }

    public Long getLong(String column) throws SQLException {
        return StandardStrategies.getLong(rs, rs.findColumn(column));
    }

    public Integer getInteger(String column) throws SQLException {
        return StandardStrategies.getInteger(rs, rs.findColumn(column));
    }

    public String getString(String column) throws SQLException {
        return rs.getString(column);
    }

    public Double getDouble(String column) throws SQLException {
        return StandardStrategies.getDouble(rs, rs.findColumn(column));
    }

    public Date getDate(String column) throws SQLException {
        return StandardStrategies.getDate(rs, rs.findColumn(column));
    }

    public Timestamp getTimestamp(String column) throws SQLException {
        return StandardStrategies.getTimestamp(rs, rs.findColumn(column));
    }

    public Boolean getBoolean(String column) throws SQLException {
        return StandardStrategies.getBoolean(rs, rs.findColumn(column));
    }

    public BigDecimal getBigDecimal(String column) throws SQLException {
        return rs.getBigDecimal(column);
    }

    public boolean next() throws SQLException {
        return rs.next();
    }
}
