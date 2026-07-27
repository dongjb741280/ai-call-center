/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.GroupMemoryConfig;
import com.voxai.core.mapper.base.BaseMapper;

public interface GroupMemoryConfigMapper extends BaseMapper<GroupMemoryConfig> {

    GroupMemoryConfig selectByGroupId(Long groupId);
}