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

### always use root user
$ sudo passwd root
$ su -

### install java-17 on amazon linux2
$ sudo yum install java-17-amazon-corretto.x86_64 -y

$ java --version
# openjdk 17.0.10 2024-01-16 LTS
# OpenJDK Runtime Environment Corretto-17.0.10.8.1 (build 17.0.10+8-LTS)
# OpenJDK 64-Bit Server VM Corretto-17.0.10.8.1 (build 17.0.10+8-LTS, mixed mode, sharing)


### install Jenkins: https://www.jenkins.io/doc/book/installing/linux/#fedora
$ sudo wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
$ sudo yum repolist
# Loaded plugins: extras_suggestions, langpacks, priorities, update-motd
# jenkins                                                                                                          | 2.9 kB  00:00:00
# jenkins/primary_db                                                                                               |  49 kB  00:00:00
# repo id                                                        repo name                                                          status
# amzn2-core/2/x86_64                                            Amazon Linux 2 core repository                                     34,262
# amzn2extra-docker/2/x86_64                                     Amazon Extras repo for docker                                         108
# amzn2extra-kernel-5.10/2/x86_64                                Amazon Extras repo for kernel-5.10                                    470
# jenkins                                                        Jenkins-stable                                                        157
# repolist: 34,997

$ sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key
$ sudo yum upgrade -y
# Loaded plugins: extras_suggestions, langpacks, priorities, update-motd
# No packages marked for update


### Add required dependencies for the jenkins package
$ sudo yum install fontconfig -y
# Loaded plugins: extras_suggestions, langpacks, priorities, update-motd
# Package fontconfig-2.13.0-4.3.amzn2.x86_64 already installed and latest version
# Nothing to do

$ yum search jenkins
# Loaded plugins: extras_suggestions, langpacks, priorities, update-motd
# =================== N/S matched: jenkins =====================================
# jenkins.noarch : Jenkins Automation Server
#   Name and summary matches only, use "search all" for everything.


$ sudo yum install jenkins -y
$ rpm -qa | grep -i jenkins
# jenkins-2.440.1-1.1.noarch


$ sudo systemctl daemon-reload
$ sudo systemctl enable jenkins
$ sudo systemctl start jenkins
$ sudo systemctl status jenkins
# ● jenkins.service - Jenkins Continuous Integration Server
#    Loaded: loaded (/usr/lib/systemd/system/jenkins.service; enabled; vendor preset: disabled)
#    Active: active (running) since Fri 2024-03-01 02:42:16 UTC; 11s ago
#  Main PID: 16440 (java)
#    CGroup: /system.slice/jenkins.service
#            └─16440 /usr/bin/java -Djava.awt.headless=true -jar /usr/share/java/jenkins.war --webroot=%C/jenkins/war --httpPort=8080

# Mar 01 02:41:47 ip-10-0-3-107.ec2.internal jenkins[16440]: 9bafd90c38d3405caab8d593461fc9d9
# Mar 01 02:41:47 ip-10-0-3-107.ec2.internal jenkins[16440]: This may also be found at: /var/lib/jenkins/secrets/initialAdminPassword
# Mar 01 02:41:47 ip-10-0-3-107.ec2.internal jenkins[16440]: *************************************************************
# Mar 01 02:41:47 ip-10-0-3-107.ec2.internal jenkins[16440]: *************************************************************
# Mar 01 02:41:47 ip-10-0-3-107.ec2.internal jenkins[16440]: *************************************************************
# Mar 01 02:42:16 ip-10-0-3-107.ec2.internal jenkins[16440]: 2024-03-01 02:42:16.462+0000 [id=32]        INFO        jenkins.InitReactorRunner$1#onAttained: Completed initialization
# Mar 01 02:42:16 ip-10-0-3-107.ec2.internal jenkins[16440]: 2024-03-01 02:42:16.500+0000 [id=25]        INFO        hudson.lifecycle.Lifecycle#onReady: Jenkins is fully up and running
# Mar 01 02:42:16 ip-10-0-3-107.ec2.internal systemd[1]: Started Jenkins Continuous Integration Server.
# Mar 01 02:42:16 ip-10-0-3-107.ec2.internal jenkins[16440]: 2024-03-01 02:42:16.596+0000 [id=48]        INFO        h.m.DownloadService$Downloadable#load: Obtained the updated data file for hudson.tasks.Maven.MavenInstaller
# Mar 01 02:42:16 ip-10-0-3-107.ec2.internal jenkins[16440]: 2024-03-01 02:42:16.597+0000 [id=48]        INFO        hudson.util.Retrier#start: Performed the action check updates server successfully at the attempt #1

