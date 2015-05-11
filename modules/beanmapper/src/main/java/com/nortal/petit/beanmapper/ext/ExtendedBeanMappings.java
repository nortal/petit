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

import com.nortal.petit.beanmapper.BeanMapping;

/**
 * @author Aleksei Lissitsin
 */
public class ExtendedBeanMappings {
    private static ExtendedBeanMappingFactory factory = new ExtendedBeanMappingFactoryImpl();

    public static void setFactory(ExtendedBeanMappingFactory factory) {
        ExtendedBeanMappings.factory = factory;
    }

    /**
     * Gets an extended beanMapping.
     * 
     * @param extendedProps
     *            are in the form name1, column1, name2, column2, ... and
     *            expected to contain an even number of elements.
     */
    public static <B> BeanMapping<B> get(Class<B> beanClass,
            String... extendedProps) {
        if (extendedProps.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "ExtendedBeanMappings#get: extendedProps are expected to have an even number of elements!");
        }
        BeanMapping<B> mapping = ExtendedBeanMappingCache.getInstance().get(
                beanClass, extendedProps);
        if (mapping == null) {
            mapping = factory.create(beanClass, extendedProps);
            ExtendedBeanMappingCache.getInstance().put(beanClass, mapping);
        }
        return mapping;
    }
}
