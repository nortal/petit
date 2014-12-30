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

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeanUtils;

/**
 * @author Aleksei Lissitsin
 * 
 */
class BeanMappingReflectionUtils {

    private static Column getAttributeOverride(Class<?> type, String name) {
        AttributeOverride ao = type.getAnnotation(AttributeOverride.class);
        if (ao != null) {
            if (ao.name().equals(name)) {
                return ao.column();
            }
        }

        AttributeOverrides aos = type.getAnnotation(AttributeOverrides.class);
        if (aos != null) {
            for (AttributeOverride a : aos.value()) {
                if (a.name().equals(name)) {
                    return a.column();
                }
            }
        }

        return null;
    }

    private static void addAll(List<Annotation> l, Annotation[] ans) {
        if (ans != null) {
            for (Annotation a : ans) {
                if (a instanceof AttributeOverrides) {
                    l.addAll(Arrays.asList(((AttributeOverrides) a).value()));
                } else {
                    l.add(a);
                }
            }
        }
    }

    private static void readAnnotations(List<Annotation> l, Class<?> type, String name) {
        Column ao = getAttributeOverride(type, name);
        if (ao != null) {
            l.add(ao);
        }
        Field field = FieldUtils.getDeclaredField(type, name, true);
        if (field != null) {
            addAll(l, field.getAnnotations());
        }
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(type, name);
        if (pd != null) {
            if (pd.getReadMethod() != null) {
                addAll(l, pd.getReadMethod().getAnnotations());
            }
        }
        if (type.getSuperclass() != null) {
            readAnnotations(l, type.getSuperclass(), name);
        }
    }

    static List<Annotation> readAnnotations(Class<?> type, String name) {
        List<Annotation> res = new ArrayList<Annotation>();
        readAnnotations(res, type, name);
        return res;
    }

    @SuppressWarnings("unchecked")
    static <B extends Annotation> B getAnnotation(List<Annotation> ans, Class<B> annotationType) {
        if (ans != null) {
            for (Annotation a : ans) {
                if (a.annotationType().isAssignableFrom(annotationType)) {
                    return (B) a;
                }
            }
        }
        return null;
    }

    static Column getAttributeOverride(List<Annotation> ans, String name) {
        if (ans != null) {
            for (Annotation a : ans) {
                if (a instanceof AttributeOverride) {
                    AttributeOverride ao = (AttributeOverride) a;
                    if (ao.name().equals(name)) {
                        return ao.column();
                    }
                }
            }
        }
        return null;
    }
}
