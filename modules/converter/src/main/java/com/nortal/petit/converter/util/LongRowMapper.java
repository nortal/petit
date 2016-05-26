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

import org.springframework.jdbc.core.RowMapper;

import com.nortal.petit.converter.columnreader.StandardStrategies;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Row mapper for @Long type that correctly handles <code>NULL</code> values.
 * 
 * @author Vassili Jakovlev (vassili.jakovlev@nortal.com)
 */
public class LongRowMapper implements RowMapper<Long> {

    private final ColumnPosition columnPosition;

    /**
     * Creates row mapper that reads value from the first column in result set.
     */
    public LongRowMapper() {
        this(1);
    }

    public LongRowMapper(int columnIndex) {
        columnPosition = new ColumnPosition(columnIndex);
    }

    public LongRowMapper(String columnName) {
        columnPosition = new ColumnPosition(columnName);
    }

    /**
     * @return <code>null</code>, if column's SQL value is <code>NULL</code>,
     *         otherwise column's numeric value converted to @Long
     */
    @Override
    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        return StandardStrategies.getLong(rs, columnPosition.getIndex(rs));
    }
}