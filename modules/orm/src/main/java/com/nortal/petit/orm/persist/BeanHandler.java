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
package com.nortal.petit.orm.persist;

import java.util.Collection;

import javax.persistence.PersistenceException;

import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.beanmapper.BeanMappings;
import com.nortal.petit.beanmapper.Property;

/**
 * @author Aleksei Lissitsin
 * 
 */
public class BeanHandler<B> implements ItemHandler<B> {

    private Property<B, Object> id;

    public BeanHandler() {
    }

    public BeanHandler(BeanMapping<B> mapping) {
        init(mapping);
    }

    public BeanHandler<B> init(Property<B, Object> id) {
        this.id = id;
        return this;
    }

    public BeanHandler<B> init(BeanMapping<B> mapping) {
        if (mapping.id() == null) {
            throw new PersistenceException("No id mapping in " + mapping);
        }
        return init(mapping.id());
    }

    public BeanHandler<B> init(Collection<B>... col) {
        Class<B> clazz = inferClass(col);
        if (clazz == null) {
            return null;
        }
        return init(BeanMappings.get(clazz));
    }

    @SuppressWarnings("unchecked")
    private static <B> Class<B> inferClass(Collection<B>[] col) {
        for (Collection<B> c : col) {
            if (c != null && !c.isEmpty()) {
                return (Class<B>) c.iterator().next().getClass();
            }
        }
        return null;
    }

    @Override
    public Object id(B b) {
        return id.read(b);
    }

    @Override
    public B initItem(B b) {
        return b;
    }
}
