package org.workswap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = ServerApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles({"server", "test", "api"})
class StartUpTest {

    @Test
    void listingsStatProducerExists() {
        System.out.println("Hello");
    }
}