$ sudo cat /var/lib/jenkins/secrets/initialAdminPassword
# <temp passwd>


### jenkins integrates with git
#  install git -> install 'GitHub' plugin -> 'Tools' to config git on Jenkins UI
$ yum search git
$ sudo yum install git -y
$ git --version
# git version 2.40.1



$ ls -liha /var/lib/jenkins/workspace/GitPullJob
# total 24K
# 14490431 drwxr-xr-x 5 jenkins jenkins  147 Mar 11 22:07 .
#  9765792 drwxr-xr-x 3 jenkins jenkins   24 Mar 11 22:07 ..
# 14490439 -rw-r--r-- 1 jenkins jenkins  130 Mar 11 22:07 Dockerfile
#   936087 drwxr-xr-x 8 jenkins jenkins  162 Mar 11 22:07 .git
# 14490441 -rw-r--r-- 1 jenkins jenkins 6.2K Mar 11 22:07 pom.xml
# 14490440 -rw-r--r-- 1 jenkins jenkins  271 Mar 11 22:07 README.md
# 14490442 -rw-r--r-- 1 jenkins jenkins  479 Mar 11 22:07 regapp-deploy.yml
# 14490443 -rw-r--r-- 1 jenkins jenkins  195 Mar 11 22:07 regapp-service.yml
#   936099 drwxr-xr-x 3 jenkins jenkins   32 Mar 11 22:07 server
#   936103 drwxr-xr-x 3 jenkins jenkins   32 Mar 11 22:07 webapp

### jenkins integrates with maven
# install maven -> update env path -> install 'Maven Integration' plugin on jenkins ui -> 'Tool' to config Java & maven on Jenkins UI
$ sudo find / -iname jvm
# /etc/jvm
# /usr/lib/jvm
# /usr/share/jvm


#https://maven.apache.org/install.html
#https://maven.apache.org/download.cgi
$ sudo wget https://dlcdn.apache.org/maven/maven-3/3.9.7/binaries/apache-maven-3.9.7-bin.tar.gz
$ tar xzvf apache-maven-3.9.7-bin.tar.gz


$ vi ~/.bash_profile
# export JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto
# export PATH=${PATH}:${JAVA_HOME}/bin

# export MAVEN_HOME=/opt/maven
# export PATH=${PATH}:${MAVEN_HOME}/bin


$ source ~/.bash_profile
$ java --version
# openjdk 17.0.10 2024-01-16 LTS
# OpenJDK Runtime Environment Corretto-17.0.10.8.1 (build 17.0.10+8-LTS)
# OpenJDK 64-Bit Server VM Corretto-17.0.10.8.1 (build 17.0.10+8-LTS, mixed mode, sharing)


$ mvn --version
# Apache Maven 3.9.6 (bc0240f3c744dd6b6ec2920b3cd08dcc295161ae)
# Maven home: /opt/maven
# Java version: 17.0.10, vendor: Amazon.com Inc., runtime: /usr/lib/jvm/java-17-amazon-corretto.x86_64
# Default locale: en_CA, platform encoding: UTF-8
# OS name: "linux", version: "5.10.209-198.858.amzn2.x86_64", arch: "amd64", family: "unix"


$ ls -liha /var/lib/jenkins/workspace/MavenBuildJob/webapp/target/
# total 4.0K
#  9868988 drwxr-xr-x 5 jenkins jenkins   76 Mar 11 22:21 .
#   895144 drwxr-xr-x 4 jenkins jenkins   46 Mar 11 22:21 ..
#  1008303 drwxr-xr-x 2 jenkins jenkins   28 Mar 11 22:21 maven-archiver
# 14531080 drwxr-xr-x 2 jenkins jenkins    6 Mar 11 22:21 surefire
#  1008301 drwxr-xr-x 4 jenkins jenkins   54 Mar 11 22:21 webapp
#  9869556 -rw-r--r-- 1 jenkins jenkins 2.4K Mar 11 22:21 webapp.war
