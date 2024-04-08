#################################################################################
# MIT License                                                                   #
#                                                                               #
# Copyright (c) 2024 Chris Yang                                                 #
#                                                                               #
# Permission is hereby granted, free of charge, to any person obtaining a copy  #
# of this software and associated documentation files (the "Software"), to deal #
# in the Software without restriction, including without limitation the rights  #
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     #
# copies of the Software, and to permit persons to whom the Software is         #
# furnished to do so, subject to the following conditions:                      #
#                                                                               #
# The above copyright notice and this permission notice shall be included in all#
# copies or substantial portions of the Software.                               #
#                                                                               #
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    #
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      #
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   #
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        #
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, #
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE #
# SOFTWARE.                                                                     #
#################################################################################
import boto3
from botocore.exceptions import ClientError

##NOTE: ecr repo name is consistent in the ansible playbook
ecr_repo_name = 'cicd-test-app'


ecr_client = boto3.client('ecr')
ecr_repo_exists = False
ecr_repo_desc =  ecr_client.describe_repositories(
    maxResults=100
)

try:
    ecr_repos = ecr_repo_desc.get('repositories')
    for ecr_repo in ecr_repos:
        ecr_name = ecr_repo.get('repositoryName')

        if ecr_name == ecr_repo_name:
            print(f"ecr repo with name '{ecr_repo_name}' exists already...")
            ecr_repo_exists = True

    if not ecr_repo_exists:
        print(f"ecr repo with name '{ecr_repo_name}' not exists, and will create one...")
        response = ecr_client.create_repository(
            repositoryName = ecr_repo_name,
            tags=[
                {
                    'Key': 'name',
                    'Value': ecr_repo_name
                },
            ],
            imageTagMutability='IMMUTABLE',
            imageScanningConfiguration={
                'scanOnPush': True
            }
        )
        print(f"ecr repo with name '{ecr_repo_name}' is created with response: '{response}'")

except ClientError as e:
    print(e)

