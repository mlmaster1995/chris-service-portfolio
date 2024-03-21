#ecr: https://docs.aws.amazon.com/AmazonECR/latest/userguide/docker-push-ecr-image.html
###### setup ecr repo on local mac
$ aws sts  get-caller-identity --profile chris
{
    "UserId": "...",
    "Account": "...",
    "Arn": "arn:aws:iam::...:user/chris"
}

$ aws ecr create-repository --repository-name docker-test-app --region us-east-1 --profile chris
# {
#     "repository": {
#         "repositoryArn": "arn:aws:ecr:us-east-1:...:repository/docker-test-app",
#         "registryId": "...",
#         "repositoryName": "docker-test-app",
#         "repositoryUri": "....dkr.ecr.us-east-1.amazonaws.com/docker-test-app",
#         "createdAt": "2024-03-21T13:09:58.324000-04:00",
#         "imageTagMutability": "MUTABLE",
#         "imageScanningConfiguration": {
#             "scanOnPush": false
#         }
#     }
# }

##### setup ecr access on Ansible server
##update role policy to Ansible server to give ecr access permission
$ aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <id>.dkr.ecr.us-east-1.amazonaws.com
# WARNING! Your password will be stored unencrypted in /home/ansibleadmin/.docker/config.json.
# Configure a credential helper to remove this warning. See
# https://docs.docker.com/engine/reference/commandline/login/#credentials-store
# Login Succeeded
$ docker tag docker-test-app:0.0.1 <id>.dkr.ecr.us-east-1.amazonaws.com/docker-test-app:SNAPSHOT-1
$ docker image ls
# REPOSITORY                                                     TAG                         IMAGE ID       CREATED          SIZE
# <id>.dkr.ecr.us-east-1.amazonaws.com/docker-test-app   SNAPSHOT-1                  2bc3e8273885   10 minutes ago   498MB
# docker-test-app                                                0.0.1                       2bc3e8273885   10 minutes ago   498MB
# tomcat                                                         9.0.86-jdk17-corretto-al2   5eda5bd6ebaf   9 days ago       498MB
$ docker push <id>.dkr.ecr.us-east-1.amazonaws.com/docker-test-app:SNAPSHOT-1

## check result on local mac
$ aws ecr list-images --repository-name docker-test-app --region=us-east-1 --profile chris
# {
#     "imageIds": [
#         {
#             "imageTag": "SNAPSHOT-1",
#             "imageDigest": "sha256:ee19b3f8611fc0e0daab4b2fe6a251e55293bdb3ba55e56ac1991da004ad33da"
#         }
#     ]
# }

$ aws ecr describe-images --repository-name docker-test-app --region us-east-1 --profile chris
# {
#     "imageDetails": [
#         {
#             "registryId": "...",
#             "repositoryName": "docker-test-app",
#             "imageDigest": "sha256:ee19b3f8611fc0e0daab4b2fe6a251e55293bdb3ba55e56ac1991da004ad33da",
#             "imageTags": [
#                 "SNAPSHOT-1"
#             ],
#             "imageSizeInBytes": 231517256,
#             "imagePushedAt": "2024-03-21T13:52:29-04:00"
#         }
#     ]
# }

## push helm to ecr: https://docs.aws.amazon.com/AmazonECR/latest/userguide/push-oci-artifact.html
$ helm push docker-test-app.tgz oci://264603349251.dkr.ecr.us-east-2.amazonaws.com/batchrules



####ecr iam policy: 
# https://docs.aws.amazon.com/AmazonECR/latest/userguide/image-push.html
# https://docs.aws.amazon.com/AmazonECR/latest/userguide/repository-policy-examples.html