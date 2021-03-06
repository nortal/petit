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

import com.nortal.petit.converter.Converter;
import com.nortal.petit.converter.provider.Provider;

public class ConverterColumnReaders implements Provider<Type, ColumnReader<?>> {
    private Provider<Type, Converter<?, ?>> converters;
    private Provider<Type, ColumnReader<?>> readers;
    
    public ConverterColumnReaders(Provider<Type, Converter<?, ?>> converters, Provider<Type, ColumnReader<?>> readers) {
        this.converters = converters;
        this.readers = readers;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public ColumnReader<?> get(Type type) {
        Converter<?, ?> converter = converters.get(type);
        if (converter != null) {
            ColumnReader<?> reader = readers.get(converter.getFromType());
            if (reader != null) {
                return new CompositeColumnReader(converter, reader);
            }
        }
        return readers.get(type);
    }
}
