package com.voxai.core.mapper;

import com.voxai.core.entity.VdnDtmf;
import com.voxai.core.mapper.base.BaseMapper;

import java.util.List;

public interface VdnDtmfMapper extends BaseMapper<VdnDtmf> {

    List<VdnDtmf> selectByNavagite(Long id);

}