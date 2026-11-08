package com.voxai.dialer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;

class DialerStartupTest {
    @Test void defaultsToNoNetworkOrDialingWithoutOptIn() {
        new ApplicationContextRunner().withUserConfiguration(VoxaiDialerApplication.class)
                .withPropertyValues("dialer.enabled=false").run(context -> {
                    assertThat(context).hasNotFailed(); assertThat(context).doesNotHaveBean(DialerEngine.class); assertThat(context).doesNotHaveBean(HttpDialerGateway.class);
                });
    }
    @Test void refusesEnabledStartupWithoutTenantCredentials() {
        new ApplicationContextRunner().withUserConfiguration(VoxaiDialerApplication.class)
                .withPropertyValues("dialer.enabled=true").run(context -> assertThat(context).hasFailed());
    }
}
