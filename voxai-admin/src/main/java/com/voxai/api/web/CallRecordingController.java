package com.voxai.api.web;

import com.voxai.api.exception.BusinessException;
import com.voxai.core.enums.ErrorCode;
import com.voxai.core.mapper.CallLogMapper;
import com.voxai.core.po.AdminAccountInfo;
import com.voxai.core.po.CallLogPo;
import com.voxai.core.po.CommonResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.http.Method;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("config/callRecording")
public class CallRecordingController extends BaseController {
    private static final String RECORD_PREFIX = "/cc-record/";

    @Autowired
    private CallLogMapper callLogMapper;

    @Autowired(required = false)
    private MinioClient minioClient;

    @GetMapping("/{callId}")
    public CommonResponse<String> recordingUrl(@ModelAttribute("adminAccountInfo") AdminAccountInfo account,
                                               @PathVariable Long callId, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        Long companyId = account.getBindCompanyId();
        if (companyId == null || callId == null || callId <= 0) {
            throw unavailable();
        }
        // Resolve the object from a tenant-scoped call, never from a client-supplied path.
        CallLogPo call = callLogMapper.getCall(companyId, callId, "");
        if (call == null || !companyId.equals(call.getCompanyId()) || minioClient == null
                || StringUtils.isBlank(call.getRecord()) || !call.getRecord().startsWith(RECORD_PREFIX)) {
            throw unavailable();
        }
        String object = call.getRecord().substring(RECORD_PREFIX.length());
        if (object.isEmpty()) {
            throw unavailable();
        }
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket("cc-record").object(object).build());
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET).bucket("cc-record").object(object).expiry(300).build());
            return new CommonResponse<>(url);
        } catch (Exception error) {
            logger.warn("Recording unavailable for callId:{}", callId);
            throw unavailable();
        }
    }

    private BusinessException unavailable() {
        return new BusinessException(ErrorCode.FILE_NOT_EXIST, "录音文件暂不可用");
    }
}
