import boto3
import time


ec2_client = boto3.client('ec2')


################## create vpc
chris_vpc_name = 'chris-vpc-20240330'
chris_vpc_cidrBlock = '192.168.0.0/16'

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

chris_vpc_id = None
chris_vpcs = chris_vpc_desc.get('Vpcs')
if not chris_vpcs:
    print(f"vpc with name '{chris_vpc_name}' not exists, and will create one...")

    chris_vpc = ec2_client.create_vpc(
        CidrBlock=chris_vpc_cidrBlock,
        DryRun=False,
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
    DryRun=False
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
        DryRun=False
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
        DryRun=False,
        InternetGatewayId=chris_igw_id,
        VpcId=chris_vpc_id
    )
    print(f"igw with name '{chris_igw_name}' is attached to the vpc with id '{chris_vpc_id}'")
else: 
    print(f"igw with name '{chris_igw_name}' is attached already...")




################## create route table
chris_route_table_name='chris-vpc-pub-route-table'

chris_route_desc = ec2_client.describe_route_tables(
    Filters = [
        {
            'Name': 'tag:name',
            'Values': [
                chris_route_table_name,
            ]
        },
    ],
    DryRun=False
)

chris_route_tables = chris_route_desc.get('RouteTables')
chris_route_table_id = None
if not chris_route_tables:
    print(f"route table with name '{chris_route_table_name} not exists and will create it...'")

    chris_route_table = ec2_client.create_route_table(
        DryRun = False,
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
        DryRun=False,
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
chris_subnet_info=[('pri-us-east-1a','192.168.0.0/20','us-east-1a'),
               ('pri-us-east-1b','192.168.16.0/20','us-east-1b'),
               ('pri-us-east-1c','192.168.32.0/20','us-east-1c'),
               ('pub-us-east-1d','192.168.48.0/20','us-east-1d'),
               ('pub-us-east-1e','192.168.64.0/20','us-east-1e'),
               ('pub-us-east-1f','192.168.80.0/20','us-east-1f')]

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
        DryRun=False,
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
            DryRun=False
        )

        print(f"subnet with name '{subnet_name}' is created...")

        ##associate subnet to the route table
        #if 'pub' in subnet_name:
        subnet_id = chris_subnet.get('Subnet').get('SubnetId')
        associate_route_table = ec2_client.associate_route_table(
            DryRun = False,
            RouteTableId = chris_route_table_id,
            SubnetId = subnet_id
        )

        print(f"subnet with name '{subnet_name}' has been associated with route table with name '{chris_route_table_name}'")
    else:
        print(f"subnet with name '{subnet_name}' exists already...")