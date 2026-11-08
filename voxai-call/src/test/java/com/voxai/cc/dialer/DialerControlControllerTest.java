package com.voxai.cc.dialer;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DialerControlControllerTest {
    @Test void deniesDisabledMissingInvalidAndOtherTenantCredentials() {
        DialerControlProperties properties=new DialerControlProperties(); properties.setTokens(Collections.singletonMap(7L,"01234567890123456789012345678901"));
        DialerControlController api=new DialerControlController(properties,mock(TaskMediaService.class));
        String token="Bearer 01234567890123456789012345678901";
        assertThrows(ResponseStatusException.class,() -> api.authorize(7L,token)); properties.setEnabled(true);
        assertThrows(ResponseStatusException.class,() -> api.authorize(7L,null)); assertThrows(ResponseStatusException.class,() -> api.authorize(7L,"Bearer wrong"));
        assertThrows(ResponseStatusException.class,() -> api.authorize(8L,token)); assertDoesNotThrow(() -> api.authorize(7L,token));
    }
    @Test void requiresLongTenantTokensAndBoundedLimitsWhenEnabled() {
        DialerControlProperties properties=new DialerControlProperties(); properties.setEnabled(true);
        assertThrows(IllegalArgumentException.class,properties::validate);
        properties.setTokens(Collections.singletonMap(7L,"01234567890123456789012345678901")); properties.setMaxPredictiveRatio(10);
        assertThrows(IllegalArgumentException.class,properties::validate);
        properties.setMaxPredictiveRatio(2); assertDoesNotThrow(properties::validate);
    }
}
