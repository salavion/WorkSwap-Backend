package org.workswap.statistic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.workswap.ServerApplication;

@SpringBootTest(
    classes = ServerApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles({"server", "test"})
class StartUpTest {

    @Test
    void listingsStatProducerExists() {
        System.out.println("Hello");
    }
}