package com.voxai.cc.service.impl;

import com.voxai.core.mapper.base.BaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.voxai.cc.service.BaseService;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public abstract class BaseServiceImpl<T> implements BaseService<T> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    abstract BaseMapper<T> baseMapper();

    @Override
    public int add(T record) {
        return baseMapper().insertSelective(record);
    }

    @Override
    public int deleteById(Long id) {
        return baseMapper().deleteByPrimaryKey(id);
    }

    @Override
    public int editById(T record) {
        return baseMapper().updateByPrimaryKeySelective(record);
    }

    @Override
    public T findById(Long id) {
        return baseMapper().selectByPrimaryKey(id);
    }



}
