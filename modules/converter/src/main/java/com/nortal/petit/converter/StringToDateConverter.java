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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.Validate;

/**
 * @author Margus Hanni
 */
public class StringToDateConverter extends BaseFromStringConverter<Date> {
    public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy";

    private String dateFormat;

    public StringToDateConverter() {
        this(DEFAULT_DATE_FORMAT);
    }

    public StringToDateConverter(String dateFormat) {
        super(Date.class);
        Validate.notEmpty(dateFormat);
        this.dateFormat = dateFormat;
    }

    @Override
    public Date convertNotBlank(String value) {
        try {
            return new SimpleDateFormat(dateFormat).parse(value);
        } catch (ParseException e) {
            return null;
        }
    }
}
