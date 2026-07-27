package com.voxai.cc.fs.esl.outbound;

import io.netty.channel.Channel;
import com.voxai.cc.fs.esl.transport.event.EslEvent;
import com.voxai.cc.fs.esl.inbound.IEslEventListener;
import com.voxai.cc.fs.esl.internal.Context;

public interface IClientHandler extends IEslEventListener {
    void onConnect(Context ctx, EslEvent event);

    void onClose(Channel channel);
}
