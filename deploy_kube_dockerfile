FROM openjdk:8u232 as build

ARG SBT_VERSION=1.3.12

# Install sbt
RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion
LABEL Description="Flink kafka Streaming"

ENV WORK_DIR="/flink-kafka-kube"
USER root

COPY ./ ${WORK_DIR}/

RUN cd ${WORK_DIR}/ && sbt assembly

RUN mkdir ${WORK_DIR}/out/ && \
    mv flink-kafka-kube/target/scala-*/flink-kafka-kubernetes-example-assembly-*.jar ${WORK_DIR}/out/app.jar

FROM flink:1.10.1-scala_2.12
ENV WORK_DIR="/flink-kafka-kube"

COPY --from=build ${WORK_DIR}/out /

RUN mv /app.jar /opt/flink

USER flink
EXPOSE 8081 6123
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["--help"]