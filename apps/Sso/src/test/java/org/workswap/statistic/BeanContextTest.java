package org.workswap.statistic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.workswap.sso.SsoApplication;

@SpringBootTest(
    classes = SsoApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles({"sso", "test"})
class StartUpTest {

    @Test
    void listingsStatProducerExists() {
        System.out.println("Hello");
    }
}