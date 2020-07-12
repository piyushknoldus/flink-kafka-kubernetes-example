/*
 * © Copyright 2020
 * Author: Piyush Rana
 * Email: piyush@knoldus.com
 */
package connector

import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import model.{KafkaEndpoint, KafkaProducerConfig}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaProducer, KafkaSerializationSchema}
import org.apache.kafka.clients.producer.ProducerConfig

object KafkaProducer extends LazyLogging {
  def apply[T](config: KafkaProducerConfig,
               endpoint: KafkaEndpoint,
               topicNameStrategy: => String,
               serializer: String => KafkaSerializationSchema[T]): Either[Exception, FlinkKafkaProducer[T]] = {
    val prop = new Properties()
    prop.setProperty(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
      s"${endpoint.host}:${endpoint.port}"
    )
    prop.put(ProducerConfig.ACKS_CONFIG, String.valueOf(config.ack))
    prop.put(ProducerConfig.BUFFER_MEMORY_CONFIG, String.valueOf(config.bufferMemory))
    prop.put(ProducerConfig.LINGER_MS_CONFIG, String.valueOf(config.lingerMs))
    prop.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, String.valueOf(config.transactionalId))
    config.transactionTimeoutMs.map(v => prop.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, String.valueOf(v)))

    val topicName = topicNameStrategy

    try {
      lazy val sink = new FlinkKafkaProducer[T](
        topicName,
        serializer(topicName),
        prop,
        FlinkKafkaProducer.Semantic.EXACTLY_ONCE
      )

      sink.setWriteTimestampToKafka(config.writeTimestamp)
      logger.info("Created kafka producer with output topic {}", topicName)
      Right(sink)
    } catch {
      case e: Exception =>
        Left(new Exception(s"Unable to create KafkaProducer, Original Error: ${e.getMessage()}", e))
    }
  }
}
