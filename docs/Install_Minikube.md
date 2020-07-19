Note* - The below scripts and steps are used for development purpose and not for production deployment.

# Installing Minikube

Step 1) Run the installation script present in folder - 
~/document-upload/docker/local/configuration/miniKube

For Linux - 
/minikube_linux_install.sh 

For Mac - 
/minikube_mac_install.sh 

### To check if installed successfully -

`minikube version`

Output - 
minikube version: v1.2.0

`minikube status`

Output - 

host: Stopped
kubelet: 
apiserver: 
kubectl: 


### Configure Minikube

Step 1) Start Minikube ( with 3 cpu's and 12Gb Ram)

`minikube start --cpus 3 --memory 12000`

Step 2) Check the Status

`minikube status`

Output - 

host: Running
kubelet: Running
apiserver: Running
kubectl: Correctly Configured: pointing to minikube-vm at 192.168.99.101


Step 2) Open UI

`minikube dashboard
`

### Start services on minikube

Refer to scripts present in -
~/document-upload/docker/local/configuration/service

Step 1) Start Zookeeper 

`kubectl create -f zookeeper.yml`

Note* - Wait status to be Running. We can check through UI as well as from cli running the commands - 

`kubectl get pods `

Step 2) Start kafka 

`kubectl create -f kafka.yml`

Step 3) Start Schema Registry 

`kubectl create -f schema-registry.yml`


### To Verify -

`kubectl get pods`

NAME                READY   STATUS    RESTARTS   AGE
kafka-0             1/1     Running   0          25m
schema-registry-0   1/1     Running   0          23m
zookeeper-0         1/1     Running   0          41m

### To Verify if services are working -

1) Kafka check 

- ssh into kafka pod in minikube -

`kubectl exec -it kafka-0 bash`

- List the topics

`kafka-topics --zookeeper $KAFKA_ZOOKEEPER_CONNECT --list
`
Output - 
__confluent.support.metrics
_schemas


2) Schema- registry check

- Forward the port of schema-registry to access it from outside minikube

`kubectl port-forward schema-registry-0 8081:8081
`
- Registering a New Version of a Schema Under the Subject "Kafka-key"

`curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  --data '{"schema": "{\"type\": \"string\"}"}' \
  http://localhost:8081/subjects/Kafka-key/versions
`
Output - 
{"id":1}

- Listing All Subjects
 
` curl -X GET http://localhost:8081/subjects
`
Output - 
 ["Kafka-value","Kafka-key"]
 

Step 4) Start flink kafka streaming service

`kubectl create -f flink-kafka-streaming.yml`


### TO Stop Minikube 

`minikube stop
`

Output - 
✋  Stopping "minikube" in virtualbox ...
🛑  "minikube" stopped.
