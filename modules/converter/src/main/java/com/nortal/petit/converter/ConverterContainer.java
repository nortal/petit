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
package com.nortal.petit.converter;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Roman Tekhov
 */
public class ConverterContainer {

    private Map<Class<?>, Map<Class<?>, Converter<?, ?>>> toTypeMapping = new HashMap<Class<?>, Map<Class<?>, Converter<?, ?>>>();

    public void add(ConverterGroup group) {
        for (Converter<?, ?> converter : group.getAll()) {
            Class<?> toType = converter.getToType();

            Map<Class<?>, Converter<?, ?>> fromTypeMapping = toTypeMapping.get(toType);
            if (fromTypeMapping == null) {
                fromTypeMapping = new HashMap<Class<?>, Converter<?, ?>>();
                toTypeMapping.put(toType, fromTypeMapping);
            }

            fromTypeMapping.put(converter.getFromType(), converter);
        }
    }

    @SuppressWarnings("unchecked")
    public <F, T> Converter<F, T> get(Class<F> fromType, Class<T> toType) {
        Map<Class<?>, Converter<?, ?>> fromTypeMapping = toTypeMapping.get(toType);

        if (fromTypeMapping == null) {
            return null;
        }

        Class<?> type = fromType;
        while (type != null) {
            Converter<?, ?> fromTypeConverter = fromTypeMapping.get(type);

            Converter<?, ?> converter = (fromTypeConverter != null) ? fromTypeConverter : getForInterface(type,
                    fromTypeMapping);

            if (converter != null) {
                return (Converter<F, T>) converter;
            }

            type = type.getSuperclass();
        }

        return null;
    }

    private Converter<?, ?> getForInterface(Class<?> type, Map<Class<?>, Converter<?, ?>> fromTypeMapping) {
        Class<?>[] interfaces = type.getInterfaces();

        for (Class<?> interfaceType : interfaces) {
            @SuppressWarnings("unchecked")
            Converter<?, ?> converter = coalesce(fromTypeMapping.get(type),
                    getForInterface(interfaceType, fromTypeMapping));

            if (converter != null) {
                return converter;
            }
        }

        return null;
    }

    private <T> T coalesce(T... items) {
        if (ArrayUtils.isNotEmpty(items)) {
            for (T item : items) {
                if (item != null) {
                    return item;
                }
            }
        }
        return null;
    }
}
