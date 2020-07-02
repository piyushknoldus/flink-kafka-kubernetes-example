/*
 * © Copyright 2020
 * Author: Piyush Rana
 * Email: piyush@knoldus.com
 */

package serde

import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation

class KafkaSimpleDeserializer[T](
  parser: Array[Byte] => T,
  clazz: Class[T]
) extends DeserializationSchema[T] {
  override def deserialize(message: Array[Byte]): T = parser(message)
  override def isEndOfStream(nextElement: T): Boolean = false

  // Method to decide whether the element signals the end of the stream.
  // Since all our messages are data messages this is to be false
  override def getProducedType: TypeInformation[T] = TypeInformation.of(clazz)
}
