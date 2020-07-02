/*
 * © Copyright 2020
 * Author: Piyush Rana
 * Email: piyush@knoldus.com
 */

import com.typesafe.scalalogging.LazyLogging
import io.circe.parser
import model.ApplicationLogs

import scala.util.{Failure, Success, Try}

object MessageParser extends LazyLogging {
  def eventParser(
                   toAppLogs: (String) => Either[Throwable, ApplicationLogs],
                   toDecodedString: Array[Byte] => Either[Throwable, String]
  )(record: Array[Byte]): Either[Array[Byte], ApplicationLogs] =
    toDecodedString(record) match {
      case Right(decodedKafkaMsg) =>
        toAppLogs(decodedKafkaMsg) match {
          case Right(v) => Right(v)
          case Left(_)  => Left(record)
        }
      case Left(_) => Left(record)
    }

  def toEvent(appJsonMessage: String): Either[Throwable, ApplicationLogs] =
    parser.decode[ApplicationLogs](appJsonMessage)

  def parseTsv(bytes: Array[Byte]): Either[Throwable, String] =
    Try(new String(bytes, "UTF-8")) match {
      case Success(decodedString)        => Right(decodedString)
      case Failure(exception: Throwable) => Left(exception)
    }
}
