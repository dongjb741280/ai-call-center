/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.Station;
import com.voxai.core.mapper.base.BaseMapper;

public interface StationMapper extends BaseMapper<Station> {

    Station selectByAppId(Integer applicationId);
}