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

import com.nortal.petit.beanmapper.BeanMapper;
import com.nortal.petit.beanmapper.BeanMappings;
import com.nortal.petit.beanmapper.ext.ExtendedBeanMappings;

/**
 * @author Aleksei Lissitsin
 * 
 */
public abstract class BeanMappers {
    public static <B> BeanMapper<B> get(Class<B> beanClass) {
        return new BeanMapper<B>(BeanMappings.get(beanClass),
                new DefaultResultSetReader());
    }

    /**
     * Gets a beanMapper with an underlying extended beanMapping.
     * 
     * @param extProps
     *            are in the form name1, column1, name2, column2, ... and
     *            expected to contain an even number of elements.
     */
    public static <B> BeanMapper<B> extended(Class<B> beanClass,
            String... extProps) {
        return new BeanMapper<B>(ExtendedBeanMappings.get(beanClass, extProps),
                new DefaultResultSetReader());
    }
}
