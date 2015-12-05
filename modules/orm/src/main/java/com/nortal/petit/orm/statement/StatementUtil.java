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
package com.nortal.petit.orm.statement;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.springframework.util.Assert;

import com.google.common.base.Function;
import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.beanmapper.Property;

/**
 * @author Aleksei Lissitsin
 * 
 */
class StatementUtil {
    static boolean sort;

    static String[] toStringArray(Collection<String> strings) {
        String[] array = strings.toArray(new String[] {});
        if (sort) {
            Arrays.sort(array);
        }
        return array;
    }

    static <B> Function<String, String> getPropertyNameMapper(
            final BeanMapping<B> mapping, final boolean includeReadOnly) {
        return new Function<String, String>() {
            public String apply(String name) {
                return getColumn(mapping.props(), name, includeReadOnly);
            }
        };
    }

    private static <B> String getColumn(Map<String, Property<B, Object>> props,
            String name, boolean includeReadOnly) {
        Property<B, Object> p = props.get(name);
        if (p == null) {
            Assert.notNull(p, "No property " + name + " found!");
        }
        if (includeReadOnly || !p.readOnly()) {
            return p.column();
        }
        return null;
    }
}
