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
package com.nortal.petit.beanmapper;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * A simple per-class cache of BeanMappings.
 * 
 * @author Aleksei Lissitsin <aleksei.lissitsin@webmedia.ee>
 */
class BeanMappingCache {
    private static BeanMappingCache instance = new BeanMappingCache();

    private Map<Class<?>, BeanMapping<?>> map = new WeakHashMap<Class<?>, BeanMapping<?>>();
    private boolean enabled = true;

    @SuppressWarnings("unchecked")
    protected <B> BeanMapping<B> get(Class<B> clazz) {
        if (enabled) {
            return (BeanMapping<B>) map.get(clazz);
        }
        return null;
    }

    protected <B> void put(Class<B> clazz, BeanMapping<B> mapping) {
        if (enabled) {
            map.put(clazz, mapping);
        }
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected static BeanMappingCache getInstance() {
        return instance;
    }
}
