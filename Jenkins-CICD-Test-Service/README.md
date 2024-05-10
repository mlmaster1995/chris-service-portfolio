# Jenkins CI/CD Test Service
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)         ![CI/CD Build: Pass](https://img.shields.io/badge/CI/CD_Build-Pass-green)  ![CICD Unit Test: Pass](https://img.shields.io/badge/CI/CD_Test-Pass-blue)     ![Version: 1.0.0](https://img.shields.io/badge/App_Version-1.0.0-black) ![Helm Chart Version:0.0.1](https://img.shields.io/badge/Helm_Chart-1.0.0-purple) ![Test Env: AWS EKS](https://img.shields.io/badge/Test_Env-AWS_EKS-yellow) ![Status: Updated](https://img.shields.io/badge/Status-Done-red)

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
* [Jinja Template](https://jinja.palletsprojects.com/en/3.1.x/)

## Diagram
![cicd-test](https://github.com/mlmaster1995/chris-service-portfolio/assets/55723894/d3e2f2fa-3451-47b5-b13e-7978e75107b9)

## CRUD Service

**NOTE**:
1. database setup is finished by the ```liquibase changelog``` including table creation and default sample data loading when the service starts. The detail about the entity table is in the change logs located in the ```resources``` folder
2. rest api has pagination implemented and the default page size is 100-row, but it could be updated via ```application.properties```  
3. for important env properties like server/management port, db access, ```cd_values.yml``` is the place to update. ```values.yaml``` in the helm chart is also good for updating the env values, but it will **NOT** get used by the ```Jenkins CI/CD pipeline```.
4. all rest endpoints are secured by ```Basic Http Auth``` from ```SpringSecurity``` module. All the ```users```, ```roles``` and ```role-user``` mappings are setup by the ```liquibase changelog``` into the ```mariadb``` when the service starts.
5. this service **NOT** provide the user ```registration``` and ```login endpoint``` to manage the auth feature, so any updates in the security data should be done by adding new ```liquibase changelogs```. 

### REST API Endpiont 

| Method | API                         | Feature                                             | Auth Role                  |
|--------|-----------------------------|-----------------------------------------------------|----------------------------|
| GET    | /health                     | service status information from springboot actuator | Authenticated without Role |
| GET    | /api/v1/members             | get all members under one page                      | USER, ADMIN                |
| GET    | /api/v1/members/{member_id} | get gym member by id                                | USER, ADMIN                |
| POST   | /api/v1/member/{email}      | get gym memeber by email                            | USER, ADMIN                |
| POST   | /api/v1/member              | add new gym member to database                      | ADMIN                      |
| PUT    | /api/v1/member              | update existing gym member into database            | ADMIN                      |
| DELETE | /api/v1/members/{memberId}  | delete member by id                                 | ADMIN                      |

### Security Auth

| User  | Role        | Password Encrypt | Auth Base64                  |
|-------|-------------|------------------|------------------------------|
| user  | USER        | BCrypt           | dXNlcjp1c2Vy                 |
| admin | ADMIN       | BCrypt           | YWRtaW46Y2hyaXNBZG1pbjIwMjQh |
| chris | USER, ADMIN | BCrypt           | Y2hyaXM6Y2hyaXMyMDI0IQ==     |

## Helm Chart

**NOTE**:
1. This ```helm chart package``` is structured for the project with multiple microservices as dependencies. The ```starter chart``` for each ```sub-chart``` is available [here](https://github.com/mlmaster1995/chris-service-portfolio/tree/main/Backend-Service-Jenkins-Pipeline/helm/chris-service-starter-chart).
2. I add the ```mariadb:11``` as one ```pod``` or one ```sidecar``` for the crud service in the helm chart which is **NOT** a good practice (**tried to avoid more costs in the end-to-end test**), but the best way is to use ```AWS RDS with Proxy setup``` for better performance.
3. [values.yaml](https://github.com/mlmaster1995/chris-service-portfolio/blob/main/Jenkins-CICD-Test-Service/K8sHelmChart/values.yaml) is the ```main entry``` for the whole helm chart package deployment configuration, but it's **ONLY** for the **MANUAL** build and deploy with ```$ helm <actions>``` command. Also it will **EXPOSE** all the sensitive data like aws-account-id, aws-region, database access etc, so it's **NOT** recommended from the ```public repo```.  
4. [cd_vars.yml](https://github.com/mlmaster1995/chris-service-portfolio/blob/main/Jenkins-CICD-Test-Service/cd_vars.yml) is the ```main entry``` for the helm chart package build & deploy via the ```Ansible``` on the ```Jenkins CI/CD pipeline```. 
5. [helm_chart_values.j2](https://github.com/mlmaster1995/chris-service-portfolio/blob/main/Jenkins-CICD-Test-Service/helm_chart_values.j2) is the ```main template``` for the ```dynamic values.yaml``` used by the ```Ansible Playbook``` to populate with ```cd_vars.yml``` generating ```Ansible Managed values.yaml``` for helm package pushed to ```AWS ECR``` and then deploy to ```AWS EKS``` in the ```Jenkins CI/CD pipeline```. 


## Ansible Playbook
**NOTE**:
1. [cd_playbook.yml](https://github.com/mlmaster1995/chris-service-portfolio/blob/main/Jenkins-CICD-Test-Service/cd_playbook.yml) is the main workflow controller used by the ```Jenkins CI/CD pipeline```. And it might be different from the project to the project. 
2. [Ansbile Vault](https://github.com/mlmaster1995/chris-service-portfolio/tree/main/Jenkins-CICD-Test-Service/cd_vault) needs to setup in the ```Ansible Server``` to play the workflow **BEFORE** the pipeline is triggered.
3. ```task flow``` is controlled by the vars in ```cd_vars.yml``` including ```image_to_ecr```, ```chart_to_ecr```, ```init_install```, ```upgrade_install```. The playbook will run based on the typically enabled task. 

## EKS End-To-End Test Sample
**NOTE**:
1. There are different ways to test the endpoint after deploying to ```EKS```. The crud servie could setup as type of ```NodePort``` or ```LoadBalancer``` for the external access testing. But it's **VERY VERY VERY BAD** practice on the cloud as it will expose all the endpoints to the public.
2. The servie I created is always using ```ClusterIP``` to hide the servie fully in the backend. For external access, [AWS Load Balancer Controller](https://kubernetes-sigs.github.io/aws-load-balancer-controller/v1.1/) is the best option to line up the typical endpoint to the load balancer via the ```Ingress Resource``` definition plus SSL/TLS cert for better security.
3. For local test, use ```$kube proxy```, but it needs to config the auth access from local pc to the remote eks. To make it easy & fast, I use this [test-pod](https://github.com/mlmaster1995/chris-service-portfolio/blob/main/Backend-Service-Jenkins-Pipeline/k8s/test-pod.yml) in the same namespace to test the endpoints as follows.

### Start Test Pod: 
```
$ kubectl apply -f test-pod.yml
```

### Access Test Pod:
```
$ kubectl exec -it test-pod -nchris-service-portfolio --sh
```


### Test Sample 1: 
```
$ curl --location 'simple-rest-crud-service:8081/health' \
--header 'Authorization: Basic Y2hyaXM6Y2hyaXMyMDI0IQ=='
```

response: 
```
{"status":"UP","components":{"db":{"status":"UP","details":{"database":"MariaDB","validationQuery":"isValid()"}},"diskSpace":{"status":"UP","details":{"total":85886742528,"free":79709462528,"threshold":10485760,"path":"/opt/service/.","exists":true}},"livenessState":{"status":"UP"},"ping":{"status":"UP"},"readinessState":{"status":"UP"}},"groups":["liveness","readiness"]}
```

### Test Sample 2:
```
$ curl --location 'simple-rest-crud-service:8080/api/v1/member/email' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic dXNlcjp1c2Vy' \
--data-raw '{
    "email": "vincent2024crooks@chrismember.ca"
}'
```
response: 
```
{"id":24,"firstName":"vincent","lastName":"crooks","email":"vincent2024crooks@chrismember.ca"}
```

## Contact
Chris Yang: kyang3@lakeheadu.ca
