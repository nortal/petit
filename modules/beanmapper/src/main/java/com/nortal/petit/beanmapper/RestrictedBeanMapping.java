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

import java.util.HashMap;
import java.util.Map;

/**
 * A beanmapping restricted to a set of properties.
 * 
 * @author Aleksei Lissitsin
 * 
 */
public class RestrictedBeanMapping<B> extends DelegateBeanMapping<B> {

    private Map<String, Property<B, Object>> propMap = new HashMap<String, Property<B, Object>>();

    public RestrictedBeanMapping(BeanMapping<B> beanMapping, String... props) {
        super(beanMapping);
        Map<String, Property<B, Object>> origMap = beanMapping.props();
        for (String prop : props) {
            propMap.put(prop, origMap.get(prop));
        }
    }

    @Override
    public Map<String, Property<B, Object>> props() {
        return propMap;
    }
}
