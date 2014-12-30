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

import org.apache.commons.lang3.StringUtils;

/**
 * @author Roman Tekhov
 */
public abstract class BaseFromStringConverter<T> extends BaseNotNullConverter<String, T> {

    protected BaseFromStringConverter(Class<T> toType) {
        super(String.class, toType);
    }

    @Override
    protected T convertNotNull(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return convertNotBlank(value);
    }

    protected abstract T convertNotBlank(String value);

}
