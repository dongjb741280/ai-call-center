/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.esl.outbound;

import io.netty.channel.Channel;
import com.voxai.core.esl.transport.event.EslEvent;
import com.voxai.core.esl.inbound.IEslEventListener;
import com.voxai.core.esl.internal.Context;

public interface IClientHandler extends IEslEventListener {
    void onConnect(Context ctx, EslEvent event);

    void onClose(Channel channel);
}
