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
package com.nortal.petit.orm;

public class DefaultBeanPropertyConverter implements BeanPropertyConverter {

    @Override
    public Object toJdbcValue(Object o) {
        if (o == null) {
            return null;
        }
        if (o.getClass().isEnum()) {
            return ((Enum<?>) o).name();
        }
        return o;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object toPropertyValue(Class<?> type, String value) {
        if (Enum.class.isAssignableFrom(type)) {
            return Enum.valueOf((Class) type, value);
        }
        throw new IllegalArgumentException("Unsupported property type: " + type);
    }

    @Override
    public Object apply(Object input) {
        return toJdbcValue(input);
    }
}
