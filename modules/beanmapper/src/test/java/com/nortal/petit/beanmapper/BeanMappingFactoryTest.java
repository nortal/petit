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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.junit.Assert;
import org.junit.Test;

import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.beanmapper.BeanMappingFactoryImpl;
import com.nortal.petit.beanmapper.BeanMappings;
import com.nortal.petit.beanmapper.Property;

/**
 * @author Aleksei Lissitsin
 * 
 */
public class BeanMappingFactoryTest {

    @Test
    public void testCachePropertyAnnotationsProperty_false() {
        BeanMappingFactoryImpl bmf = new BeanMappingFactoryImpl();
        BeanMapping<Bean> bean1 = bmf.create(Bean.class);
        Property<Bean, Object> prop1 = bean1.props().get("idField");

        Assert.assertTrue("Property annotations are not empty", prop1.getConfiguration().getAnnotations().isEmpty());

        System.setProperty("com.nortal.persistence.useAdditionalConfiguration", "true");
        BeanMappingFactoryImpl bmf2 = new BeanMappingFactoryImpl();
        BeanMapping<Bean> bean2 = bmf2.create(Bean.class);
        Property<Bean, Object> prop2 = bean2.props().get("idField");

        Assert.assertTrue("Property annotations are empty", !prop2.getConfiguration().getAnnotations().isEmpty());
    }

    @Test
    public void testColumnNames() {
        BeanMapping<Bean> beanMapping = BeanMappings.get(Bean.class);

        @SuppressWarnings("serial")
        Map<String, String> columnMap = new HashMap<String, String>() {
            {
                put("idField", "f_id_field");
                put("superProp", "g_super_prop");
                put("emb.foo", "over_foo");
                put("emb.bar", "em_f_bar");
                put("emb.kar", "em_classover_kar");
            }
        };

        Assert.assertEquals(beanMapping.props().size(), columnMap.size());
        for (Entry<String, Property<Bean, Object>> e : beanMapping.props().entrySet()) {
            Assert.assertEquals(columnMap.get(e.getKey()), e.getValue().column());
        }
    }

    public static class Bean extends SuperBean {
        @Id
        @Column(name = "f_id_field")
        private Long idField;

        @Embedded
        @AttributeOverride(name = "foo", column = @Column(name = "over_foo"))
        @AttributeOverrides({ @AttributeOverride(name = "foo", column = @Column(name = "overs_foo")),
                @AttributeOverride(name = "foo2", column = @Column(name = "overs_foo")) })
        @Column(name = "em")
        private EmBean emb;

        @Column(name = "g_id_field")
        public Long getIdField() {
            return idField;
        }

        public void setIdField(Long idField) {
            this.idField = idField;
        }

        public EmBean getEmb() {
            return emb;
        }

        public void setEmb(EmBean emb) {
            this.emb = emb;
        }
    }

    @AttributeOverride(name = "kar", column = @Column(name = "classover_kar"))
    public static class EmBean {
        private Long foo;
        @Column(name = "f_bar")
        private Long bar;

        private Long kar;

        private Long transMutant;

        @Transient
        public Long getTransMutant() {
            return transMutant;
        }

        public void setTransMutant(Long transMutant) {
            this.transMutant = transMutant;
        }

        public Long getKar() {
            return kar;
        }

        public void setKar(Long kar) {
            this.kar = kar;
        }

        public Long getFoo() {
            return foo;
        }

        public void setFoo(Long foo) {
            this.foo = foo;
        }

        public Long getBar() {
            return bar;
        }

        public void setBar(Long bar) {
            this.bar = bar;
        }
    }

    public static class SuperBean {
        @Column(name = "f_id_field2")
        private Long idField;

        @Column(name = "f_super_prop")
        private Long superProp;

        private Long superProp2;

        @Transient
        public Long getSuperProp2() {
            return superProp2;
        }

        public void setSuperProp2(Long superProp2) {
            this.superProp2 = superProp2;
        }

        @Column(name = "g_id_field2")
        public Long getIdField() {
            return idField;
        }

        public void setIdField(Long idField) {
            this.idField = idField;
        }

        @Column(name = "g_super_prop")
        public Long getSuperProp() {
            return superProp;
        }

        public void setSuperProp(Long superProp) {
            this.superProp = superProp;
        }
    }

}
