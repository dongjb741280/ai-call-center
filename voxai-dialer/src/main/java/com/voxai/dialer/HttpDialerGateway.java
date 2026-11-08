package com.voxai.dialer;

import com.fasterxml.jackson.databind.*;
import com.voxai.core.entity.TaskConfig;
import com.voxai.core.po.dialer.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Component
@ConditionalOnProperty(name="dialer.enabled",havingValue="true")
public class HttpDialerGateway implements DialerGateway {
    private final DialerProperties properties;
    private final RestTemplate http;
    private final ObjectMapper json;
    public HttpDialerGateway(DialerProperties properties,ObjectMapper json) {
        this.properties=properties; this.json=json;
        SimpleClientHttpRequestFactory factory=new SimpleClientHttpRequestFactory() {
            @Override protected void prepareConnection(java.net.HttpURLConnection connection,String method) throws java.io.IOException {
                super.prepareConnection(connection,method); connection.setInstanceFollowRedirects(false);
            }
        };
        factory.setConnectTimeout(properties.getConnectTimeoutMillis()); factory.setReadTimeout(properties.getReadTimeoutMillis());
        factory.setOutputStreaming(false);
        this.http=new RestTemplate(factory);
    }
    // HTTP redirects are rejected to avoid forwarding either credential to a different origin.
    private JsonNode exchange(boolean admin,String path,HttpMethod method,Object payload) {
        HttpHeaders headers=new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_JSON);
        if (admin) { headers.setBasicAuth(properties.getUsername(),properties.getPassword(),java.nio.charset.StandardCharsets.UTF_8); }
        else { headers.setBearerAuth(properties.getCallToken()); headers.set("X-Company-Id",String.valueOf(properties.getCompanyId())); }
        ResponseEntity<String> response=http.exchange((admin?properties.getAdminBaseUrl():properties.getCallBaseUrl())+path,
                method,new HttpEntity<>(payload,headers),String.class);
        if (!response.getStatusCode().is2xxSuccessful()) { throw new IllegalStateException("Dialer endpoint did not return success"); }
        try {
            JsonNode body=json.readTree(response.getBody());
            if (!admin) { return body; }
            if (body==null || !body.has("code") || !body.get("code").isIntegralNumber() || body.get("code").asInt()!=0) {
                throw new IllegalStateException("Admin rejected dialer operation");
            }
            return body.get("data");
        } catch (java.io.IOException e) { throw new IllegalStateException("Invalid dialer response",e); }
    }
    private <T> T convert(JsonNode value,Class<T> type) { return json.convertValue(value,type); }
    private <T> List<T> list(JsonNode value,Class<T> type) {
        if (value==null || !value.isArray()) { throw new IllegalStateException("Expected a dialer list response"); }
        return json.convertValue(value,json.getTypeFactory().constructCollectionType(List.class,type));
    }
    private String task(Long id) { return "/config/task/"+id; }
    private Map<String,Object> lease(DialerWork work) {
        Map<String,Object> body=new HashMap<>(); body.put("attemptId",work.getAttemptId()); body.put("leaseToken",work.getLeaseToken());
        body.put("requestId",work.getRequestId()); body.put("contactId",work.getContactId()); body.put("leaseSeconds",properties.getLeaseSeconds()); return body;
    }
    private DialerRequest request(DialerWork work) {
        DialerRequest request=new DialerRequest(); request.setAttemptId(work.getAttemptId()); request.setLeaseToken(work.getLeaseToken()); return request;
    }
    @Override public List<TaskConfig> tasks(long afterId) {
        List<TaskConfig> tasks=list(exchange(true,"/config/task/engine?afterId="+afterId+"&limit=100",HttpMethod.GET,null),TaskConfig.class);
        if (tasks.stream().anyMatch(t -> !Objects.equals(properties.getCompanyId(),t.getCompanyId()))) { throw new IllegalStateException("Admin credential does not belong to configured company"); }
        return tasks;
    }
    @Override public List<DialerWork> active(Long id,String after) { return list(exchange(true,task(id)+"/attempt/active?limit=100&afterAttemptId="+after,HttpMethod.GET,null),DialerWork.class); }
    @Override public DialerCapacity capacity(Long id) { return convert(exchange(false,"/internal/dialer/task/"+id+"/capacity",HttpMethod.GET,null),DialerCapacity.class); }
    @Override public void acknowledge(TaskConfig task,boolean success) { exchange(true,task(task.getId())+"/command/"+task.getCommandId()+"/ack?success="+success,HttpMethod.POST,null); }
    @Override public void close(TaskConfig task) {
        Map<String,Object> body=new HashMap<>(); body.put("commandId",UUID.randomUUID().toString()); body.put("version",task.getVersion()); body.put("action","close");
        exchange(true,task(task.getId())+"/command",HttpMethod.POST,body);
    }
    @Override public int pending(Long id) { JsonNode value=exchange(true,task(id)+"/engine/pending",HttpMethod.GET,null); if(value==null || !value.isIntegralNumber()) { throw new IllegalStateException("Invalid pending count"); } return value.asInt(); }
    @Override public List<DialerWork> claim(Long id,String requestId) {
        Map<String,Object> body=new HashMap<>(); body.put("requestId",requestId); body.put("limit",1); body.put("leaseSeconds",properties.getLeaseSeconds());
        return list(exchange(true,task(id)+"/claim",HttpMethod.POST,body),DialerWork.class);
    }
    @Override public void heartbeat(DialerWork work) { exchange(true,task(work.getTaskId())+"/attempt/heartbeat",HttpMethod.POST,lease(work)); }
    @Override public DialerStatus dial(DialerWork work) { return convert(exchange(false,"/internal/dialer/task/"+work.getTaskId()+"/dial",HttpMethod.POST,request(work)),DialerStatus.class); }
    @Override public DialerStatus status(DialerWork work) { return convert(exchange(false,"/internal/dialer/task/"+work.getTaskId()+"/status",HttpMethod.POST,request(work)),DialerStatus.class); }
    @Override public void complete(DialerWork work,DialerStatus result) {
        Map<String,Object> body=lease(work); body.put("callId",result.getCallId()); body.put("result",result.getResult());
        body.put("answerTime",result.getAnswerTime()); body.put("bridgeTime",result.getBridgeTime()); body.put("agentCode",result.getAgentCode());
        exchange(true,task(work.getTaskId())+"/attempt/result",HttpMethod.POST,body);
    }
    RestTemplate http() { return http; }
}
