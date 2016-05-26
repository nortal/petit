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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A T-typed property of an S-typed subbean of a B-typed bean considered as a
 * direct property of the bean.
 * 
 * @author Aleksei Lissitsin
 * 
 */
public class CompositeProperty<B, T, S> extends PropertySupport<B, T> {

    private Property<B, S> subBean;

    private Property<S, T> subProp;

    public CompositeProperty(Property<B, S> subBean, Property<S, T> subProp, String column) {
        this.subBean = subBean;
        this.subProp = subProp;
        name = subBean.name() + "." + subProp.name();
        this.column = column;
        type = subProp.type();
        readOnly = subProp.readOnly();
    }

    public void write(B bean, T value) {
        S subBeanObject = subBean.read(bean);
        if (subBeanObject == null) {
            try {
                Type type = subBean.type();
                if (type instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) type;
                    type = pt.getRawType();
                } 
                subBeanObject = ((Class<S>)type).newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            subBean.write(bean, subBeanObject);
        }
        subProp.write(subBeanObject, value);
    }

    public T read(B bean) {
        S subBeanObject = subBean.read(bean);
        if (subBeanObject != null) {
            return subProp.read(subBeanObject);
        } else {
            return null;
        }
    }

    @Override
    public boolean isIdProperty() {
        return false;
    }
}
