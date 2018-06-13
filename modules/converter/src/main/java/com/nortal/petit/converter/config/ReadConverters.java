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
package com.nortal.petit.converter.config;

import java.lang.reflect.Type;

import com.nortal.petit.converter.CompositeConverter;
import com.nortal.petit.converter.Converter;

public class ReadConverters extends Converters {

    @Override
    protected Type getKey(Converter<?, ?> converter) {
        return converter.getToType();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Converter<?, ?> get(Type to) {
        Converter<?, ?> converter = super.get(to);
        if (converter != null && !to.equals(converter.getFromType())) {
            Converter<?, ?> converter2 = get(converter.getFromType());
            if (converter2 != null) {
                return new CompositeConverter(converter2, converter);
            }
        }
        return converter;
    }
}
