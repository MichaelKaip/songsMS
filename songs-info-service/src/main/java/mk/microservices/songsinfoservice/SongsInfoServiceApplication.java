package mk.microservices.songsinfoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SongsInfoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SongsInfoServiceApplication.class, args);
    }

}
