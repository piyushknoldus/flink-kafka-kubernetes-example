#! /bin/bash

# Update ubuntu dependencies
sudo apt-get update
sudo apt-get install -y apt-transport-https

# # Install VirtualBox on Ubuntu
sudo apt-get install -y virtualbox virtualbox-ext-pack

#Install kubectl
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
sudo touch /etc/apt/sources.list.d/kubernetes.list
 echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee -a /etc/apt/sources.list.d/kubernetes.list
sudo apt-get update
sudo apt-get install -y kubectl

#Install minikube
curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64

chmod +x minikube && sudo mv minikube /usr/local/bin/





