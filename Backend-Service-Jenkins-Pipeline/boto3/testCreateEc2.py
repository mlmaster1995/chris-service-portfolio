'''
use boto3 module as a ec2 resource directly
'''
import boto3

ec2 = boto3.resource('ec2')

instance_name = 'test-server'
instance_id = None
instance_exists = False

for ins in ec2.instances.all():
    for tag in ins.tags:
        if tag['Key'] == 'name' and tag['Value'] == instance_name:
            instance_exists = True
            instance_id = ins.id
            print(f"ec2 instance with id '{instance_id}' exists already...")
            break
    
    if instance_exists:
        break

if not instance_exists:
    instances_created = ec2.create_instances(
        ImageId='ami-02d7fd1c2af6eead0',
        InstanceType='t2.micro',
        MaxCount=1,
        MinCount=1,
        SecurityGroupIds=[
            'sg-012bda3ab1224aeb2',
        ],
        SubnetId='subnet-007bf72041d7ab626',
        UserData='#!/bin/bash \n yum update -y \n service httpd start \nchkconfig httpd on',
        DryRun=False,
        KeyName='cicdkey',
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
                        'Value': 'boto3 ec2 test'
                    },
                    {
                        'Key': 'name',
                        'Value': instance_name
                    }
                ]
            },
        ]
    )

    instance_id = instances_created[0].id
    print(f"new instance with id '{instance_id}' is created...")


#start the instance
response = ec2.Instance(instance_id).start()
print(f"instance with id '{instance_id}' is started... " )
print(response)

# ec2.Instance(instance_id).terminate()
