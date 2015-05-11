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

import java.util.LinkedHashMap;
import java.util.Map;

import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.beanmapper.BeanMappingUtils;
import com.nortal.petit.beanmapper.BeanMappings;
import com.nortal.petit.beanmapper.ExtendedBeanMapping;
import com.nortal.petit.beanmapper.Property;

/**
 * @author Aleksei Lissitsin
 */
public class ExtendedBeanMappingFactoryImpl implements
        ExtendedBeanMappingFactory {

    @Override
    public <B> BeanMapping<B> create(Class<B> type, String... extendedProps) {
        BeanMapping<B> beanMapping = BeanMappings.get(type);
        Map<String, String> args = argsToMap(extendedProps);
        Map<String, Property<B, Object>> extProps = new LinkedHashMap<String, Property<B, Object>>();
        for (String prop : args.keySet()) {
            BeanMappingUtils.initExtendedProperty(extProps, prop,
                    beanMapping.type(), args.get(prop));
        }

        return new ExtendedBeanMapping<B>(beanMapping, extProps);
    }

    private static Map<String, String> argsToMap(String... extendedProps) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (int i = 0; i < extendedProps.length; i+=2) {
            map.put(extendedProps[i], extendedProps[i + 1]);
        }
        return map;
    }

}
