### flink-kafka-kubernetes-example

The sample example of Flink illustrating the generic way to code kafka producer and consumer and integrating it with 
Flink with packaging it into Flink custom job cluster docker image and deploying it on kubernetes.

### To run using sbt locally
1) Make sure the docker compose is up that would start the Kafka, Zookeeper and KafDrop.

`sudo docker-compose -f docker-compose.kafka.yml up`


2) Insert some dummy message in INPUT_TOPIC i.e inputtopic by running the KafkaDummyProducer in 

`sbt it:run`

This will insert some 5 dummy messages in kafka in array[byte]. This can be view in kafka via KafDrop on 
`http://localhost:9001/topic/inputtopic`

3) run the flink job

`sbt run`

#### Output - 
 
5 messages will be inserted in the outputtopic in kafka.

Kafdrop URL - `http://localhost:9001/topic/outputtopic`


### To run using docker images via docker compose files

1) Make sure the docker compose is up that would start the Kafka, Zookeeper and KafDrop.

`sudo docker-compose -f docker-compose.kafka.yml up`

2) Run the Job docker compose file

`sudo docker-compose -f docker-compose.jobs.yml up`
