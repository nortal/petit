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
package com.nortal.petit.orm.persist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Aleksei Lissitsin
 */
public class CollectionPersistUtil {

    public static <B> void save(Collection<B> beans, ItemHandler<B> handler, CollectionPersistMethods<B> methods) {
        if (beans == null || beans.isEmpty()) {
            return;
        }

        List<B> newBeans = new ArrayList<B>();
        List<B> oldBeans = new ArrayList<B>();

        for (B b : beans) {
            if (handler.id(b) == null) {
                newBeans.add(handler.initItem(b));
            } else {
                oldBeans.add(b);
            }
        }

        if (!newBeans.isEmpty()) {
            methods.insert(newBeans);
        }

        if (!oldBeans.isEmpty()) {
            methods.update(oldBeans);
        }
    }

    public static <B> void persist(Collection<B> newState, Collection<B> oldState, ItemHandler<B> info,
            CollectionPersistMethods<B> methods) {
        Collection<B> deleted = findDeleted(newState, oldState, info);

        save(newState, info, methods);
        if (deleted != null) {
            methods.delete(deleted);
        }
    }

    private static <B> Collection<B> findDeleted(Collection<B> newState, Collection<B> oldState, ItemHandler<B> handler) {
        if (oldState == null || oldState.isEmpty()) {
            return null;
        }

        if (newState == null || newState.isEmpty()) {
            return oldState;
        }

        Set<Object> ids = new HashSet<Object>();
        for (B b : newState) {
            ids.add(handler.id(b));
        }

        List<B> res = new ArrayList<B>();
        for (B b : oldState) {
            if (!ids.contains(handler.id(b))) {
                res.add(b);
            }
        }

        return res;
    }
}
