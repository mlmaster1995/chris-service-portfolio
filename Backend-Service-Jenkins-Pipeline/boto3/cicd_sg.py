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

ec2_client = boto3.client('ec2')
dry_run = False


try:
    ################## vpc params
    chris_vpc_name = 'chris-cicd-vpc'
    
    chris_vpc_desc = ec2_client.describe_vpcs(
        Filters=[
            {
                'Name': 'tag:name',
                'Values': [
                    chris_vpc_name
                ]
            },
        ]
    )
    
    chris_vpcs = chris_vpc_desc.get('Vpcs')
    if not chris_vpcs:
        print(f"vpc with name '{chris_vpc_name}' not exists, run cicd_vpc.py to create the vpc at first...")
        sys.exit(1)
    else:
        chris_vpc_id = chris_vpcs[0]['VpcId']
        print(f"vpc with name '{chris_vpc_name} & id '{chris_vpc_id}' has been created already...")

    ################## sg params
    jenkins_sg_name = "chris_cicd_vpc_jenkins_sg"
    ansible_sg_name = "chris_cicd_vpc_ansible_sg"
    eksboot_sg_name = "chris_cicd_vpc_eks_boot_sg"
    sg_name_list = [[jenkins_sg_name], [ansible_sg_name], [eksboot_sg_name]]

    for sg_tup in sg_name_list:
        # create sg
        sg_name = sg_tup[0]
        sg_id = None
        sg_desc = ec2_client.describe_security_groups(
            Filters=[
                {
                    'Name': 'tag:name',
                    'Values': [
                        sg_name,
                    ]
                },
            ],
            DryRun=dry_run,
        )

        sg_list = sg_desc.get('SecurityGroups')

        if not sg_list:
            print(f"security group with name '{sg_name}' not exists, and will create one ...")
            
            sg_description = "create sg as " + sg_name + " for safe access"

            sg = ec2_client.create_security_group(
                Description= sg_description,
                GroupName=sg_name,
                VpcId=chris_vpc_id,
                TagSpecifications=[
                    {
                        'ResourceType': 'security-group',
                        'Tags': [
                            {
                                'Key': 'name',
                                'Value': sg_name
                            },
                        ]
                    },
                ],
                DryRun=dry_run
            )

            if len(sg_tup) == 1:
                sg_id = sg.get('GroupId')
                sg_tup.append(sg_id)
                print(f"security group with name '{sg_name}', and id '{sg_id}' is created...")
        else:
            if len(sg_tup) == 1:
                sg_id = sg_list[0].get('GroupId')
                sg_tup.append(sg_id)

            print(f"security group with name '{sg_tup[0]}', and id '{sg_tup[1]}' exists already...")

except ClientError as e:
    print(e)

        