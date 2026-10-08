/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.esl.dptools;


import com.voxai.core.esl.internal.IModEslApi;
import com.voxai.core.esl.transport.SendMsg;

public class DpTools {

    private final IModEslApi api;

    public DpTools(IModEslApi api) {
        this.api = api;
    }

    public DpTools answer() {
        api.sendMessage(new SendMsg().addCallCommand("answer"));
        return this;
    }

}
