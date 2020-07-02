/*
 * © Copyright 2020
 * Author: Piyush Rana
 * Email: piyush@knoldus.com
 */

import com.typesafe.scalalogging.LazyLogging
import connector.{KafkaConsumer, KafkaProducer}
import model.{ApplicationLogs, JobConfig}
import org.apache.flink.streaming.api.scala.{DataStream, _}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import pureconfig.error.ConfigReaderFailures
import pureconfig.ConfigSource
import runners.{FlinkRunner, backend}
import serde.{KafkaSimpleDeserializer, KafkaSimpleSerializer}
import pureconfig.generic.auto._
import io.circe.syntax._
import scala.util.{Failure, Success, Try}

object Job extends LazyLogging {

  private def loadConfigOrFail(path: String): JobConfig =
    ConfigSource.resources(path).load[JobConfig] match {
      case Left(exception: ConfigReaderFailures) =>
        throw new RuntimeException(s"Unable to load the config from path $path, original error: ${exception.prettyPrint()}")
      case Right(configuration) => configuration
    }

  private def createEnvOrFail(config: JobConfig, jobName: String): StreamExecutionEnvironment =
    FlinkRunner(config.task, c => backend(c)) match {
      case Right(e) => e
      case Left(error) =>
        throw new RuntimeException(
          s"Unable to init exec environment. Job=$jobName. Failed with errors ${error.getMessage}",
          error
        )
    }

  private def createConsumerOrFail(config: JobConfig): FlinkKafkaConsumer[Either[Array[Byte], ApplicationLogs]] =
    KafkaConsumer(
      config.input,
      config.kafka,
      () =>
        new KafkaSimpleDeserializer[Either[Array[Byte], ApplicationLogs]](
          parser = MessageParser.eventParser(
            toAppLogs = MessageParser.toEvent,
            toDecodedString = MessageParser.parseTsv
          ),
          classOf[Either[Array[Byte], ApplicationLogs]]
        )
    ) match {
      case Right(c) => c
      case Left(_)  => throw new RuntimeException("Unable to init Kafka Consumer!")
    }

  private def createStringProducerOrFail(config: JobConfig, jobName: String): FlinkKafkaProducer[String] =
    KafkaProducer(
      config.output,
      config.kafka,
      topicNameStrategy = config.output.topic.getOrElse(""),
      topic =>
        new KafkaSimpleSerializer[String](
          topic,
          tsv => tsv.getBytes("UTF-8")
        )
    ) match {
      case Right(eventProducer) => eventProducer
      case Left(exception) =>
        throw new RuntimeException(
          s"Job $jobName failed. Unable to connect to kafka. Original error ${exception.getMessage}",
          exception
        )
    }

  def main(args: Array[String]) {
    val config = loadConfigOrFail("application.conf")
    val jobName = "tsv2json"
    val env = createEnvOrFail(config, jobName)

    logger.info(
      "Execution environment was configured using TaskConfig={}. Job={}, {}",
      config.task,
      jobName,
      env
    )

    lazy val inputStream: DataStream[Either[Array[Byte], ApplicationLogs]] = env.addSource {
      createConsumerOrFail(config)
    }

    inputStream
      .map(events => events.toOption.asJson.noSpaces)
      .addSink {
        createStringProducerOrFail(config, jobName)
      }

    logger.info(
      "Execution environment was configured Successfully ==>>> using JobConfig={}. Job={}",
      config,
      jobName
    )

    if(config.logExecutionPlan.getOrElse(false)) {
      logger.info("ExecutionPlan={}", env.getExecutionPlan)
    }
    Try(env.execute(jobName)) match {
      case Success(s) => logger.info("Job {} finished successfully in {} ms", jobName, s.getNetRuntime)
      case Failure(e) =>
        logger.error("Execution Failed. Error={}", e.getMessage(), e)
        new RuntimeException(s"Job $jobName failed with errors ${e.getMessage}", e)
    }
  }
}