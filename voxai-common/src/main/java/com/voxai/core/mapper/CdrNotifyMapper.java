package com.voxai.core.mapper;

import com.voxai.core.entity.CdrNotify;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface CdrNotifyMapper {
    @Insert("insert into cc_cdr_notify (call_id, company_id, cdr_notify_url, content, next_attempt_at) " +
            "values (#{callId}, #{companyId}, #{cdrNotifyUrl}, #{content}, #{cts}) " +
            "on duplicate key update call_id = values(call_id)")
    int enqueue(CdrNotify notify);

    @Select("select call_id as id, call_id as callId, company_id as companyId, cdr_notify_url as cdrNotifyUrl, " +
            "content, push_times as pushTimes from cc_cdr_notify " +
            "where status = 1 and next_attempt_at <= #{now} and lease_until <= #{now} " +
            "order by next_attempt_at, call_id limit 100")
    List<CdrNotify> pending(@Param("now") long now);

    @Update("update cc_cdr_notify set lease_until = #{leaseUntil}, lease_token = #{leaseToken}, push_times = push_times + 1 " +
            "where call_id = #{callId} and status = 1 and next_attempt_at <= #{cts} and lease_until <= #{cts} " +
            "and push_times = #{pushTimes}")
    int claim(CdrNotify notify);

    @Update("update cc_cdr_notify set status = #{status}, push_response = #{pushResponse}, " +
            "next_attempt_at = #{uts}, lease_until = 0, lease_token = '' " +
            "where call_id = #{callId} and lease_token = #{leaseToken}")
    int finish(CdrNotify notify);
}
