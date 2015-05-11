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

/**
 * Extended BeanMapping. Useful when adding custom fields (for example fields
 * with Transient annotation) to the default mapping.
 * 
 * @author Alrik Peets
 *
 * @param <B>
 */
public class ExtendedBeanMapping<B> extends DelegateBeanMapping<B> {

    private Map<String, Property<B, Object>> extendedProps = new LinkedHashMap<String, Property<B, Object>>();

    public ExtendedBeanMapping(BeanMapping<B> beanMapping,
            Map<String, Property<B, Object>> extProps) {
        super(beanMapping);
        this.extendedProps = extProps;
    }

    @Override
    public Map<String, Property<B, Object>> props() {
        Map<String, Property<B, Object>> props = new LinkedHashMap<String, Property<B, Object>>(
                beanMapping.props());
        for (String key : extendedProps.keySet()) {
            Property<B, Object> property = extendedProps.get(key);
            props.put(key, property);
        }
        return props;
    }
}
