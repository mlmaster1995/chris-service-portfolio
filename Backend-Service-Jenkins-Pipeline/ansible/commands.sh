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

########Ansible Server as a master node for CI/CD with Jenkins
### update hostname
$ sudo yum update -y
$ sudo hostnamectl set-hostname ansible-server
$ sudo reboot now
$ hostnamectl
# Static hostname: ansible-server
#         Icon name: computer-vm
#         Chassis: vm
#     Machine ID: 0aa5104ce22840988d0953dde2ad309a
#         Boot ID: d5a171172a6a470c8b062b4613dada09
# Virtualization: xen
# Operating System: Amazon Linux 2
#     CPE OS Name: cpe:2.3:o:amazon:amazon_linux:2
#         Kernel: Linux 5.10.210-201.852.amzn2.x86_64
#     Architecture: x86-64

### add new user
$ sudo useradd ansibleadmin
$ sudo passwd ansibleadmin
$ sudo usermod -aG wheel ansibleadmin
$ visudo /etc/sudoers
#  %wheel  ALL=(ALL)       NOPASSWD: ALL 

### setup ansible package
$ sudo amazon-linux-extras install ansible2 -y
$ rpm -qa | grep -i ansible
# ansible-2.9.23-1.amzn2.noarch
$ ansible --version
# ansible 2.9.23
#   config file = /etc/ansible/ansible.cfg
#   configured module search path = [u'/home/ansibleadmin/.ansible/plugins/modules', u'/usr/share/ansible/plugins/modules']
#   ansible python module location = /usr/lib/python2.7/site-packages/ansible
#   executable location = /usr/bin/ansible
#   python version = 2.7.18 (default, Dec 18 2023, 22:08:43) [GCC 7.3.1 20180712 (Red Hat 7.3.1-17)]

$ sudo vi /etc/ssh/sshd_config # enable password auth access
$ sudo systemctl restart sshd

$ sudo ssh-keygen -C ''
$ sudo ssh-copy-id ansibleadmin@<managed-node-id> # --->  !!!same user & setup needs to be done on the managed node as well and also update new users on all accessed documents by ansible!!!!

$ sudo vi /etc/ansible/hosts
#[<managed-node-group-name>]
#<managed-node-ip>

$ ansible all -m ping
# [WARNING]: Platform linux on host 1xx.xx.xx.xxx is using the discovered Python interpreter at /usr/bin/python, but future
# installation of another Python interpreter could change this. See
# https://docs.ansible.com/ansible/2.9/reference_appendices/interpreter_discovery.html for more information.
# 1xx.xx.xx.xxx | SUCCESS => {
#     "ansible_facts": {
#         "discovered_interpreter_python": "/usr/bin/python"
#     },
#     "changed": false,
#     "ping": "pong"
# }

$ ansible all -m command -a uptime
# 1xx.3x.8x.2xx | CHANGED | rc=0 >>
#  23:11:48 up 8 min,  2 users,  load average: 0.00, 0.01, 0.00

$ ansible all --list-hosts
$ ansible-inventory --graph
$ ansible-inventory --list
$ ansible-doc -l

$ ansible-playbook playbook.yml
$ ansible-playbook --syntax-check playbook.yml
$ ansible-playbook -vv -C playbook.yml # dry run with task results and taks config
$ ansible-playbook -vv --ask-vault-pass playbook.yml # ansible ask for password intput manually
$ ansible-playbook --vault-password-file=vault-pass playbook.yml # automate the process


###integrate jenkins to ansible
#1 update push over ssh to Jenins server
#2 must setup ssh access to ansible server on the Jenkins server
#3 update id_rsa(from Jenkins server)as access key on Jenkins config to access Ansible server
$ sudo chown ansibleadmin:ansibleadmin -R /opt/docker


###docker module: https://docs.ansible.com/ansible/latest/collections/community/docker/docker_image_module.html#examples
$ ansible-galaxy collection list
# ansible 2.9.23
#   config file = /etc/ansible/ansible.cfg
#   configured module search path = [u'/home/ansibleadmin/.ansible/plugins/modules', u'/usr/share/ansible/plugins/modules']
#   ansible python module location = /usr/lib/python2.7/site-packages/ansible
#   executable location = /usr/bin/ansible
#   python version = 2.7.18 (default, Dec 18 2023, 22:08:43) [GCC 7.3.1 20180712 (Red Hat 7.3.1-17)]
$ ansible-galaxy collection install community.docker
$ python list-collections.py
# (u'community.docker', u'/home/ansibleadmin/.ansible/collections/ansible_collections/community/docker')


###install helm

###ansible vault
$ ansible-vault create secret.yaml
$ ansible-vault create --vault-password-file=passfile secret.yaml
$ ansible-vault encrypt file.yaml
$ ansible-vault decrypt file.yaml
$ ansible-vault view file.yaml
$ ansible-vault rekey file.yaml #change the paswd
