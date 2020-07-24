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

### Install CRD for flink-operator
`kubectl apply -f crd/flink-operator-crd.yaml`


### Install chart for flink-operator
`helm install flink-op charts/flink-operator`

It takes approx. 5min to complete job reg. certs and ready the pods
Check their status 
`kubectl get po -n flink`


### Create Flink cluster and submit job
`kubectl apply -f charts/flink-job-cluster/1topic2another.yaml`

Apply watch to see the pods related to flink cluster as they will be terminated once the job is completed. 

`watch kubectl get po`

Once the pod comes to the completed stage you can check the output of the job 
`kubectl logs $(kubectl get po -o name | grep flinkjobcluster-sample-job) -f`


