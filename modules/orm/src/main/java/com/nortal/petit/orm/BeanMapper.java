package com.nortal.petit.orm;

import org.springframework.jdbc.core.RowMapper;

import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.beanmapper.ResultSetReader;

public class BeanMapper<B> extends com.nortal.petit.beanmapper.BeanMapper<B> implements RowMapper<B> {

    public BeanMapper(BeanMapping<B> mapping, ResultSetReader resultSetReader) {
        super(mapping, resultSetReader);
    }
}
