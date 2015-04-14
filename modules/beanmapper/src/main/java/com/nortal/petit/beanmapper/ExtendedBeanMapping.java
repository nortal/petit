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

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.Assert;

import com.google.common.base.Function;

/**
 * Extended BeanMapping. Useful when adding custom fields (for example fields with Transient annotation)
 * to the default mapping.
 * 
 * @author Alrik Peets
 *
 * @param <B>
 */
public class ExtendedBeanMapping<B> implements BeanMapping<B> {

    private BeanMapping<B> beanMapping;
    private Map<String, Property<B, Object>> extendedProps = new LinkedHashMap<String, Property<B, Object>>();
    
    public ExtendedBeanMapping(BeanMapping<B> beanMapping) {
        this.beanMapping = beanMapping;
    }
    
    @Override
    public B instance() {
        return beanMapping.instance();
    }

    @Override
    public String table() {
        return beanMapping.table();
    }

    @Override
    public Map<String, Property<B, Object>> props() {
        Map<String, Property<B, Object>> props = new LinkedHashMap<String, Property<B, Object>>(beanMapping.props());
        for (String key : extendedProps.keySet()) {
            Property<B, Object> property = extendedProps.get(key);
            props.put(key, property);
        }
        return props;
    }

    @Override
    public Property<B, Object> id() {
        return beanMapping.id();
    }

    public void addExtendedProperty(String property, String columnName) {
        BeanMappingUtils.initExtendedProperty(extendedProps, property, type(), columnName);
    }
    
    @Override
    public Function<String, String> getPropertyNameMapper(final boolean includeReadOnly) {
        return new Function<String, String>() {
            public String apply(String name) {
                return getColumn(name, includeReadOnly);
            }
        };
    }

    private String getColumn(String name, boolean includeReadOnly) {
        Property<B, Object> p = props().get(name);
        if (p == null) {
            Assert.notNull(p, "No property " + name + " found!");
        }
        if (includeReadOnly || !p.readOnly()) {
            return p.column();
        }
        return null;
    }

    @Override
    public Class<B> type() {
        return beanMapping.type();
    }
}
