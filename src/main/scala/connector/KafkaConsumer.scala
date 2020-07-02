/*
 * © Copyright 2020
 * Author: Piyush Rana
 * Email: piyush@knoldus.com
 */
package connector

import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import model.{KafkaConsumerConfig, KafkaEndpoint}
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.kafka.clients.consumer.ConsumerConfig

object KafkaConsumer extends LazyLogging {
  def apply[T](config: KafkaConsumerConfig,
               endpoint: KafkaEndpoint,
               deSerializer: () => DeserializationSchema[T]): Either[Throwable, FlinkKafkaConsumer[T]] = {
    val kafkaConsumerProp = new Properties()
    kafkaConsumerProp.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, s"${endpoint.host}:${endpoint.port}")
    config.consumerGroupId.map(v => kafkaConsumerProp.put(ConsumerConfig.GROUP_ID_CONFIG, v))

    try {
      lazy val source: FlinkKafkaConsumer[T] =
        new FlinkKafkaConsumer[T](config.topic, deSerializer(), kafkaConsumerProp)

      config.startFromLatest match {
        case Some(v) =>
          if (v) { source.setStartFromLatest() } else {
            source.setStartFromEarliest()
          }
        case None => source.setStartFromLatest()
      }

      logger.info("Created kafka consumer with input topic {}", config.topic)
      Right(source)
    } catch {
      case e: Exception =>
        Left(
          new RuntimeException(s"Unable to create KafkaConsumer, Original Error: ${e.getMessage}", e)
        )
    }
  }
}
