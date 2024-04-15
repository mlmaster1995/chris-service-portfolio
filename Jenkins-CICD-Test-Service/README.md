# Jenkins CI/CD Test Service
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)         ![CI/CD Build: Pass](https://img.shields.io/badge/CI/CD_Build-Pass-green)  ![CICD Unit Test: Pass](https://img.shields.io/badge/CI/CD_Test-Pass-blue)     ![Version: 1.0.0](https://img.shields.io/badge/App_Version-1.0.0-black) ![Helm Chart Version:0.0.1](https://img.shields.io/badge/Helm_Chart-1.0.0-purple)  ![Status: Updated](https://img.shields.io/badge/Status-Done-red)

## About The Project
1. This project is constructed to integrate with the [Jenkins CI/CD pipeline](https://github.com/mlmaster1995/chris-service-portfolio/tree/main/Backend-Service-Jenkins-Pipeline), and it's to setup a template how the future ```backend service``` is implemented with this pipeline in this portfolio. Also it's a good test for the pipeline. 
2. This project includes 4 parts: 
    - One simple ```REST CRUD service``` with one entity ```GymMember``` designed & implemented as a microservice with ```full unit test coverage``` and one simple auth layer to ```secure the rest api end points```.
    - Full ```Helm Chart package``` for both ```rest crud service``` and ```mariadb``` for ```kubernetes``` deployment & end-to-end test in ```AWS EKS```
    - ```Ansible playbook``` cover the full ```CD workflow``` from this repo to the ```AWS EKS``` with the ```vault``` enabled to secure all the sensitive data like ```aws-account-id```, ```aws-region```, ```db-password``` etc.
    - ```Jinjia Template``` is to integrate with ```Ansible``` generating dynamic ```values.yml``` to config the ```Helm Chart deployment``` in the CD workflow.    

## Built With
* [JDK17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
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
* [Ansible](https://www.ansible.com/)
* [Jinjia Template](https://jinja.palletsprojects.com/en/3.1.x/)

## Diagram
![cicd-test](https://github.com/mlmaster1995/chris-service-portfolio/assets/55723894/d3e2f2fa-3451-47b5-b13e-7978e75107b9)