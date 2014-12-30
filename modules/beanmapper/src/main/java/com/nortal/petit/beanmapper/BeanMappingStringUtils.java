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

import org.apache.commons.lang3.StringUtils;

/**
 * @author Aleksei Lissitsin
 * 
 */
class BeanMappingStringUtils {
    /**
     * Transforms the given camel case string into it's underscore
     * representation. Example: someString -&gt; some_string.
     * 
     * @param camelCase
     *            string in camel case.
     * @return string in underscore representation.
     */
    public static String camelCaseToUnderscore(String camelCase) {
        if (StringUtils.isBlank(camelCase)) {
            return camelCase;
        }

        StringBuilder sb = new StringBuilder();

        for (Character c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c)) {
                c = Character.toLowerCase(c);
                if (sb.length() > 0) {
                    sb.append("_");
                }
            }

            sb.append(c);
        }

        return sb.toString();
    }
}
