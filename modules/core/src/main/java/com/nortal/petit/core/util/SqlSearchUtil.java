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
package com.nortal.petit.core.util;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Anton Stalnuhhin (antons@webmedia.ee)
 */
public class SqlSearchUtil {
    private final static String PERCENT_WILDCARD = "%";

    public static String formatSearchPattern(String pattern) {
        return formatSearchPattern(pattern, SearchType.CONTAINS);
    }

    public static String formatSearchPattern(String pattern, String searchType) {
        if (!EnumUtils.isValidEnum(SearchType.class, searchType)) {
            throw new IllegalArgumentException("Unsupported search type: " + searchType);
        }
        return formatSearchPattern(pattern, SearchType.valueOf(searchType));
    }

    public static String formatSearchPattern(String pattern, SearchType searchType) {
        if (searchType == null) {
            throw new IllegalArgumentException("Search type should be specified.");
        }
        return searchType.getFormattedSearchPattern(pattern);
    }

    public static enum SearchType {
        STARTS_WITH {
            @Override
            public String getFormattedSearchPattern(String pattern) {
                return StringUtils.isBlank(pattern) ? PERCENT_WILDCARD : pattern + PERCENT_WILDCARD;
            }
        },
        ENDS_WITH {
            @Override
            public String getFormattedSearchPattern(String pattern) {
                return StringUtils.isBlank(pattern) ? PERCENT_WILDCARD : PERCENT_WILDCARD + pattern;
            }
        },
        CONTAINS {
            @Override
            public String getFormattedSearchPattern(String pattern) {
                return StringUtils.isBlank(pattern) ? PERCENT_WILDCARD : PERCENT_WILDCARD + pattern + PERCENT_WILDCARD;
            }
        },
        EXACT;

        public String getFormattedSearchPattern(String pattern) {
            return StringUtils.isBlank(pattern) ? "" : pattern;
        }
    }

}
