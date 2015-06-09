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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Aleksei Lissitsin
 */
public class BeanMappingCacheTest {
	static class FactoryWrapper implements BeanMappingFactory {
		BeanMappingFactory essence;
		int counter = 0;
		
		FactoryWrapper(BeanMappingFactory essence) {
			this.essence = essence;
		}
		@Override
		public <B> BeanMapping<B> create(Class<B> type) {
			counter++;
			return essence.create(type);
		}
	}
	
	private FactoryWrapper init(boolean cached) {
		FactoryWrapper factory = new FactoryWrapper(new BeanMappingFactoryImpl());
		BeanMappings.setFactory(factory);
		BeanMappings.setCached(cached);
		return factory;
	}
	
	private int counterLong(boolean cached){
		FactoryWrapper factory = init(cached);
		
		new BeanMappingFactoryTest().testColumnNames();
		new BeanMappingFactoryTest().testColumnNames();
		
		return factory.counter;
	}
	
	@Test
	public void testCachedLong(){
		Assert.assertTrue(counterLong(true) <= 2);
	}

	@Test
	public void testUncachedLong(){
		Assert.assertEquals(4, counterLong(false));
	}
	
	private int counter(boolean cached){
		FactoryWrapper factory = init(cached);
		
		new BeanMappingTest().testTable__fullBeanMapping();
		new BeanMappingTest().testTable__fullBeanMapping();
		
		return factory.counter;
	}
	
	@Test
	public void testCached(){
		Assert.assertTrue(counter(true) <= 1);
	}
	
	@Test
	public void testUncached(){
		Assert.assertEquals(2, counter(false));
	}
	

}
