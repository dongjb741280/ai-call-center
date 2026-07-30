package com.voxai.cc.fs.esl.dptools;

import com.voxai.cc.fs.esl.internal.IModEslApi;
import com.voxai.cc.fs.esl.transport.SendMsg;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DpToolsTest {

    @Mock
    private IModEslApi api;

    @Test
    void shouldSendAnswerCommand() {
        DpTools tools = new DpTools(api);
        DpTools result = tools.answer();

        verify(api).sendMessage(any(SendMsg.class));
        assertSame(tools, result, "should return self for chaining");
    }
}
