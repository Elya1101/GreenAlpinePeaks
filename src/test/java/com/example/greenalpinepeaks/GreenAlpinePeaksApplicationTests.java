package com.example.greenalpinepeaks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Application Context Tests")
class GreenAlpinePeaksApplicationTests {

    @Test
    @DisplayName("Should load Spring application context successfully")
    void contextLoads() {
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Should have main method that can be executed")
    void mainMethodShouldRun() {
        GreenAlpinePeaksApplication.main(new String[]{});
        assertThat(true).isTrue();
    }
}