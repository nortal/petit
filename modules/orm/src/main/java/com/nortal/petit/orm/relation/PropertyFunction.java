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
package com.nortal.petit.orm.relation;

import com.google.common.base.Function;
import com.nortal.petit.beanmapper.Property;

/**
 * @author Aleksei Lissitsin
 * 
 */
public class PropertyFunction<B, P> implements Function<B, P> {
    private Property<B, P> property;

    public PropertyFunction(Property<B, P> property) {
        this.property = property;
    }

    @Override
    public P apply(B input) {
        return property.read(input);
    }
}
