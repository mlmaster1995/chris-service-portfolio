# Chris Auth Entry Service

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)   ![App Version](https://img.shields.io/badge/App_Version-1.0.0_SNAPSHOT-black) ![Helm Chart Version:1.0.0](https://img.shields.io/badge/Helm_Chart-1.0.0-purple) ![Dev Status](https://img.shields.io/badge/Status-In_Progress-red)

## About The Project

1. This project is to leverage the benefits of Spring Security to create a security layer filtering the http request
   with JWT(Json Web Token) mechanism for my backend services.
2. This project includes 6 parts:
    - ```Auth Entry Client``` is a lib package used by the ```auth entry service``` and any backend service that needs
      to validate the JWT token
    - ```Auth Test Service``` is a dummy service(could be any ```backend springboot service```) with dep
      as ```auth entry client ``` to present how to use ```auth entry client``` lib in the backend service
    - ```Auth Entry Service``` is a backend service for client to register and get the JTW token
    - ```Auth Scheduler Service``` is a daemon running periodically to check the user login status and update the status
      for JTW token validation process
    - ```local docker test``` includes docker files for all components and a docker-compose file for end-to-end local
      test
    - ```k8s helm chart``` includes a full charts for end-to-end testing in k8s env (AWS EKS)

## Built With

* [JDK17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Testcontainers](https://testcontainers.com/)
* [JUnit 5](https://junit.org/junit5/)
* [Mockito](https://site.mockito.org/)
* [Maven](https://maven.apache.org/)
* [REST API](https://www.redhat.com/en/topics/api/what-is-a-rest-api)
* [SpringBoot 3](https://spring.io/projects/spring-boot)
* [SpringSecurity](https://spring.io/projects/spring-security)
* [SpringDataJPA](https://spring.io/projects/spring-data-jpa)
* [Hibernate](https://hibernate.org/)
* [Liquibase](https://www.liquibase.com/)
* [Mariadb](https://mariadb.org/)
* [Docker](https://www.docker.com/?utm_source=google&utm_medium=cpc&utm_campaign=BRAND_SEARCH_BRAND_AMER_NORAM&utm_term=docker&gad_source=1&gclid=CjwKCAjwoPOwBhAeEiwAJuXRh0Ergcpu4AssaQTXGnlbGeWHNNyzurXBeXPpV5ILTsrweBjwpMD1GRoC_BgQAvD_BwE)
* [Helm](https://helm.sh/)

## Diagram

![auth-entry-service drawio](https://github.com/mlmaster1995/chris-service-portfolio/assets/55723894/72c23f27-c3e4-4462-b7f9-191a641a80e5)

**Data Flow**:

1. user call ```/api/v1/auth/register``` to register the access with ```Auth Entry Service``` persisting the auth info
   into ```auth_api database```
2. user call ```/api/v1/auth/login``` with basic http auth to get ```basic JWT token``` from ```Auth Entry Service```
3. user call ```/api/v1/v1/data``` with ```basic JWT token``` to access ```Auth Test Service``` to fetch the test data
4. ```Auth Test Service``` call ```Auth Entry Service``` to check the user status to validate the ```basic JWT token```
5. if ```basic JWT token``` is valid, the test data is returned back to the user
6. user call ```/api/v1/auth/logout``` with basic http auth to log out from ```Auth Entry Service```
7. ```Auth Scheduler Service``` runs periodically to check the user status based on the ```token expiration time ```. If
   the user is log out, token is expired as well.

## Auth Entry Service

### REST API Endpoint
| Method | API                   | Feature                                             | Auth Role   |
|--------|-----------------------|-----------------------------------------------------|-------------|
| GET    | /health               | service status information from springboot actuator | n/a         |
| POST   | /api/v1/auth/register | register new user with the service                  | n/a         |
| GET    | /api/v1/auth/login    | log in the service and get the JWT token            | USER, ADMIN |
| GET    | /api/v1/auth/logout   | log out from the service and expire JWT token       | USER, ADMIN |
| GET    | /api/v1/auth/token    | an self-test endpoint to validate the token         | USER, ADMIN |
| POST   | /api/v1/auth/status   | an internal endpoint to get user status             | n/a         |

### Service Properties: 
1. must set up properties in ```application.properties```:
```
#port
server.port=${APP_SERVER_PORT}
management.server.port=${APP_MANAGEMENT_PORT}

#databse
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}
spring.datasource.driver-class-name=${DATABASE_DRIVER}

#auth service
app.auth.jwt.basic.duration.sec=${APP_AUTH_JWT_BASIC_DURATION_SEC}
app.auth.encoder.enabled=${APP_AUTH_ENCODER_ENABLED}
app.auth.find.users.page.size=${APP_AUTH_FIND_USERS_PAGE_SIZE}

#skip jwt token check on certain endpoints
app.auth.client.jwt.filter.skip=${APP_AUTH_CLIENT_JWT_FILTER_SKIP}

#only true when client lib is used by the remote service
app.auth.client.status.check=${APP_AUTH_CLIENT_STATUS_CHECK}

#profile
spring.profiles.active=${APP_AUTH_CLIENT_PROFILE}
```

2. The properties env values are filled up within ```.env``` for ```docker-compose.yml``` and ```value.yaml``` for ```k8s```

## Auth Entry Client
1. The client is a lib dep used by the backend services. It's NOT a daemon or a service, but a maven package that needs to be added into backend service pom file. 

2. To use the client lib in the backend service, following deps need to added into pom:
```
<dependency>
   <groupId>com.chris</groupId>
   <artifactId>AuthEntryClient</artifactId>
   <version>1.0.0-SNAPSHOT</version>
</dependency>

<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
   <groupId>org.mariadb.jdbc</groupId>
   <artifactId>mariadb-java-client</artifactId>
</dependency>
```

3. The client lib also needs following properties that needs to defined in the ```application.properties```: 
```
######## auth client props ########
#auth client needs the database access
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}
spring.datasource.driver-class-name=${DATABASE_DRIVER}

#auth client remote call on auth entry service
app.auth.client.remote.url=${APP_AUTH_CLIENT_REMOTE_URL}
app.auth.client.url.port=${APP_AUTH_CLIENT_URL_PORT}
app.auth.client.url.endpoint=${APP_AUTH_CLIENT_URL_ENDPOINT}

#skip jwt token check on certain endpoints
app.auth.client.jwt.filter.skip=${APP_AUTH_CLIENT_JWT_FILTER_SKIP}

#turn on the remote service check for user status
app.auth.client.status.check=${APP_AUTH_CLIENT_STATUS_CHECK}

#auth client profile
spring.profiles.active=${APP_AUTH_CLIENT_PROFILE}
```
4. The properties env values are filled up within ```.env``` for ```docker-compose.yml``` and ```value.yaml``` for ```k8s```

## Auth Scheduler Service
1. must set up properties in ```application.properties```
```
#databse
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}
spring.datasource.driver-class-name=${DATABASE_DRIVER}

#auth service
app.auth.user.status.check.cron=${APP_AUTH_USER_STATUS_CHECK_CRON}
```

2. The properties env values are filled up within ```.env``` for ```docker-compose.yml``` and ```value.yaml``` for ```k8s```

## Auth Test Service
1. This is a sample service about how to use ```Auth Entry Client``` in the backend springboot service to integrate with the ```Auth Entry Service``` for JWT token validation

## Docker Env Test
**NOTE**:
This project provides full set of docker related files for a local end-to-end test

### Build Images: 
```
$ mvn clean install
...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for ChrisAuthEntryService 1.0.0-SNAPSHOT:
[INFO]
[INFO] ChrisAuthEntryService .............................. SUCCESS [  0.072 s]
[INFO] AuthEntryClient .................................... SUCCESS [ 20.403 s]
[INFO] AuthEntryService ................................... SUCCESS [ 49.987 s]
[INFO] AuthSchedulerService ............................... SUCCESS [  6.844 s]
[INFO] AuthTestService .................................... SUCCESS [  8.045 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  01:25 min
[INFO] Finished at: 2024-05-29T18:29:10-04:00
[INFO] ------------------------------------------------------------------------
...

$ docker image ls 
..
REPOSITORY               TAG               IMAGE ID       CREATED              SIZE
auth-test-service        1.0.0-SNAPSHOT    bd9f76d7c7b9   54 seconds ago       380MB
auth-scheduler-service   1.0.0-SNAPSHOT    9ef77af390c0   About a minute ago   367MB
auth-entry-service       1.0.0-SNAPSHOT    a8dbcb8dec7d   About a minute ago   387MB
mariadb                  11                1afa8181eb9d   About a minute ago   388MB
..
```
### Start Service
```
$ docker-compose up -d
$ docker ps
CONTAINER ID   IMAGE                                   COMMAND                  CREATED         STATUS         PORTS                                            NAMES
982159f631fc   auth-test-service:1.0.0-SNAPSHOT        "/__cacert_entrypoin…"   5 seconds ago   Up 4 seconds   0.0.0.0:8898->8080/tcp, 0.0.0.0:8899->8081/tcp   auth-test-service
e4bda92db8a0   auth-scheduler-service:1.0.0-SNAPSHOT   "/__cacert_entrypoin…"   5 seconds ago   Up 4 seconds                                                    auth-scheduler-service
cd04a2b21582   auth-entry-service:1.0.0-SNAPSHOT       "/__cacert_entrypoin…"   5 seconds ago   Up 4 seconds   0.0.0.0:8888->8080/tcp, 0.0.0.0:8889->8081/tcp   auth-entry-service
b100218fa50a   mariadb:11                              "docker-entrypoint.s…"   8 days ago      Up 8 days      0.0.0.0:3306->3306/tcp                           chris-db
```
### End-To-End Test
1. Register a new user
```
$ curl --location '127.0.0.1:8888/api/v1/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{
"username":"chris-20240506",
"password":"123456",
"email":"kyang-20240506@lakeheadu.ca"
}'

"user with email (kyang-20240506@lakeheadu.ca) is registered successfully"
```
2. User login
```
$ curl --location '127.0.0.1:8888/api/v1/auth/login' \
--header 'Authorization: Basic a3lhbmctMjAyNDA1MDZAbGFrZWhlYWR1LmNhOjEyMzQ1Ng=='

"eyJraWQiOiI2ZmJiNWQ1ZC1jYzYyLTRjMTgtOTMzMC1kYTMwZDg0YjI5NWIiLCJh...."
```

3. Self-test token on ```Auth Entry Service```
```
$ curl --location '127.0.0.1:8888/api/v1/auth/token' \
--header 'Authorization: eyJraWQiOiI2ZmJiNWQ1ZC1jYzYy....NeUg1-IS6_cqzOFhgURVHAA-zyX_AFSr9xRalOup2BDg'

"token is valid and here is DATA!"
```

5. Test token with ```Auth Test Service```
```
$ curl -i --location '127.0.0.1:8898/api/v1/data' \
--header 'Authorization:  eyJraWQiOiI2ZmJiNWQ1ZC1jYzYy....NeUg1-IS6_cqzOFhgURVHAA-zyX_AFSr9xRalOup2BDg' \
--header 'Cookie: XSRF-TOKEN=141205ec-ad0a-4228-87d1-eeab70939e65'

HTTP/1.1 200
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/plain;charset=UTF-8
Content-Length: 45
Date: Mon, 20 May 2024 23:49:01 GMT

access is authorized and data is retrieved...
```
## K8S Env Test

In Progress...









