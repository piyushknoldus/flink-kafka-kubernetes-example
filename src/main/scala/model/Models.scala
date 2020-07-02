/*
 * © Copyright 2020
 * Author: Piyush Rana
 * Email: piyush@knoldus.com
 */
package model

import runners.TaskConfig

case class ApplicationLogs(appName: String, dateTime: Long, logType: String, msg: String)

object ApplicationLogs {
  import io.circe._
  import io.circe.generic.semiauto._

  implicit val applicationLogsEncoder: Decoder[ApplicationLogs] = deriveDecoder[ApplicationLogs]
  implicit val applicationLogsDecoder: Encoder[ApplicationLogs] = deriveEncoder[ApplicationLogs]
}

case class KafkaEndpoint(host: String, port: Int)

case class KafkaConsumerConfig(topic: String, consumerGroupId: Option[String], startFromLatest: Option[Boolean])

case class KafkaProducerConfig(
                                topic: Option[String],
                                sideOutPrefix: Option[String],
                                bufferMemory: Long,
                                lingerMs: Long,
                                ack: String,
                                writeTimestamp: Boolean,
                                transactionTimeoutMs: Option[Int]
                              )

case class JobConfig(
                      task: TaskConfig,
                      input: KafkaConsumerConfig,
                      output: KafkaProducerConfig,
                      kafka: KafkaEndpoint,
                      logExecutionPlan: Option[Boolean]
                    )

