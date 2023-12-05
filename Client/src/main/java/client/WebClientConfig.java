package client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ComponentScan
public class WebClientConfig {
    @Bean
    public WebClient.Builder authBuilder() {
        return WebClient.builder();
    }
}