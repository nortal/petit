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

import javax.persistence.Column;


public class ConversionTestBean {

    @Column(name = "db_field")
    private String testField;
    private CustomPropertyTypeOne customTypeFieldOne;
    private CustomPropertyTypeTwo customTypeFieldTwo;

    public String getTestField() {
        return testField;
    }

    public void setTestField(String testField) {
        this.testField = testField;
    }


    public CustomPropertyTypeOne getCustomTypeFieldOne() {
        return customTypeFieldOne;
    }

    public void setCustomTypeFieldOne(CustomPropertyTypeOne customTypeFieldOne) {
        this.customTypeFieldOne = customTypeFieldOne;
    }

    public CustomPropertyTypeTwo getCustomTypeFieldTwo() {
        return customTypeFieldTwo;
    }

    public void setCustomTypeFieldTwo(CustomPropertyTypeTwo customTypeFieldTwo) {
        this.customTypeFieldTwo = customTypeFieldTwo;
    }
}
