### change server name
$ sudo chmod 600 ./cicdkey.pem
$ sudo hostnamectl set-hostname jenkins-server
$ hostnamectl
#    Static hostname: jenkins-server
#          Icon name: computer-vm
#            Chassis: vm
#         Machine ID: b03af172b99b40b191ebaa756e5644f8
#            Boot ID: 4cc0fd9a3ab2453498b617d29dbfe716
#     Virtualization: xen
#   Operating System: Amazon Linux 2
#        CPE OS Name: cpe:2.3:o:amazon:amazon_linux:2
#             Kernel: Linux 5.10.210-201.852.amzn2.x86_64
#       Architecture: x86-64

###enable ssh on jenkins
$ sudo vi /etc/ssh/sshd_config
# To disable tunneled clear text passwords, change to no here!
PasswordAuthentication yes
#PermitEmptyPasswords no
#PasswordAuthentication no

$ sudo passwd ec2-user
$ ssh-keygen -c ''
$ ssh-copy-id ec2-user@<docker-server-ip>


