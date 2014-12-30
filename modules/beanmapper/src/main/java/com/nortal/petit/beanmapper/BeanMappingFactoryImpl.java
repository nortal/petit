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
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.PersistenceException;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * The standard BeanMapping factory. Constructs BeanMapping for a given class.
 * Understands the following JPA annotations:
 * <p>
 * {@link Table} - specifies table mapping via the name attribute, if not present underscored simple name of the bean
 * class is used instead;
 * <p>
 * {@link Column} - the name attribute specifies the column mapping of a property (if not present, underscored name of
 * the property is used instead), the insertable attribute controls the readOnly flag of the property (by default,
 * readOnly is false, insertable=false would change it readOnly = true). If Column is not present, the property is
 * mapped anyway. To ignore a property, use the Transient annotation
 * <p>
 * {@link Transient} - marks that the specified property should not be mapped.
 * <p>
 * {@link Id} - marks the property as the Id property.
 * <p>
 * {@link Embedded} - marks that the property is an embedded bean: adds all properties of the embedded bean as composite
 * properties to the current bean
 * <p>
 * {@link AttributeOverride}, {@link AttributeOverrides} - allows for overriding name->column mappings of an embedded
 * subbean.
 * 
 * You can control whether properties should also collect other annotations into PropetyConfiguration object via
 * useAdditionalConfiguration property of the factory (default is false). It is also automatically initiated by
 * com.nortal.persistence.useAdditionalConfiguration system property.
 * 
 * If custom beanmapping creation is needed, users can either extend this class or provide a completely different
 * implementation of BeanMappingFactory to BeanMappings.
 * 
 * @author Aleksei Lissitsin
 * 
 */
public class BeanMappingFactoryImpl implements BeanMappingFactory {

    private static final String USE_ADDITIONAL_CONFIGURATION_KEY = "com.nortal.persistence.useAdditionalConfiguration";

    private boolean useAdditionalConfiguration = false;

    public BeanMappingFactoryImpl() {
        String property = System.getProperty(USE_ADDITIONAL_CONFIGURATION_KEY, "true");
        useAdditionalConfiguration((Boolean.valueOf(property)));
    }

    protected boolean useAdditionalConfiguration() {
        return useAdditionalConfiguration;
    }

    public void useAdditionalConfiguration(boolean useAdditionalConfiguration) {
        this.useAdditionalConfiguration = useAdditionalConfiguration;
    }

    @Override
    public <B> BeanMapping<B> create(Class<B> type) {
        Map<String, Property<B, Object>> props = new LinkedHashMap<String, Property<B, Object>>();
        List<Property<B, Object>> idProps = new ArrayList<Property<B, Object>>();
        initProps(props, idProps, type);

        if (idProps.size() > 1) {
            throw new PersistenceException("Multiple primary keys @Id defined for model " + type.getSimpleName());
        }

        SimpleBeanMapping<B> m = new SimpleBeanMapping<B>(type, inferTable(type), props);

        if (idProps.size() == 1) {
            m.id(idProps.get(0));
        }

        return m;
    }

    public String inferTable(Class<?> type) {
        Table table = AnnotationUtils.findAnnotation(type, Table.class);
        if (table != null && !StringUtils.isBlank(table.name())) {
            return (StringUtils.isNotBlank(table.schema()) ? (table.schema() + ".") : "") + table.name();
        }
        return BeanMappingStringUtils.camelCaseToUnderscore(type.getSimpleName());
    }

    public <B> void initProps(Map<String, Property<B, Object>> props, List<Property<B, Object>> idProps, Class<B> type) {
        Collection<PropertyDescriptor> pds = findPropertyDescriptors(type);

        for (PropertyDescriptor pd : pds) {
            initProperty(props, idProps, pd.getName(), type);
        }
    }

    private <B> Collection<PropertyDescriptor> findPropertyDescriptors(Class<B> clazz) {
        Set<PropertyDescriptor> result = new LinkedHashSet<PropertyDescriptor>();

        Collection<String> fieldNames = collectFieldNames(clazz);
        for (String fieldName : fieldNames) {
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(clazz, fieldName);
            if (pd != null) {
                result.add(pd);
            }
        }

        return result;
    }

    private <B> Collection<String> collectFieldNames(Class<B> clazz) {
        Set<String> fieldNames = new HashSet<String>();

        if (clazz.getSuperclass() != null) {
            fieldNames.addAll(collectFieldNames(clazz.getSuperclass()));
        }

        for (Field f : clazz.getDeclaredFields()) {
            fieldNames.add(f.getName());
        }

        return fieldNames;
    }

    @SuppressWarnings("unchecked")
    private <B> void initProperty(Map<String, Property<B, Object>> props, List<Property<B, Object>> idProps,
            String name, Class<B> type) {
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(type, name);

        if (!isPropertyReadableAndWritable(pd)) {
            return;
        }

        List<Annotation> ans = BeanMappingReflectionUtils.readAnnotations(type, pd.getName());

        if (BeanMappingReflectionUtils.getAnnotation(ans, Transient.class) != null) {
            return;
        }

        Column column = BeanMappingReflectionUtils.getAnnotation(ans, Column.class);

        ReflectionProperty<B, Object> prop = new ReflectionProperty<B, Object>(name,
                (Class<Object>) pd.getPropertyType(), inferColumn(name, column), pd.getWriteMethod(),
                pd.getReadMethod());

        if (column != null) {
            prop.readOnly(!column.insertable());
        }

        if (BeanMappingReflectionUtils.getAnnotation(ans, Id.class) != null) {
            idProps.add(prop);
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
    }

    private boolean isPropertyReadableAndWritable(PropertyDescriptor pd) {
        if (pd == null || pd.getReadMethod() == null || pd.getWriteMethod() == null) {
            return false;
        }

        return true;
    }

    protected <B> Map<String, Property<B, Object>> getCompositeProperties(Property<B, Object> prop, List<Annotation> ans) {
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

    protected String inferColumn(String name, Column column) {
        if (column != null && StringUtils.isNotEmpty(column.name())) {
            return column.name();
        }
        return BeanMappingStringUtils.camelCaseToUnderscore(name);
    }
}
