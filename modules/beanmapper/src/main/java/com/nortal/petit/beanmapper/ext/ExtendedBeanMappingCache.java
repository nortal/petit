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
package com.nortal.petit.beanmapper.ext;

import java.util.Map;
import java.util.WeakHashMap;

import com.nortal.petit.beanmapper.BeanMapping;

/**
 * @author Aleksei Lissitsin
 */
class ExtendedBeanMappingCache {
    private static ExtendedBeanMappingCache instance = new ExtendedBeanMappingCache();

    private Map<String, BeanMapping<?>> map = new WeakHashMap<String, BeanMapping<?>>();
    private boolean enabled = true;

    @SuppressWarnings("unchecked")
    protected <B> BeanMapping<B> get(Class<B> clazz, String... extendedProps) {
        if (enabled) {
            return (BeanMapping<B>) map.get(serialize(clazz, extendedProps));
        }
        return null;
    }

    protected <B> void put(Class<B> clazz, BeanMapping<B> mapping,
            String... extendedProps) {
        if (enabled) {
            map.put(serialize(clazz, extendedProps), mapping);
        }
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected static ExtendedBeanMappingCache getInstance() {
        return instance;
    }

    private static <B> String serialize(Class<B> clazz, String... extendedProps) {
        StringBuilder sb = new StringBuilder(clazz.getCanonicalName());
        for (String s : extendedProps) {
            sb.append(";");
            sb.append(s);
        }
        return sb.toString();
    }
}
