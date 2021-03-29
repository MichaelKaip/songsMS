# songsMS
A university project of deconstructing a monolith-application into microservices using Spring Boot/Cloud.

The Project contains of the following Microservics:

| Service Name | Default Port Mapping | Description |
| --------| -----|-------|
| SongsMS Songs Discovery Server | 8761 | A Microservice providing Service Discovery for all Services within the SongsMS Microservices Project. |
| SongsMS Config Server | 8888 | A Microservice providing dynamic configurations for all services within the project from an [external source](https://github.com/MichaelKaip/songsMS-config-server-repo) |
| SongsMS Config Server Repo | | Repository for configurations for the SongsMS Microservices Project |
| SongsMS API Gateway | 8080 | Provides an API Gateway for securing the Microservices |
| SongsMS User Service | 8081 | A Microservice for administrating User Data |
| SongsMS Songs Service | 8082 | A Microservice for administrating songs and providing user-related list of songs |
| SongsMS Songs Info Service | 8083 | A Microservice for providing additional song-related information. |

## Service Architecture

![image](https://user-images.githubusercontent.com/27303233/112891240-2dc2aa00-90d8-11eb-8b78-b88e9e7a7dca.png)
