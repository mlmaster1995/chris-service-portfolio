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

### rollout the cluster
$ eksctl create cluster -f eks-cluster.yml --profile chris
# 2024-03-18 18:24:03 [ℹ]  eksctl version 0.174.0-dev+3c1a5c4c2.2024-03-15T18:43:47Z
# 2024-03-18 18:24:03 [ℹ]  using region us-east-1
# 2024-03-18 18:24:03 [✔]  using existing VPC (vpc-0abd1bb7d1a1a20dc) and subnets (private:map[] public:map[us-east-1a:{subnet-0e7f1a11e9ba646a6 us-east-1a 172.31.80.0/20 0 } us-east-1b:{subnet-007bf72041d7ab626 us-east-1b 172.31.16.0/20 0 }])
# 2024-03-18 18:24:03 [!]  custom VPC/subnets will be used; if resulting cluster doesn't function as expected, make sure to review the configuration of VPC/subnets
# 2024-03-18 18:24:04 [ℹ]  nodegroup "test-ng" will use "ami-0b047bdfc83a5c3f4" [AmazonLinux2/1.29]
# 2024-03-18 18:24:04 [ℹ]  using Kubernetes version 1.29
# ...
# 2024-03-18 18:38:48 [ℹ]  kubectl command should work with "/Users/chris.young/.kube/config", try 'kubectl get nodes'
# 2024-03-18 18:38:48 [✔]  EKS cluster "chris-eks" in "us-east-1" region is ready
$ eksctl delete cluster --name chris-eks --profile chris

### install kubectl on amazon linux(https://docs.aws.amazon.com/eks/latest/userguide/install-kubectl.html)
$ uname -a
# Linux eks-bookstrap-server 5.10.210-201.855.amzn2.x86_64 #1 SMP Tue Mar 12 19:03:26 UTC 2024 x86_64 x86_64 x86_64 GNU/Linux
$ curl -O https://s3.us-west-2.amazonaws.com/amazon-eks/1.29.0/2024-01-04/bin/darwin/amd64/kubectl
$ chmod +x ./kubectl
$ mkdir -p $HOME/bin && cp ./kubectl $HOME/bin/kubectl  && rm -rf ./kubectl
$ export PATH=$HOME/bin:$PATH
$ rm -rf ~/.kube
$ aws eks update-kubeconfig --region us-east-1 --name chris-eks --profile chris
# Added new context arn:aws:eks:us-east-1:336371013214:cluster/chris-eks to /Users/chris.young/.kube/config

### install kubectl on mac(https://gist.github.com/Zheaoli/335bba0ad0e49a214c61cbaaa1b20306)
$ curl -O https://s3.us-west-2.amazonaws.com/amazon-eks/1.23.17/2024-01-04/bin/darwin/amd64/kubectl
$ chmod +x ./kubectl
$ sudo mv ./kubectl /usr/local/bin/kubectl
$ rm -rf ~/.kube
$ aws eks update-kubeconfig --region us-east-1 --name chris-eks --profile chris
# Added new context arn:aws:eks:us-east-1:336371013214:cluster/chris-eks to /Users/chris.young/.kube/config
$ kubectl version --client
# Client Version: version.Info{Major:"1", Minor:"23+", GitVersion:"v1.23.17-eks-5e0fdde", GitCommit:"462fb841c09eeed8cb38ba5ee9adf967ef6b56ab", GitTreeState:"clean", BuildDate:"2024-01-02T20:41:24Z", GoVersion:"go1.19.13", Compiler:"gc", Platform:"darwin/amd64"}