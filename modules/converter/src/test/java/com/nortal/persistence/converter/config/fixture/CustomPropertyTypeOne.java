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
package com.nortal.persistence.converter.config.fixture;


import com.nortal.petit.converter.BaseFromStringConverter;
import com.nortal.petit.converter.Converter;


public class CustomPropertyTypeOne {

    private Long val;

    public CustomPropertyTypeOne() {
    }

    CustomPropertyTypeOne(Long val) {
        this.val = val;
    }

    public Long getVal() {
        return val;
    }

    public void setVal(Long val) {
        this.val = val;
    }


    public static Converter<String, CustomPropertyTypeOne> createConverter() {
        return new BaseFromStringConverter<CustomPropertyTypeOne>(CustomPropertyTypeOne.class) {
            @Override
            protected CustomPropertyTypeOne convertNotBlank(String value) {
                return new CustomPropertyTypeOne(Long.parseLong(value));
            }
        };
    }

}
