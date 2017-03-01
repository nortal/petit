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
package com.nortal.persistence.converter.config.fixture.types;


import com.nortal.petit.converter.BaseFromStringConverter;
import com.nortal.petit.converter.Converter;


public class CustomLongWrapperA {

    private Long val;

    private CustomLongWrapperA(Long val) {
        this.val = val;
    }

    Long getVal() {
        return val;
    }

    public static Converter<String, CustomLongWrapperA> createConverterFromString() {
        return new BaseFromStringConverter<CustomLongWrapperA>(CustomLongWrapperA.class) {
            @Override
            protected CustomLongWrapperA convertNotBlank(String value) {
                return new CustomLongWrapperA(Long.parseLong(value));
            }
        };
    }


}
