###### eks-bookstrap server -> one of the manage node from Ansible
### update hostname
$ sudo yum update -y
$ sudo hostnamectl set-hostname eks-bookstrap-server
$ sudo reboot now
$ sudo hostnamectl
#    Static hostname: eks-bookstrap-server
#          Icon name: computer-vm
#            Chassis: vm
#         Machine ID: 7c9e26b06dc3468f99ea839bbbd6a22e
#            Boot ID: c16e9e2e61754b8696059fa78a7bc238
#     Virtualization: xen
#   Operating System: Amazon Linux 2
#        CPE OS Name: cpe:2.3:o:amazon:amazon_linux:2
#             Kernel: Linux 5.10.210-201.852.amzn2.x86_64
#       Architecture: x86-64


### add new user as ansibleadmin
$ sudo useradd ansibleadmin
$ sudo passwd ansibleadmin
$ sudo usermod -aG wheel ansibleadmin
$ visudo /etc/sudoers
#  %wheel  ALL=(ALL)       NOPASSWD: ALL 


### update ssh server config
$ sudo vi /etc/ssh/sshd_config
# To disable tunneled clear text passwords, change to no here!
PasswordAuthentication yes
#PermitEmptyPasswords no
#PasswordAuthentication no
$ sudo systemctl restart sshd
$ sudo systemctl status sshd
# ● sshd.service - OpenSSH server daemon
#    Loaded: loaded (/usr/lib/systemd/system/sshd.service; enabled; vendor preset: enabled)
#    Active: active (running) since Mon 2024-03-18 21:39:40 UTC; 3s ago
#      Docs: man:sshd(8)
#            man:sshd_config(5)
#  Main PID: 3304 (sshd)
#    CGroup: /system.slice/sshd.service
#            └─3304 /usr/sbin/sshd -D

# Mar 18 21:39:40 eks-bookstrap-server systemd[1]: Stopped OpenSSH server daemon.
# Mar 18 21:39:40 eks-bookstrap-server systemd[1]: Starting OpenSSH server daemon...
# Mar 18 21:39:40 eks-bookstrap-server sshd[3304]: Server listening on 0.0.0.0 port 22.
# Mar 18 21:39:40 eks-bookstrap-server sshd[3304]: Server listening on :: port 22.
# Mar 18 21:39:40 eks-bookstrap-server systemd[1]: Started OpenSSH server daemon.

### rollout eks
$ aws sts get-caller-identity
# {
#     "Account": "...",
#     "UserId": "...:...",
#     "Arn": "arn:aws:sts::<...>:assumed-role/eks-bootstrap-access-role/i-0c6da4de180ecdf1d"
# }

#(https://docs.aws.amazon.com/emr/latest/EMR-on-EKS-DevelopmentGuide/setting-up-eksctl.html)
$ curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
$ sudo cp /tmp/eksctl /usr/local/bin
$ eksctl version
#0.174.0

#https://eksctl.io/usage/minimum-iam-policies/
$ eksctl create cluster -f eks-cluster.yml


#https://docs.aws.amazon.com/eks/latest/userguide/install-kubectl.html
$ uname -a
$ curl -O https://s3.us-west-2.amazonaws.com/amazon-eks/1.23.17/2024-01-04/bin/linux/amd64/kubectl
$ chmod +x ./kubectl
$ sudo cp ./kubectl /usr/local/bin/kubectl
$ aws eks update-kubeconfig --region us-east-1 --name chris-eks
# Added new context arn:aws:eks:us-east-1:336371013214:cluster/chris-eks to /Users/chris.young/.kube/config

$ kubectl get nodes -owide
# NAME                            STATUS   ROLES    AGE    VERSION               INTERNAL-IP     EXTERNAL-IP    OS-IMAGE         KERNEL-VERSION                  CONTAINER-RUNTIME
# ip-172-31-30-176.ec2.internal   Ready    <none>   109m   v1.29.0-eks-5e0fdde   172.31.30.176       ...      Amazon Linux 2   5.10.210-201.852.amzn2.x86_64   containerd://1.7.11
# ip-172-31-85-62.ec2.internal    Ready    <none>   109m   v1.29.0-eks-5e0fdde   172.31.85.62        ...      Amazon Linux 2   5.10.210-201.852.amzn2.x86_64   containerd://1.7.11

$ eksctl delete cluster --region us-east-1 --name chris-eks


$ vi ~/.bashrc
# alias k='kubectl'

