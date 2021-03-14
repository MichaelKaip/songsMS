package mk.microservices.songsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SongsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SongsServiceApplication.class, args);
    }

}
