/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.VdnConfig;
import com.voxai.core.mapper.base.BaseMapper;
import com.voxai.core.po.VdnSchedulePo;

import java.util.List;

public interface VdnConfigMapper extends BaseMapper<VdnConfig> {

    List<VdnSchedulePo> selectByVdn(Long id);

}