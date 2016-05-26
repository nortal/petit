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

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.jdbc.core.RowMapper;

import com.nortal.petit.converter.columnreader.ColumnReader;
import com.nortal.petit.converter.columnreader.StandardStrategies;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper for generic Pair. Note that we need to provide Class arguments
 * since generic type information is nowhere to be found at runtime and we
 * cannot decide which method to invoke. Wait for runtime generics in Java! %)
 * Watch for default values ('key', 'value') for field names and (String,
 * String) for field classes.
 * 
 * @author Aleksei Lissitsin <aleksei.lissitsin@webmedia.ee>
 */
public class PairRowMapper<T, K> implements RowMapper<Pair<T, K>> {
    private final ColumnPosition keyField;
    private final ColumnPosition valueField;
    private final ColumnReader<T> keyType;
    private final ColumnReader<K> valueType;

    public PairRowMapper() {
        this("key", "value");
    }

    @SuppressWarnings("unchecked")
    public PairRowMapper(String keyField, String valueField) {
        this(keyField, valueField, (Class<T>)String.class, (Class<K>)String.class);
    }

    public PairRowMapper(Class<T> keyClass, Class<K> valueClass) {
        this("key", "value", keyClass, valueClass);
    }

    @SuppressWarnings("unchecked")
    public PairRowMapper(String keyField, String valueField, Class<T> keyClass, Class<K> valueClass) {
        this.keyField = new ColumnPosition(keyField);
        this.valueField = new ColumnPosition(valueField);
        this.keyType = (ColumnReader<T>) StandardStrategies.instance.get(keyClass);
        this.valueType = (ColumnReader<K>) StandardStrategies.instance.get(valueClass);
    }

    public Pair<T, K> mapRow(ResultSet rs, int arg1) throws SQLException {
        T key = keyType.getColumnValue(rs, keyField.getIndex(rs));
        K value = valueType.getColumnValue(rs, valueField.getIndex(rs));
        return Pair.of(key, value);
    }
}
