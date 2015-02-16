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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains additional information about the property.
 * The gathering of extra data can be disabled by setting environment variable
 * <code>com.nortal.persistence.useAdditionalConfiguration</code> to <code>false</code>
 * 
 * @author Alrik Peets
 */
public class PropertyConfiguration {
    private List<Annotation> annotations;
    private Type[] collectionTypeArguments;

    /**
     * Returns all annotations associated with the property
     * 
     * @return
     */
    public List<Annotation> getAnnotations() {
        if (annotations == null) {
            annotations = new ArrayList<Annotation>();
        }
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return BeanMappingReflectionUtils.getAnnotation(getAnnotations(), annotationType);
    }

    /**
     * If property is of {@link Collection} type returns the generic types of the Collection
     * 
     * @return
     */
    public Type[] getCollectionTypeArguments() {
        return collectionTypeArguments;
    }

    public void setCollectionTypeArguments(Type[] collectionTypeArguments) {
        this.collectionTypeArguments = collectionTypeArguments;
    }
}
