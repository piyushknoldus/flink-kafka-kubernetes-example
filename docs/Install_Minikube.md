Note* - The below scripts and steps are used for development purpose and not for production deployment.

# Installing Minikube

Step 1) Run the installation script present in folder - 
flink-kafka-kubernetes-example/docker/minikube

For Linux - 
/minikube_linux_install.sh 

For Mac - 
/minikube_mac_install.sh 

### To check if installed successfully -

`minikube version`

Output - 
`
minikube version: v1.12.1
commit: 5664228288552de9f3a446ea4f51c6f29bbdd0e0-dirty
`

### Configure Minikube

Start Minikube ( with 3 cpu's and 12Gb Ram)

`minikube start --cpus 2 --memory 6000`

Check the Status

`minikube status`

Output - 

minikube
type: Control Plane
host: Running
kubelet: Running
apiserver: Running
kubeconfig: Configured


Open UI

`minikube dashboard
`

### TO Stop Minikube 

`minikube stop
`

Output - 
✋  Stopping "minikube" in virtualbox ...
🛑  "minikube" stopped.
