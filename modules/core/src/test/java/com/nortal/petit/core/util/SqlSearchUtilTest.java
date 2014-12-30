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

import org.junit.Assert;
import org.junit.Test;

import com.nortal.petit.core.util.SqlSearchUtil;

/**
 * @author Vassili Jakovlev (vassili.jakovlev@nortal.com)
 */
public class SqlSearchUtilTest {

    @Test
    public void formatSearchPattern() {
        Assert.assertEquals("foo", SqlSearchUtil.formatSearchPattern("foo", SqlSearchUtil.SearchType.EXACT));
        Assert.assertEquals("foo%", SqlSearchUtil.formatSearchPattern("foo", SqlSearchUtil.SearchType.STARTS_WITH));
        Assert.assertEquals("%foo", SqlSearchUtil.formatSearchPattern("foo", SqlSearchUtil.SearchType.ENDS_WITH));
        Assert.assertEquals("%foo%", SqlSearchUtil.formatSearchPattern("foo", SqlSearchUtil.SearchType.CONTAINS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullSearchType() {
        SqlSearchUtil.formatSearchPattern("foo", (SqlSearchUtil.SearchType) null);
    }

    @Test
    public void nullPattern() {
        Assert.assertEquals("", SqlSearchUtil.formatSearchPattern(null, SqlSearchUtil.SearchType.EXACT));
        Assert.assertEquals("%", SqlSearchUtil.formatSearchPattern(null, SqlSearchUtil.SearchType.STARTS_WITH));
        Assert.assertEquals("%", SqlSearchUtil.formatSearchPattern(null, SqlSearchUtil.SearchType.ENDS_WITH));
        Assert.assertEquals("%", SqlSearchUtil.formatSearchPattern(null, SqlSearchUtil.SearchType.CONTAINS));
    }

    @Test
    public void searchTypeFromString() {
        Assert.assertEquals("foo", SqlSearchUtil.formatSearchPattern("foo", "EXACT"));
        Assert.assertEquals("foo%", SqlSearchUtil.formatSearchPattern("foo", "STARTS_WITH"));
        Assert.assertEquals("%foo", SqlSearchUtil.formatSearchPattern("foo", "ENDS_WITH"));
        Assert.assertEquals("%foo%", SqlSearchUtil.formatSearchPattern("foo", "CONTAINS"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsupportedSearchType() {
        SqlSearchUtil.formatSearchPattern("foo", "unknown-search-type");
    }

    @Test
    public void defaultSearchType() {
        Assert.assertEquals("%foo%", SqlSearchUtil.formatSearchPattern("foo"));
    }

}
