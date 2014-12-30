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
package com.nortal.petit.orm.statement;

import com.google.common.base.Function;
import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.converter.Converter;
import com.nortal.petit.converter.ConverterFactory;
import com.nortal.petit.converter.ConverterGroup;

/**
 * @author Aleksei Lissitsin
 * 
 */
public class MappingParamFunction<B> implements Function<String, Object> {
    private BeanMapping<B> mapping;

    private B bean;

    public MappingParamFunction(BeanMapping<B> mapping) {
        this.mapping = mapping;
    }

    public void setBean(B bean) {
        this.bean = bean;
    }

    @Override
    public Object apply(String p) {
        return convert(mapping.props().get(p).read(bean));
    }

    protected Object convert(Object o) {
        if (o == null) {
            return null;
        }
        Converter<Object, String> converter = (Converter<Object, String>) ConverterFactory.getConverter(
                ConverterGroup.DEFAULT_GROUP, o.getClass(), String.class);
        if (converter != null) {
            return converter.convert(o);
        }
        return o;
    }

}
