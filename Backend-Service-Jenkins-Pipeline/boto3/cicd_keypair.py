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
    ec2_client = boto3.client('ec2')

    ec2_key_name = 'cicdkey'
    ec2_key_type = 'rsa'
    ec2_key_id = None

    ec2_key_desc = ec2_client.describe_key_pairs(
        Filters=[
            {
                'Name': 'tag:name',
                'Values': [
                    ec2_key_name,
                ]
            },
        ],
        DryRun=dry_run
    )    

    ec2_keypairs = ec2_key_desc.get('KeyPairs')

    if not ec2_keypairs:
        print(f"keypair with name '{ec2_key_name}' not exists and will create a new one...")

        ec2_key_pair = ec2_client.create_key_pair(
            KeyName=ec2_key_name,
            DryRun=dry_run,
            KeyType=ec2_key_type,
            TagSpecifications=[
                {
                    'ResourceType': 'key-pair',
                    'Tags': [
                        {
                            'Key': 'name',
                            'Value': ec2_key_name
                        },
                    ]
                },
            ],
            KeyFormat='pem'
        )

        ec2_key_id = ec2_key_pair.get('KeyPairId')
        print(f"keypair with name '{ec2_key_name}', id'{ec2_key_id}' is created...")
    else: 
        print(f"keypair with name '{ec2_key_name}' exists already...") 

except ClientError as e:
    print(e)