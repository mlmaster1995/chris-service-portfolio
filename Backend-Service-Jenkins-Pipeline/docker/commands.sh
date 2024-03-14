### clean the system
$ sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine



### https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-docker.html
$ sudo yum update -y
$ sudo amazon-linux-extras install docker -y
$ rpm -qa | grep -i docker
# docker-20.10.25-1.amzn2.0.4.x86_64

$ sudo systemctl enable docker
$ sudo systemctl start docker
$ sudo systemctl status docker
# ● docker.service - Docker Application Container Engine
#    Loaded: loaded (/usr/lib/systemd/system/docker.service; enabled; vendor preset: disabled)
#    Active: active (running) since Thu 2024-03-07 23:33:20 UTC; 4s ago
#      Docs: https://docs.docker.com
#   Process: 6548 ExecStartPre=/usr/libexec/docker/docker-setup-runtimes.sh (code=exited, status=0/SUCCESS)
#   Process: 6547 ExecStartPre=/bin/mkdir -p /run/docker (code=exited, status=0/SUCCESS)
#  Main PID: 6551 (dockerd)
#     Tasks: 7
#    Memory: 23.5M
#    CGroup: /system.slice/docker.service
#            └─6551 /usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock --default-ulimit nofile=32768:65536

# Mar 07 23:33:20 ip-10-0-9-151.ec2.internal dockerd[6551]: time="2024-03-07T23:33:20.318897253Z" level=info msg="ClientConn switching balancer to \"pick_first\"" module=grpc
# Mar 07 23:33:20 ip-10-0-9-151.ec2.internal dockerd[6551]: time="2024-03-07T23:33:20.353289122Z" level=warning msg="Your kernel does not support cgroup blkio weight"
# Mar 07 23:33:20 ip-10-0-9-151.ec2.internal dockerd[6551]: time="2024-03-07T23:33:20.353722677Z" level=warning msg="Your kernel does not support cgroup blkio weight_device"
# Mar 07 23:33:20 ip-10-0-9-151.ec2.internal dockerd[6551]: time="2024-03-07T23:33:20.354222071Z" level=info msg="Loading containers: start."
# Mar 07 23:33:20 ip-10-0-9-151.ec2.internal dockerd[6551]: time="2024-03-07T23:33:20.510298144Z" level=info msg="Default bridge (docker0) is assigned with an IP address 172.17.0.0/16. Daemon option --bip can be used to set a preferred IP address"
# Mar 07 23:33:20 ip-10-0-9-151.ec2.internal dockerd[6551]: time="2024-03-07T23:33:20.554533118Z" level=info msg="Loading containers: done."
# Mar 07 23:33:20 ip-10-0-9-151.ec2.internal dockerd[6551]: time="2024-03-07T23:33:20.569560803Z" level=info msg="Docker daemon" commit=5df983c graphdriver(s)=overlay2 version=20.10.25
# Mar 07 23:33:20 ip-10-0-9-151.ec2.internal dockerd[6551]: time="2024-03-07T23:33:20.570016048Z" level=info msg="Daemon has completed initialization"
# Mar 07 23:33:20 ip-10-0-9-151.ec2.internal systemd[1]: Started Docker Application Container Engine.
# Mar 07 23:33:20 ip-10-0-9-151.ec2.internal dockerd[6551]: time="2024-03-07T23:33:20.606147420Z" level=info msg="API listen on /run/docker.sock"

$ grep docker /etc/group
# docker:x:992:

###integrate Jenkins with docker
# add dockeradmin user -> install "publish over ssh" plugin -> config "system" on Jenkins UI -> add ssh server(docker server)
$ sudo useradd dockeradmin 
$ sudo passwd dockeradmin
$ sudo usermod -aG docker dockeradmin
$ ssh-copy-id dockeradmin@<docker-ip> # on Jenkins server


###setup docker env for app deployment in docker server
$ sudo mkdir /opt/docker-test
$ sudo chown -R dockeradmin:dockeradmin /opt/docker-test

# test tomcat image
$ docker pull tomcat:9.0.86-jdk17-corretto-al2
$ docker run --name tomcat9 -d -p8888:8080 5ed

# build, run, start service image
$ cd /opt/docker
$ docker build -t docker-test-app:0.0.1 .
$ docker kill docker-test-app:0.0.1
$ docker rm docker-test-app:0.0.1
$ docker run -d -p8888:8080 docker-test-app:0.0.1