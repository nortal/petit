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

/**
 * A provider of BeanMapping instances. This class is intended for use
 * throughout the application code.
 * 
 * @author Aleksei Lissitsin
 * 
 */
public class BeanMappings {

    private static BeanMappingFactory factory = new BeanMappingFactoryImpl();

    public static void setFactory(BeanMappingFactory factory) {
        BeanMappings.factory = factory;
    }
    
	/**
	 * Specify whether beanMapping instances should be cached (default behaviour) or
	 * constructed every time (useful during development).
	 * 
	 * @param enabled
	 */
	public static void setCached(boolean cached) {
		BeanMappingCache.getInstance().setEnabled(cached);
	}

    public static <B> BeanMapping<B> get(Class<B> beanClass) {
        BeanMapping<B> mapping = BeanMappingCache.getInstance().get(beanClass);
        if (mapping == null) {
        	mapping = factory.create(beanClass);
            BeanMappingCache.getInstance().put(beanClass, mapping);
        }
        return mapping;
    }
}
