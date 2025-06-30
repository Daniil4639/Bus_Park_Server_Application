package app.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientsConfig {

    @Value("${services.buses_drivers.host}")
    private String busesDriversServiceHost;

    @Value("${services.buses_drivers.port}")
    private String busesDriversServicePort;

    @Value("${services.paths_stations.host}")
    private String pathsStationsServiceHost;

    @Value("${services.paths_stations.port}")
    private String pathsStationsServicePort;

    @Value("${services.schedule.host}")
    private String scheduleServiceHost;

    @Value("${services.schedule.port}")
    private String scheduleServicePort;

    @Bean
    public WebClient busesDriversServiceClient() {
        return WebClient.builder()
                .baseUrl("http://" + busesDriversServiceHost + ":" + busesDriversServicePort)
                .build();
    }

    @Bean
    public WebClient pathsStationsServiceClient() {
        return WebClient.builder()
                .baseUrl("http://" + pathsStationsServiceHost + ":" + pathsStationsServicePort)
                .build();
    }

    @Bean
    public WebClient scheduleServiceClient() {
        return WebClient.builder()
                .baseUrl("http://" + scheduleServiceHost + ":" + scheduleServicePort)
                .build();
    }
}