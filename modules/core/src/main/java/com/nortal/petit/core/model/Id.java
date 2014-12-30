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
package com.nortal.petit.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable. (And better keep it that way)
 * 
 * @param <T>
 *            marker type, the entity this ID refers to.
 * @author Jevgeni Võssotski
 */
public class Id<T> implements LongId, Comparable<Id<T>> {

    private final long id;

    public long get() {
        return id;
    }

    public Long getLong() {
        return Long.valueOf(id);
    }

    public Id(long id) {
        this.id = id;
    }

    public static <E> Id<E> create(Long id) {
        return (id == null) ? null : new Id<E>(id);
    }

    @Override
    public String toString() {
        return Long.toString(id);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Id && Long.valueOf(((Id<?>) obj).get()).equals(get());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <E> Class<Id<E>> getClazz() {
        return (Class) Id.class;
    }

    /**
     * Convenience method for creating a list of parameterized IDs.
     * 
     * @param longList
     *            A list of IDs
     */
    public static <E> List<Id<E>> toIdList(List<Long> longList) {
        List<Id<E>> idList = new ArrayList<Id<E>>(longList.size());
        for (Long elem : longList) {
            if (elem != null) {
                idList.add(new Id<E>(elem));
            }
        }
        return idList;
    }

    /**
     * Convenience method for creating a list of longs from parameterized IDs.
     * 
     * @param idList
     *            A list of IDs
     */
    public static <E> List<Long> toLongList(List<Id<E>> idList) {
        List<Long> longList = new ArrayList<Long>(idList.size());
        for (Id<E> elem : idList) {
            longList.add(elem.get());
        }
        return longList;
    }

    public int compareTo(Id<T> o) {
        return getLong().compareTo(o.getLong());
    }

}
