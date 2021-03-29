# songsMS
A university project of deconstructing a monolith-application into microservices using Spring Boot/Cloud.

The Project contains of the following Microservics:

| Service Name | Default Port Mapping | Description |
| --------| -----|-------|
| api-gateway-service | 8080 | Provides an API Gateway for securing the Microservices |
| user-service | 8081 | Provides an endpoint for authentication |
| songs-service | 8082 | Provides endpoints for administrating songs and and song-lists. User can have private and/or public songlists. |
| -songs-info-service | 8083 | A Microservice for providing additional song-related information. |

## Service Architecture

![image](https://user-images.githubusercontent.com/27303233/112891838-e4bf2580-90d8-11eb-8509-fd70d10cd04f.png)

