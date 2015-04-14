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

import com.google.common.base.Function;

/**
 * A mapping of a bean as a collection of properties.
 * 
 * @author Aleksei Lissitsin
 * 
 */
public interface BeanMapping<B> {
    
    public static final String USE_ADDITIONAL_CONFIGURATION_KEY = "com.nortal.persistence.useAdditionalConfiguration";
    
    /**
     * Creates a new bean instance.
     */
    B instance();

    /**
     * Returns the name of the table to map this bean to.
     */
    String table();

    /**
     * Returns a name->property map of properties.
     */
    Map<String, Property<B, Object>> props();

    /**
     * Returns the property distinguished as an id column or null if no such
     * property exists.
     */
    Property<B, Object> id();
    
    Class<B> type();
    
    Function<String, String> getPropertyNameMapper(final boolean includeReadOnly);
}
