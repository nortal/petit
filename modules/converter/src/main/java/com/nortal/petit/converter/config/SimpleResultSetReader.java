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
package com.nortal.petit.converter.config;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nortal.petit.converter.columnreader.ColumnReader;
import com.nortal.petit.converter.provider.Provider;
import com.nortal.petit.converter.util.ResultSetReader;

public class SimpleResultSetReader implements ResultSetReader {
    private Provider<Type, ColumnReader<?>> strategies;
    
    public SimpleResultSetReader(Provider<Type, ColumnReader<?>> strategies) {
        this.strategies = strategies;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Type type, ResultSet rs, String column) throws SQLException {
        ColumnReader<?> strategy = strategies.get(type);
        return (T) strategy.getColumnValue(rs, column);

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Type type, ResultSet rs, int index) throws SQLException {
        ColumnReader<?> strategy = strategies.get(type);
        return (T) strategy.getColumnValue(rs, index);
    }
}
