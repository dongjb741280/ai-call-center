package com.voxai.api.web;

import com.voxai.api.configration.interceptor.HttpRequestInteceptor;
import com.voxai.api.exception.BusinessException;
import com.voxai.core.enums.ErrorCode;
import com.voxai.core.mapper.CallLogMapper;
import com.voxai.core.po.AdminAccountInfo;
import com.voxai.core.po.CallLogPo;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.http.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CallRecordingControllerTest {
    private final CallLogMapper calls = mock(CallLogMapper.class);
    private final MinioClient minio = mock(MinioClient.class);
    private final CallRecordingController controller = new CallRecordingController();
    private final AdminAccountInfo account = new AdminAccountInfo();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final CallLogPo call = new CallLogPo();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "callLogMapper", calls);
        ReflectionTestUtils.setField(controller, "minioClient", minio);
        account.setBindCompanyId(10L);
        call.setCompanyId(10L);
        call.setRecord("/cc-record/202611/test.wav");
    }

    @Test
    void signsOnlyTheTenantsStoredObjectForFiveMinutes() throws Exception {
        when(calls.getCall(10L, 100L, "")).thenReturn(call);
        when(minio.getPresignedObjectUrl(any())).thenReturn("http://minio/cc-record/test.wav?signed=1");
        assertEquals("http://minio/cc-record/test.wav?signed=1", controller.recordingUrl(account, 100L, response).getData());
        ArgumentCaptor<StatObjectArgs> stat = ArgumentCaptor.forClass(StatObjectArgs.class);
        verify(minio).statObject(stat.capture());
        assertEquals("cc-record", stat.getValue().bucket());
        assertEquals("202611/test.wav", stat.getValue().object());
        ArgumentCaptor<GetPresignedObjectUrlArgs> args = ArgumentCaptor.forClass(GetPresignedObjectUrlArgs.class);
        verify(minio).getPresignedObjectUrl(args.capture());
        assertEquals("cc-record", args.getValue().bucket());
        assertEquals("202611/test.wav", args.getValue().object());
        assertEquals(Method.GET, args.getValue().method());
        assertEquals(300, args.getValue().expiry());
        assertEquals("no-store", response.getHeader("Cache-Control"));
    }

    @Test
    void refusesMissingCallsAndOtherTenantsRecordings() {
        assertUnavailable();
        when(calls.getCall(10L, 100L, "")).thenReturn(call);
        call.setCompanyId(20L);
        assertUnavailable();
        verifyNoInteractions(minio);
    }

    @Test
    void refusesMissingTenantBeforeQuerying() {
        account.setBindCompanyId(null);
        assertUnavailable();
        verifyNoInteractions(calls, minio);
    }

    @Test
    void refusesEmptyLocalAndExternalPaths() {
        when(calls.getCall(10L, 100L, "")).thenReturn(call);
        for (String path : new String[]{null, "", "/cc-record/", "/Users/fs/record.wav", "https://other/record.wav"}) {
            call.setRecord(path);
            assertUnavailable();
        }
        verifyNoInteractions(minio);
    }

    @Test
    void missingObjectDoesNotReturnABrokenSignedUrl() throws Exception {
        when(calls.getCall(10L, 100L, "")).thenReturn(call);
        when(minio.statObject(any())).thenThrow(new java.io.IOException("missing object"));
        assertUnavailable();
        verify(minio, never()).getPresignedObjectUrl(any());
    }

    @Test
    void missingStorageConfigurationReturnsUnavailable() {
        when(calls.getCall(10L, 100L, "")).thenReturn(call);
        ReflectionTestUtils.setField(controller, "minioClient", null);
        assertUnavailable();
    }

    @Test
    void signingFailureReturnsAnExplicitError() throws Exception {
        when(calls.getCall(10L, 100L, "")).thenReturn(call);
        when(minio.getPresignedObjectUrl(any())).thenThrow(new java.io.IOException("storage failure"));
        assertUnavailable();
    }

    @Test
    void unauthenticatedHttpRequestCannotObtainAPlaybackUrl() throws Exception {
        MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(new HttpRequestInteceptor(null, null, null)).build()
                .perform(get("/config/callRecording/100")).andExpect(status().isUnauthorized());
        verifyNoInteractions(calls, minio);
    }

    private void assertUnavailable() {
        BusinessException error = assertThrows(BusinessException.class, () -> controller.recordingUrl(account, 100L, response));
        assertEquals(ErrorCode.FILE_NOT_EXIST.getCode(), error.getErrorCode());
        assertEquals("录音文件暂不可用", error.getMessage());
    }
}
