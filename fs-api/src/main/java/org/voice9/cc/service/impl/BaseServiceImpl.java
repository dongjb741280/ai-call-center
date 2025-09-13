package org.voice9.cc.service.impl;

import com.voice9.core.mapper.base.BaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.voice9.cc.service.BaseService;

/**
 * Create by dongjb on 2025/09/03
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
