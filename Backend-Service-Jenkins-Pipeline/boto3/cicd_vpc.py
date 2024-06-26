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
import time

ec2_client = boto3.client('ec2')

dry_run = False

################## vpc params
chris_vpc_id = None
chris_vpc_name = 'chris-cicd-vpc'
chris_vpc_cidrBlock = '192.168.0.0/16'
chris_route_table_name='chris-vpc-pub-route-table'
chris_subnet_info=[('pri-us-east-1a','192.168.0.0/20','us-east-1a'),
               ('pri-us-east-1b','192.168.16.0/20','us-east-1b'),
               ('pri-us-east-1c','192.168.32.0/20','us-east-1c'),
               ('pub-us-east-1d','192.168.48.0/20','us-east-1d'),
               ('pub-us-east-1e','192.168.64.0/20','us-east-1e'),
               ('pub-us-east-1f','192.168.80.0/20','us-east-1f')]


################## create vpc
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
    print(f"vpc with name '{chris_vpc_name}' not exists, and will create one...")

    chris_vpc = ec2_client.create_vpc(
        CidrBlock=chris_vpc_cidrBlock,
        DryRun=dry_run,
        TagSpecifications=[
            {
                'ResourceType': 'vpc',
                'Tags': [
                    {
                        'Key': 'name',
                        'Value': chris_vpc_name
                    },
                ]
            },
        ]
    )

    # wait for creation
    time.sleep(5)

    chris_vpc_id = chris_vpc.get('Vpc').get('VpcId')
    print(f"vpc with name '{chris_vpc_name}' & id '{chris_vpc_id}' is created...")
else:
    chris_vpc_id = chris_vpcs[0]['VpcId']
    print(f"vpc with name '{chris_vpc_name} & id '{chris_vpc_id}' exists already...")


################## create igw
chris_igw_name = "chris-vpc-igw"
chris_igw_desc = ec2_client.describe_internet_gateways(
    Filters=[
        {
            'Name': 'tag:name',
            'Values': [
                chris_igw_name,
            ]
        },
    ],
    DryRun=dry_run
)

chris_igws = chris_igw_desc.get('InternetGateways')
chris_igw_id = None
chris_igw_attached = None
if not chris_igws:
    print(f"igw with name '{chris_igw_name}' not exists, and will create a new one...")
    chris_igw = ec2_client.create_internet_gateway(
        TagSpecifications=[
            {
                'ResourceType': 'internet-gateway',
                'Tags': [
                    {
                        'Key': 'name',
                        'Value': chris_igw_name
                    },
                ]
            },
        ],
        DryRun=dry_run
    )

    # wait for creation
    time.sleep(5)

    chris_igw_id = chris_igw.get('InternetGateway').get('InternetGatewayId')
    print(f"igw with name '{chris_igw_name}' & id '{chris_igw_id}' is created...")

    chris_igw_attached = False
else:
    chris_igw_id = chris_igws[0].get('InternetGatewayId')
    print(f"igw with name '{chris_igw_name}' & id '{chris_igw_id}' exists already...")

    chris_igw_attached = chris_igws[0].get('Attachments')[0].get('State') == 'available'

#attach to vpc
if not chris_igw_attached:
    chris_igw_attach = ec2_client.attach_internet_gateway(
        DryRun=dry_run,
        InternetGatewayId=chris_igw_id,
        VpcId=chris_vpc_id
    )
    print(f"igw with name '{chris_igw_name}' is attached to the vpc with id '{chris_vpc_id}'")
else: 
    print(f"igw with name '{chris_igw_name}' is attached already...")




################## create route table
chris_route_desc = ec2_client.describe_route_tables(
    Filters = [
        {
            'Name': 'tag:name',
            'Values': [
                chris_route_table_name,
            ]
        },
    ],
    DryRun=dry_run
)

chris_route_tables = chris_route_desc.get('RouteTables')
chris_route_table_id = None
if not chris_route_tables:
    print(f"route table with name '{chris_route_table_name} not exists and will create it...'")

    chris_route_table = ec2_client.create_route_table(
        DryRun = dry_run,
        VpcId = chris_vpc_id,
        TagSpecifications=[
            {
                'ResourceType': 'route-table',
                'Tags': [
                    {
                        'Key': 'name',
                        'Value': chris_route_table_name
                    },
                ]
            },
        ]
    )

    time.sleep(5)

    chris_route_table_id = chris_route_table.get('RouteTable').get('RouteTableId')
    print(f"route table with name '{chris_route_table_name}' and id '{chris_route_table_id}' is created...")

    # add igw to the route table 
    chris_route = ec2_client.create_route(
        DestinationCidrBlock='0.0.0.0/0',
        DryRun=dry_run,
        GatewayId=chris_igw_id,
        RouteTableId=chris_route_table_id
    )

else:
    chris_route_table_id = chris_route_tables[0].get('RouteTableId')
    print(f"route table with name '{chris_route_table_name}' and id '{chris_route_table_id}' exists already...")

################## create subnets
# 
# RFC1918 range: 
# 10.0.0.0-10.255.255.255 (10/8 prefix)
# VPC must smaller the above range ->  10.0.0.0/16
# 
# RFC1918 range: 
# 172.16.0.0 - 172.31.255.255 (172.16/12 prefix)
# VPC ... ->  172.31.0.0/16
# 
# RFC1918 range:
# 192.168.0.0 - 192.168.255.255 (192.168/16 prefix)
# VPC ... -> 192.168.0.0/16
# 
# 4096-host/subnet:
# subnet pri us-east-1a ->  192.168.0.0/20
# subnet pri us-east-1b ->  192.168.16.0/20
# subnet pri us-east-1c ->  192.168.32.0/20
# subnet pub us-east-1d ->  192.168.48.0/20
# subnet pub us-east-1e ->  192.168.64.0/20
# subnet pub us-east-1f ->  192.168.80.0/20
# 
# internet gw -> attach to vpc
# 
# nat gw (here not use nat instance) -> created in the public subnet -> created one elastic ip 
# 
# public route table -> attach igw to the table
# 
# private route table -> associate the private net -> attach nat to the table
# 
for subnet_info in chris_subnet_info:
    subnet_name = subnet_info[0]
    subnet_cidrBlock = subnet_info[1]
    subnet_zone = subnet_info[2]

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

    # create subnet if not exists
    if not chris_subnets:
        print(f"subnet with name '{subnet_name}' not exists, and will create a new one...")
        chris_subnet = ec2_client.create_subnet(
            TagSpecifications=[
                {
                    'ResourceType': 'subnet',
                    'Tags': [
                        {
                            'Key': 'name',
                            'Value': subnet_name
                        },
                    ]
                },
            ],
            AvailabilityZone = subnet_zone,
            CidrBlock = subnet_cidrBlock,
            VpcId= chris_vpc_id,
            DryRun=dry_run
        )

        print(f"subnet with name '{subnet_name}' is created...")

        ##associate subnet to the route table
        #if 'pub' in subnet_name:
        subnet_id = chris_subnet.get('Subnet').get('SubnetId')
        associate_route_table = ec2_client.associate_route_table(
            DryRun = dry_run,
            RouteTableId = chris_route_table_id,
            SubnetId = subnet_id
        )

        print(f"subnet with name '{subnet_name}' has been associated with route table with name '{chris_route_table_name}'")
    else:
        print(f"subnet with name '{subnet_name}' exists already...")