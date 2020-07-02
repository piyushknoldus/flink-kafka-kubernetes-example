/*
 * © Copyright 2020
 * Author: Piyush Rana
 * Email: piyush@knoldus.com
 */

package serde

import java.lang

import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord;

class KafkaSimpleSerializer[T](
  topic: String,
  serializer: T => Array[Byte]
) extends KafkaSerializationSchema[T] {
  override def serialize(data: T, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] =
    new ProducerRecord[Array[Byte], Array[Byte]](topic, serializer(data))
}
