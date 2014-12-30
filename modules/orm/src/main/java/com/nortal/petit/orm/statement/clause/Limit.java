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
package com.nortal.petit.orm.statement.clause;

/**
 * Query limit data
 * 
 * @author Alrik Peets
 */
public class Limit {

    /**
     * Start query from n-th row
     */
    private int start;
    /**
     * How many rows to query
     */
    private int count;

    public Limit(int start, int count) {
        this.start = start;
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int limit) {
        this.count = limit;
    }

    public int getFirstElementPosition() {
        return start >= 0 ? start : 0;
    }

    public int getLastElementPosition() {
        return (start >= 0 ? start : 0) + (count >= 0 ? count : 0);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + count;
        result = prime * result + start;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Limit other = (Limit) obj;
        if (count != other.count)
            return false;
        if (start != other.start)
            return false;
        return true;
    }
}
