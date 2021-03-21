package mk.microservices.apigatewayservice.config;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;


@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r
                        .path("/auth")
                        .uri("lb://user-service/auth")
                )
                .route(r -> r
                        .path("/songs")
                        .uri("lb://songs-service/songs")
                )
                .route(r -> r
                        .path("/songs/**")
                        .filters(f->f.rewritePath("/songs/(?<songId>.*)","/songs/${songId}"))
                        .uri("lb://songs-service/songs")
                )
                .route(r -> r
                        .path("/songlists/**")
                        .filters(f -> f.rewritePath("/songlists/(?<songListId>.*)", "songlists/${songlistId}"))
                        .uri("lb://songs-service/songlists")
                )
                .build();
    }
}
