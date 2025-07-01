package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BusesDriversApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusesDriversApplication.class, args);
    }
}