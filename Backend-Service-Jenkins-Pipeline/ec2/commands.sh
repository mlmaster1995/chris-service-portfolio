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

###change server name
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


###add swap file: https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux/7/html/storage_administration_guide/ch-swapspace
$ sudo dd if=/dev/zero of=/swapfile bs=1048576 count=2024
# 2024+0 records in
# 2024+0 records out
# 2122317824 bytes (2.1 GB) copied, 31.1797 s, 68.1 MB/s

$ sudo mkswap /swapfile
# mkswap: /swapfile: insecure permissions 0644, 0600 suggested.
# Setting up swapspace version 1, size = 2 GiB (2122313728 bytes)
# no label, UUID=59b5a7d5-f89a-4dd1-bfad-4d070bf22dbe

$ sudo chmod 0600 /swapfile
$ sudo vi /etc/fstab
# /swapfile          swap            swap    defaults        0 0
$ sudo systemctl daemon-reload
$ sudo swapon /swapfile
$ free -h
#               total        used        free      shared  buff/cache   available
# Mem:           952M        486M         69M        412K        396M        329M
# Swap:          2.0G          0B        2.0G

$ man -k 'ansible'