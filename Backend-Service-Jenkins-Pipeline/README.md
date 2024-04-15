# Backend Service Jenkins CI/CD Pipeline
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## About The Project
This project is to build a CI/CD pipeline with ```Jenkins``` deploying to ```AWS EKS``` in my ```free tire AWS account```. This stack is majorly used to deploy & test all the backend services I created in this portfolio. I will continue to update this repo to add more components as this portfolio grows!  

## Built With
* [Jenkins](https://www.jenkins.io/)
* [Ansible](https://docs.ansible.com/)
* [Docker](https://www.docker.com/)
* [Boto3](https://boto3.amazonaws.com/v1/documentation/api/latest/index.html)
* [AWS EC2](https://aws.amazon.com/pm/ec2/?gclid=Cj0KCQjw-_mvBhDwARIsAA-Q0Q5DAq27oOGG8rixMZ5HumlUxEzfgMfL8yJkJ4izhPX2tHA952NS1PQaAg7AEALw_wcB&trk=8c0f4d22-7932-45ae-9a50-7ec3d0775c47&sc_channel=ps&ef_id=Cj0KCQjw-_mvBhDwARIsAA-Q0Q5DAq27oOGG8rixMZ5HumlUxEzfgMfL8yJkJ4izhPX2tHA952NS1PQaAg7AEALw_wcB:G:s&s_kwcid=AL!4422!3!472464674288!e!!g!!ec2!11346198414!112250790958)
* [AWS ECR](https://aws.amazon.com/ecr/)
* [AWS EKS](https://aws.amazon.com/eks/)
* [Python](https://www.python.org/)

## CI/CD Pipeline

**NOTE**: 
1. All aws resources are provisioned on the ```public subnet``` in this statck which is **NOT** a good option as I try to save extra costs in my free tire account. The best practice is all resources in ```private subnet``` and all traffic are routed through the ```NAT gateway``` in the custom VPC and subnet blocks;
2. This stack is still secured properly via the typical ```Security Group``` settings and ```IAM role & policy``` on each resource; 

![readme](https://github.com/mlmaster1995/chris-service-portfolio/assets/55723894/7742ddc5-7e87-4cfc-920a-4d5d31e7e68b)

## Contents

| Folder        | File             | Status    |
| ------------- |---------------- |:---------:|
| ansible       | iam policy for ansible server to access the ecr, and test playbooks    | updated |
| boto3         | python test scripts, and aws resources setup scripts          |   updated |
| docker        | test docker file, and docker server setup commands         |    updated |
| ec2           | ec2 setup commands         |  updated |
| ecr           | ecr access iam policy, and ecr repo & image push commands          |    updated |
| eks           | eks access iam policy, and eks setup commands         |    updated |
| helm          | backend service starter chart, and helm setup commands         |    updated |
| jenkins       | jenkins server setup commands         | updated  |
| k8s           | eks test pod & db yaml files, and eks component setup files         |    updated |
