package com.voxai.api.web;

import com.voxai.core.entity.AgentSip;
import com.voxai.core.mapper.AgentSipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * FreeSWITCH mod_xml_curl directory 查询接口。
 *
 * 由 sofia 在 REGISTER 认证时回调，按分机号查询 cc_agent_sip（status=1）。
 * 返回明文 password，由 FreeSWITCH 用挑战 realm 现算哈希，避免 a1-hash 与
 * force-register-domain / challenge-realm 的域不一致问题。
 *
 * @author dongjb
 * @date 2026/09/21
 */
@RestController
@RequestMapping("freeswitch")
public class FreeSwitchDirectoryController {

    // 不能带 <?xml?> 声明：FS 预处理器按行丢弃含 "<?“ 的行（switch_xml.c preprocess），
    // 单行响应会导致整篇被丢弃。
    private static final String EMPTY_DIRECTORY =
            "<document type=\"freeswitch/xml\"><section name=\"directory\"/></document>";

    @Autowired
    private AgentSipMapper agentSipMapper;

    // mod_xml_curl 未配置 method 时默认 POST（参数在 form 体里）；同时放开 GET 便于 curl 排查。
    @RequestMapping(value = "directory", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/xml")
    public String directory(@RequestParam(required = false) String user,
                            @RequestParam(required = false) String domain) {
        AgentSip sip = resolveSip(user);
        if (sip == null) {
            return EMPTY_DIRECTORY;
        }

        StringBuilder xml = new StringBuilder();
        xml.append("<document type=\"freeswitch/xml\">")
                .append("<section name=\"directory\">")
                .append("<domain name=\"").append(escape(domain)).append("\">")
                .append("<groups><group name=\"default\"><users>")
                .append("<user id=\"").append(sip.getSip()).append("\">")
                .append("<params><param name=\"password\" value=\"").append(escape(sip.getSipPwd())).append("\"/></params>")
                .append("<variables>")
                .append("<variable name=\"user_context\" value=\"default\"/>")
                .append("<variable name=\"accountcode\" value=\"").append(sip.getCompanyId()).append("\"/>")
                .append("</variables>")
                .append("</user></users></group></groups>")
                .append("</domain></section></document>");
        return xml.toString();
    }

    private AgentSip resolveSip(String user) {
        if (user == null) {
            return null;
        }
        try {
            long value = Long.parseLong(user.trim());
            if (value <= 0 || value > 2147483647L) {
                return null;
            }
            return agentSipMapper.selectBySip((int) value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
