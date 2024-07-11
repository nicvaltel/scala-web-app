package Log

import com.typesafe.scalalogging.*
import org.slf4j.LoggerFactory


case class CorrelationId(value: String)

implicit case object CanLogCorrelationId extends CanLog[CorrelationId] {
  override def logMessage(originalMsg: String, a: CorrelationId): String = s"${a.value} $originalMsg"
}


def runLog(): Unit =
  // // val logger = Logger("Testlogger")
  // val logger = Logger(LoggerFactory.getLogger("FromSlf4jLogger"))
  // logger.trace("This is a trace message")
  // logger.debug("This is a debug message")
  // logger.info("This is an info message")
  // logger.warn("This is a warning message")
  // logger.error("This is an error message")

  // logger.whenDebugEnabled {
  //   // This whole block is executed only if logger level is DEBUG
  //   // We can add additional calculation to be done during debugging
  //   logger.debug("additional information for debugging")
  // }



  implicit val correlationId = CorrelationId("user-ID-1234")

  val logger = Logger.takingImplicit[CorrelationId]("test")
  val message = "This is a message with user request context"
  logger.info(message)
  logger.info("Test") // takes implicit correlationId and logs "ID Test"