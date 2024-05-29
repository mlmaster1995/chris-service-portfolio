# Chris Auth Entry Service
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)   ![App Version](https://img.shields.io/badge/App_Version-1.0.0_SNAPSHOT-black) ![Helm Chart Version:1.0.0](https://img.shields.io/badge/Helm_Chart-1.0.0-purple) ![Dev Status](https://img.shields.io/badge/Status-In_Progress-red)

## About The Project
1. This project is to leverage the benefits of Spring Security to create a security layer filtering the http request with JWT(Json Web Token) mechanism for my backend services.
2. This project includes 6 parts:
    - ```Auth Entry Client``` is a lib package used by the ```auth entry service``` and any backend service that needs to validate the JWT token 
    - ```Auth Test Service``` is a dummy service with dep as ```auth entry client ``` to present how to use ```auth entry client``` lib in the backend service
    - ```Auth Entry Service``` is a backend service for client to register and get the JTW token
    - ```Auth Scheduler Service``` is a daemon running periodically to check the user login status and update the status for JTW token validation process
    - ```local docker test``` includes docker files for all components and a docker-compose file for end-to-end local test
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
