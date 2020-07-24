import java.time.Instant
import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

object KafkaDummyProducer extends LazyLogging {

  def sampleAppLogOne(inc: Int): String =
    s"""{"appName": "flinkKafkaDemo",
       | "dateTime": ${Instant.now().getEpochSecond},
       |  "logType": "info",
       |   "msg": "this is $inc demo log message"
       |   }""".stripMargin

  val msgToGenerate = 5

  def main(args: Array[String]): Unit = {
    writeToKafka(msgToGenerate)
  }

  def writeToKafka(msgNo :Int): Unit = {

    val props = new Properties()
    props.setProperty(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
      s"localhost:9092"
    )
    props.put(
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.ByteArraySerializer"
    )
    props.put(
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.ByteArraySerializer"
    )
    val producer = new KafkaProducer[Array[Byte], Array[Byte]](props)
    val key = "key".getBytes

    for (value <- 1 to msgNo) {
      Thread.sleep(1000)
      producer.send(
        new ProducerRecord[Array[Byte], Array[Byte]](
          "inputtopic",
          key,
          sampleAppLogOne(value).getBytes()
        )
      )
    }
    logger.info(s"successfully inserted $msgNo json formatted application logs in kafka")
    producer.close()
  }
}
