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
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

public class BeanMappingUtils {

    /**
     * Returns the initialized property.
     * In case of Embedded property the root property is returned for reference. Embedde properties themselves are expanded
     * in the props variable.
     * 
     * Null is returned if the property is not valid (either not readable/writable or Transient)
     * 
     * @param props
     * @param name
     * @param type
     */
    public static <B> Property<B, Object> initProperty(Map<String, Property<B, Object>> props, String name, Class<B> type) {
        PropertyDescriptor pd = BeanMappingReflectionUtils.getPropertyDescriptor(type, name);

        if (!isPropertyReadableAndWritable(pd)) {
            return null;
        }

        List<Annotation> ans = BeanMappingReflectionUtils.readAnnotations(type, pd.getName());

        if (BeanMappingReflectionUtils.getAnnotation(ans, Transient.class) != null) {
            return null;
        }

        Column column = BeanMappingReflectionUtils.getAnnotation(ans, Column.class);

        ReflectionProperty<B, Object> prop = new ReflectionProperty<B, Object>(name,
                (Class<Object>) pd.getPropertyType(), inferColumn(name, column), pd.getWriteMethod(),
                pd.getReadMethod());

        if (column != null) {
            prop.readOnly(!column.insertable());
        }

        if (BeanMappingReflectionUtils.getAnnotation(ans, Id.class) != null) {
            prop.setIdProperty(true);
        }

        if (useAdditionalConfiguration()) {
            prop.getConfiguration().setAnnotations(ans);
            if (Collection.class.isAssignableFrom(pd.getPropertyType())) {
                prop.getConfiguration().setCollectionTypeArguments(
                        ((ParameterizedType) pd.getReadMethod().getGenericReturnType()).getActualTypeArguments());
            }
        }

        if (BeanMappingReflectionUtils.getAnnotation(ans, Embedded.class) != null) {
            props.putAll(getCompositeProperties(prop, ans));
        } else {
            props.put(prop.name(), prop);
        }
        
        return prop;
    }

    /**
     * Adds an extended property to the BeanMapping. 
     * 
     * @param props
     * @param name
     * @param type
     * @param columnMapping
     * @return
     */
    public static <B> Property<B, Object> initExtendedProperty(Map<String, Property<B, Object>> props, String name, Class<B> type, String columnMapping) {
        PropertyDescriptor pd = BeanMappingReflectionUtils.getPropertyDescriptor(type, name);

        if (!isPropertyReadableAndWritable(pd)) {
            return null;
        }

        List<Annotation> ans = BeanMappingReflectionUtils.readAnnotations(type, pd.getName());

        Column column = BeanMappingReflectionUtils.getAnnotation(ans, Column.class);

        ReflectionProperty<B, Object> prop = new ReflectionProperty<B, Object>(name,
                (Class<Object>) pd.getPropertyType(), inferColumn(columnMapping != null ? columnMapping : name, column), pd.getWriteMethod(),
                pd.getReadMethod());

        if (column != null) {
            prop.readOnly(true);
        }

        if (BeanMappingReflectionUtils.getAnnotation(ans, Id.class) != null) {
            prop.setIdProperty(true);
        }

        if (useAdditionalConfiguration()) {
            prop.getConfiguration().setAnnotations(ans);
            if (Collection.class.isAssignableFrom(pd.getPropertyType())) {
                prop.getConfiguration().setCollectionTypeArguments(
                        ((ParameterizedType) pd.getReadMethod().getGenericReturnType()).getActualTypeArguments());
            }
        }

        if (BeanMappingReflectionUtils.getAnnotation(ans, Embedded.class) != null) {
            props.putAll(getCompositeProperties(prop, ans));
        } else {
            props.put(prop.name(), prop);
        }
        
        return prop;
    }
    
    private static boolean isPropertyReadableAndWritable(PropertyDescriptor pd) {
        if (pd == null || pd.getReadMethod() == null || pd.getWriteMethod() == null) {
            return false;
        }

        return true;
    }

    private static <B> Map<String, Property<B, Object>> getCompositeProperties(Property<B, Object> prop, List<Annotation> ans) {
        Map<String, Property<B, Object>> map = new LinkedHashMap<String, Property<B, Object>>();
        BeanMapping<Object> beanMapping = BeanMappings.get(prop.type());
        for (Property<Object, Object> p : beanMapping.props().values()) {
            Column column = BeanMappingReflectionUtils.getAttributeOverride(ans, p.name());
            CompositeProperty<B, Object, Object> cp = new CompositeProperty<B, Object, Object>(prop, p,
                    column != null ? column.name() : prop.column() + "_" + p.column());
            if (column != null) {
                cp.readOnly(!column.insertable());
            }
            map.put(cp.name(), cp);
        }
        return map;
    }

    private static String inferColumn(String name, Column column) {
        if (column != null && StringUtils.isNotEmpty(column.name())) {
            return column.name();
        }
        return BeanMappingStringUtils.camelCaseToUnderscore(name);
    }

    private static boolean useAdditionalConfiguration() {
        String property = System.getProperty(BeanMapping.USE_ADDITIONAL_CONFIGURATION_KEY, "true");
        return Boolean.valueOf(property);
    }
    
}
