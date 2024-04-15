# Jenkins CICD Test Service
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)         ![CI/CD Build: Pass](https://img.shields.io/badge/CI/CD_Build-Pass-green)  ![CICD Unit Test: Pass](https://img.shields.io/badge/CI/CD_Test-Pass-blue)                ![Status: Updated](https://img.shields.io/badge/Status-Done-red)

## About The Project
1. This project is to build a CI/CD pipeline with ```Jenkins``` deploying the backend services to ```AWS EKS``` in my ```free tire AWS account```. This stack is majorly used to deploy & test all the backend services I created in this portfolio. I will continue to update this repo to add more components as this portfolio grows!  
2. This project is NOT setup as fully automation, but both automation & manual scripts are used to setup the whole pipeline. ```For example, python boto3 cannot rollout a public subnet unless assigning an elastic IP but the static IP will generate cost from my free tire account, so I would enable the public IP on the subnet maunally instead of using the script.```
3. Typical integration of this pipeline is implemented in the specific backend service repo like [here](https://github.com/mlmaster1995/chris-service-portfolio/tree/main/Jenkins-CICD-Test-Service), and Jenkins will pull the typical repo finishing the whole CI/CD process after seting up a new job.  

## Built With
* [JDK17](https://www.jenkins.io/)
* [JUnit](https://docs.ansible.com/)
* [Mockito](https://docs.ansible.com/)
* [Maven](https://helm.sh/)
* [SpringBoot](https://docs.ansible.com/)
* [SpringSecurity](https://www.docker.com/)
* [SpringDataJPA](https://www.docker.com/)
* [Hibernate](https://www.docker.com/)
* [Liquibase](https://boto3.amazonaws.com/v1/documentation/api/latest/index.html)
* [Mariadb](https://aws.amazon.com/pm/ec2/?gclid=Cj0KCQjw-_mvBhDwARIsAA-Q0Q5DAq27oOGG8rixMZ5HumlUxEzfgMfL8yJkJ4izhPX2tHA952NS1PQaAg7AEALw_wcB&trk=8c0f4d22-7932-45ae-9a50-7ec3d0775c47&sc_channel=ps&ef_id=Cj0KCQjw-_mvBhDwARIsAA-Q0Q5DAq27oOGG8rixMZ5HumlUxEzfgMfL8yJkJ4izhPX2tHA952NS1PQaAg7AEALw_wcB:G:s&s_kwcid=AL!4422!3!472464674288!e!!g!!ec2!11346198414!112250790958)
* [Docker](https://aws.amazon.com/ecr/)
* [Helm](https://aws.amazon.com/eks/)
* [Ansible](https://www.python.org/)
* [Jinjia2](https://kubernetes.io/)
