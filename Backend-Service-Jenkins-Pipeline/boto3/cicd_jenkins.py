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
import sys
from botocore.exceptions import ClientError

dry_run = False

try:
    ################## ec2 sg id
    ec2_client = boto3.client('ec2')

    ec2_sg_id = None
    jenkins_sg_name = "chris_cicd_vpc_jenkins_sg"
    sg_desc = ec2_client.describe_security_groups(
            Filters=[
                {
                    'Name': 'tag:name',
                    'Values': [
                        jenkins_sg_name,
                    ]
                },
            ],
            DryRun=dry_run,
        )
    
    sg_list = sg_desc.get('SecurityGroups')
    if not sg_list:
        print(f"security group with name '{jenkins_sg_name}' not exists, and need to run cicd_sg.py at first...")
        sys.exit(1)

    ec2_sg_id = sg_list[0].get('GroupId')
    print(f"ec2 security group name '{jenkins_sg_name}', id '{ec2_sg_id}'")

    ################## ec2 subnet id
    subnet_name = "pub-us-east-1d"
    ec2_subnet_id = None
    chris_subnet_desc = ec2_client.describe_subnets(
        Filters=[
            {
                'Name': 'tag:name',
                'Values': [
                    subnet_name,
                ]
            },
        ],
        DryRun=dry_run,
    )

    chris_subnets = chris_subnet_desc.get('Subnets')
    if not chris_subnets:
        print(f"subnet with name '{subnet_name}' not exists, and need to run cicd_vpc.py at first...")
        sys.exit(1)

    ec2_subnet_id = chris_subnets[0].get('SubnetId')
    print(f"ec2 subnet group name '{subnet_name}', id '{ec2_subnet_id}'")

    ################## ec2 params
    ec2_name = 'chris-vpc-jenkins-server'
    ec2_id = None
    ec2_exists = False
    ec2_image='ami-02d7fd1c2af6eead0'
    ec2_type='t2.micro'
    ec2_data='#!/bin/bash \nsudo yum update -y'
    ec2_key='cicdkey'

    ec2_desc = ec2_client.describe_instances(
        Filters=[
            {
                'Name': 'tag:name',
                'Values': [
                    ec2_name,
                ]
            },
        ],
        DryRun=dry_run
    )

    ec2_ins = ec2_desc.get('Reservations')
    if not ec2_ins:
        print(f"ec2 instance with name '{ec2_name}' not exists and will create one...")
        ec2_exists = False
    else: 
        ec2_status = ec2_ins[0].get('Instances')[0].get('State').get('Name')
        print(ec2_status)

        if ec2_status == 'terminated':
            ec2_exists = False
            print(f"ec2 instance with name '{ec2_name}' exists but terminated, and will create one...")
        else: 
            ec2_exists = True
            print(f"ec2 instance with name '{ec2_name}' exists already...")

    ################## ec2 create instance
    ec2 = boto3.resource('ec2')
    if not ec2_exists:
        ec2_created = ec2.create_instances(
            ImageId=ec2_image,
            InstanceType=ec2_type,
            MaxCount=1,
            MinCount=1,
            SecurityGroupIds=[
                ec2_sg_id,
            ],
            SubnetId=ec2_subnet_id,
            UserData=ec2_data,
            DryRun=dry_run,
            KeyName=ec2_key,
            TagSpecifications=[
                {
                    'ResourceType': 'instance',
                    'Tags': [
                        {
                            'Key': 'author',
                            'Value': 'Chris Yang'
                        },
                        {
                            'Key': 'use',
                            'Value': 'Jenkins server'
                        },
                        {
                            'Key': 'name',
                            'Value': ec2_name
                        }
                    ]
                },
            ]
        )

        instance_id = ec2_created[0].id
        print(f"new instance with id '{instance_id}' is created...")
except ClientError as e:
    print(e)