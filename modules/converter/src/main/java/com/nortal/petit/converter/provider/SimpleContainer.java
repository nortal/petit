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
package com.nortal.petit.converter.provider;

import java.util.HashMap;
import java.util.Map;

public class SimpleContainer<K, V> implements Container<K, V> {
	protected Map<K, V> map = new HashMap<>();

	@Override
	public V get(K key) {
		return map.get(key);
	}
	
	public Map<K, V> getAll() {
		return map;
	}

	@Override
	public void put(K key, V value) {
		map.put(key, value);
	}

	@Override
	public void putAll(Map<K, V> values) {
		map.putAll(values);
	}
}
