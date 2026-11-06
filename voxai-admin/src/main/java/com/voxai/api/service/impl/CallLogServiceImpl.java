package com.voxai.api.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.voxai.api.vo.excel.ExcelInboundCallLogEntity;
import com.voxai.api.vo.excel.ExcelOutboundCallLogEntity;
import com.voxai.core.mapper.*;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Workbook;
import com.voxai.core.constant.Constant;
import com.voxai.core.entity.CallDevice;
import com.voxai.core.entity.CallLog;
import com.voxai.core.enums.Direction;
import com.voxai.core.enums.ErrorCode;
import com.voxai.core.mapper.base.BaseMapper;
import com.voxai.core.po.CallLogPo;
import com.voxai.core.util.DateTimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.voxai.api.exception.BusinessException;
import com.voxai.api.service.CallLogService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Service
public class CallLogServiceImpl extends BaseServiceImpl<CallLog> implements CallLogService {

    @Autowired
    private CallDeviceMapper callDeviceMapper;

    @Autowired
    private CallLogMapper callLogMapper;

    @Autowired
    private CallDetailMapper callDetailMapper;

    @Autowired
    private CallDtmfMapper callDtmfMapper;

    @Autowired
    private AgentStateLogMapper agentStateLogMapper;

    /**
     * 最大10万下载
     */
    @Value("${calllog.export.limit:100000}")
    private Long calllogLimit;

    @Override
    BaseMapper<CallLog> baseMapper() {
        return callLogMapper;
    }

    @Override
    public PageInfo<CallLogPo> calllogList(Map<String, Object> params) {
        validateMonth(params);
        Long start = (Long) params.get("start");
        Long end = (Long) params.get("end");
        if (start == null) {
            start = DateTimeUtil.getBeforeDay(0);
            params.put("start", start);
        }
        if (end == null) {
            end = DateTimeUtil.getBeforeDay(-1);
            params.put("end", end);
        }
        Integer pageNum = (Integer) params.get("pageNum");
        Integer pageSize = (Integer) params.get("pageSize");
        PageHelper.startPage(pageNum, pageSize);
        List<CallLogPo> list = callLogMapper.getCallList(params);
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(callLogPo -> {
                com.voxai.core.util.CdrTimes.normalize(callLogPo);
                callLogPo.setOssServer(ossServer);
            });
        }
        return new PageInfo<>(list);
    }

    @Override
    public void calllogExport(HttpServletResponse response, Map<String, Object> params) throws IOException {
        validateMonth(params);
        Integer count = callLogMapper.selectCountByMap(params);
        if (count == 0L) {
            new BusinessException(ErrorCode.EXPORT_EMPTY);
        }
        if (count > calllogLimit) {
            new BusinessException(ErrorCode.CALL_LOG_EXPORT_LIMIT);
        }

        String direction = (String) params.getOrDefault("direction", "OUTBOUND");
        params.put("direction", direction);
        List<CallLog> callLogs = callLogMapper.selectListByMap(params);
        /**
         * 呼入和外呼分开，如果不传默认是外呼
         */
        Workbook workbook = null;
        if (Direction.OUTBOUND.name().equals(direction)) {
            List<ExcelOutboundCallLogEntity> entityList = new ArrayList<>();
            ExcelOutboundCallLogEntity entity = null;
            for (CallLog callLog : callLogs) {
                entity = new ExcelOutboundCallLogEntity();
                com.voxai.core.util.CdrTimes.normalize(callLog);
                BeanUtils.copyProperties(callLog, entity);
                entity.setCallTime(DateTimeUtil.format(callLog.getCallTime()));
                entity.setAnswerTime(DateTimeUtil.format(callLog.getAnswerTime()));
                entity.setEndTime(DateTimeUtil.format(callLog.getEndTime()));
                entity.setRecordTime(String.valueOf(callLog.getRecordTime() == null ? 0 : callLog.getRecordTime() / 1000L));
                entityList.add(entity);
            }
            workbook = ExcelExportUtil.exportExcel(new ExportParams(null, direction, ExcelType.XSSF), ExcelOutboundCallLogEntity.class, entityList);
        } else if (Direction.INBOUND.name().equals(direction)) {
            List<ExcelInboundCallLogEntity> entityList = new ArrayList<>();
            ExcelInboundCallLogEntity entity = null;
            for (CallLog callLog : callLogs) {
                entity = new ExcelInboundCallLogEntity();
                com.voxai.core.util.CdrTimes.normalize(callLog);
                BeanUtils.copyProperties(callLog, entity);
                entity.setCallTime(DateTimeUtil.format(callLog.getCallTime()));
                entity.setAnswerTime(DateTimeUtil.format(callLog.getAnswerTime()));
                entity.setEndTime(DateTimeUtil.format(callLog.getEndTime()));
                entity.setRecordTime(String.valueOf(callLog.getRecordTime() == null ? 0 : callLog.getRecordTime() / 1000L));
                entity.setFristQueueTime(DateTimeUtil.format(callLog.getFristQueueTime()));
                entity.setQueueStartTime(DateTimeUtil.format(callLog.getQueueStartTime()));
                entity.setQueueEndTime(DateTimeUtil.format(callLog.getQueueEndTime()));
                entityList.add(entity);
            }
            workbook = ExcelExportUtil.exportExcel(new ExportParams(null, direction, ExcelType.XSSF), ExcelInboundCallLogEntity.class, entityList);
        }
        String filename = URLEncoder.encode(direction + Constant.UNDER_LINE + params.getOrDefault("companyId", 0) + Constant.UNDER_LINE + DateFormatUtils.format(new Date(), "yyyy-MM-dd") + ".xlsx", Constant.UTF_8);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;Filename=" + filename);
        response.setContentType("application/vnd.ms-excel");
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
    }

    @Override
    public int cleaDayOfData(Long end) {
        int result = 0;
        // cc_call_log
        result += callLogMapper.clearTable(end);

        //cc_call_device
        result += callDeviceMapper.clearTable(end);

        //cc_call_detail
        result += callDetailMapper.clearTable(end);

        //cc_call_dtmf
        result += callDtmfMapper.clearTable(end);

        //cc_agent_state_log
        result += agentStateLogMapper.clearTable(end / 1000L);

        return result;
    }

    @Override
    public List<CallDevice> callDeviceList(Map<String, Object> params) {
        Object month = params.get("monthTime");
        if (month != null && !month.toString().isEmpty()) { validateMonthSuffix(month.toString()); }
        List<CallDevice> devices = callDeviceMapper.selectListByMap(params);
        devices.forEach(com.voxai.core.util.CdrTimes::normalize);
        return devices;
    }

    private static void validateMonthSuffix(String month) {
        if (!month.matches("_[0-9]{4}(0[1-9]|1[0-2])")) throw new IllegalArgumentException("Invalid CDR month suffix");
    }

    private void validateMonth(Map<String, Object> params) {
        Object month = params.get("monthTime");
        if (month != null && !month.toString().isEmpty()) {
            validateMonthSuffix(month.toString());
        }
    }


}
