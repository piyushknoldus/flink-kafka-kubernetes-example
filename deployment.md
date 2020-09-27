## Steps to deploy the CRDs and Helm Charts

### Prerequisites
	1.Minikube
	2.Kubectl
	3.Helm3

### Create folder and clone the git repo
`mkdir demo && cd $_ && git clone https://github.com/piyushknoldus/flink-kafka-kubernetes-example.git`

### Start K8s cluster on Minikube
`minikube start`

### Checkout to branch
`cd flink-kafka-kubernetes-example/ && git checkout feature/kubernetes-deployment `

### Install chart for kafka
`helm install kafka charts/cp-kafka`

It takes approx. 15min to ready all pods (some pods might restart in between)
Check their status 
`kubectl get po | grep kafka`

### Install chart for flink-operator
`helm install flink-op charts/flink-operator`

Check their status 
`kubectl get po -n flink`


### Install Flink Job cluster
`helm install --name flink-kafka-job-cluster charts/flink-job-cluster -n flink`

Apply watch to see the pods related to flink cluster as they will be terminated once the job is completed. 

`watch kubectl get po -n flink`

Once the pod comes to the completed stage you can check the output of the job 
`kubectl logs $(kubectl get po -o name -n flink | grep flinkjobcluster-sample-job) -n flink -f`

### Install Flink Session cluster
`helm install --name flink-session-cluster charts/flink-session-cluster -n flink`
