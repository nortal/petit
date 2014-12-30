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
package com.nortal.petit.converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Example initialization of ConverterFactory:
 * 
 * <p>
 * <blockquote>
 * 
 * <pre>
 * static {
 *     ResourceProcessor processor = new ResourceProcessor(Sets.newHashSet(COMMON_PACKAGE), null, GroupId.class) {
 *         protected void processResource(GenericBeanDefinition classDef) throws ClassNotFoundException,
 *                 InstantiationException, IllegalAccessException {
 *             ClassFile classFile = ResourceProcessor.readClassFile(classDef.getResource());
 * 
 *             AnnotationsAttribute annotationsAttr = (AnnotationsAttribute) classFile
 *                     .getAttribute(AnnotationsAttribute.visibleTag);
 * 
 *             Annotation groupAnnotation = annotationsAttr.getAnnotation(GroupId.class.getName());
 *             ArrayMemberValue memberValue = (ArrayMemberValue) groupAnnotation.getMemberValue(&quot;groups&quot;);
 * 
 *             MemberValue[] groupValues = memberValue.getValue();
 * 
 *             if (!ArrayUtils.isEmpty(groupValues)) {
 *                 Class&lt;?&gt; containerType = Class.forName(classFile.getName());
 * 
 *                 ConverterGroup group = (ConverterGroup) containerType.newInstance();
 * 
 *                 for (MemberValue groupValue : groupValues) {
 *                     StringMemberValue groupMember = (StringMemberValue) groupValue;
 *                     ConverterFactory.register(groupMember.getValue(), group);
 *                 }
 *             }
 *         }
 *     };
 *     processor.run();
 * }
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @author Roman Tekhov
 */
public class ConverterFactory {

    private static final Map<String, ConverterContainer> GROUP_MAPPING = new HashMap<String, ConverterContainer>();

    protected static void register(String groupId, ConverterGroup group) {
        ConverterContainer container = GROUP_MAPPING.get(groupId);

        if (container == null) {
            container = new ConverterContainer();
            GROUP_MAPPING.put(groupId, container);
        }

        container.add(group);
    }

    public static ConverterContainer getContainer(String key) {
        return GROUP_MAPPING.get(key);
    }

    public static <F, T> Converter<F, T> getConverter(String key, Class<F> fromType, Class<T> toType) {
        ConverterContainer container = GROUP_MAPPING.get(key);

        if (container == null) {
            return null;
        }

        return container.get(fromType, toType);
    }

    @SuppressWarnings("unchecked")
    public static <T> Converter<Object, String> getObjectConverter(String key, Class<?> fromType, Class<T> toType) {

        return (Converter<Object, String>) getConverter(key, fromType, toType);
    }

}
