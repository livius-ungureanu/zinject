package example

import zio._
import zio.stream.ZStream.apply
import zio.stream.ZStream
import zio.stream.ZSink
import zio.Console
import zio.stream.ZPipeline

import java.lang.Exception
import zio.stream.ZStream

import java.nio.file.Path
import java.io.File
import java.io.FilenameFilter
import java.nio.file.OpenOption
import java.nio.file.StandardOpenOption
import zio._
import zio.http._
import zio.http.Status.InternalServerError
import zio.json._
import zio.http.Status
import example.SubscriberCsv

object ZInject extends ZIOAppDefault {

  private val HOST_IP = "127.0.0.1"
  private val HOST_PORT = 8071
  private val PARALLELISM = 4
  private val N = 100000

  private def genSubscribers(nbSubscribers: Int) = (1 to nbSubscribers)
    .map { i =>
      s"${258243400000L + i},${258243400000L + i},${1000 + i}"
    }
    .map(line => toSubscriber(line))

  private def toSubscriber(line: String) = {
    val parts = line.split(",")
    SubscriberCsv(parts(0), parts(1), parts(2).toInt)
  }

  private def createSubscriber(
      subscriber: SubscriberCsv
  ): ZIO[Client, Throwable, Response] = {
    val url =
      s"http://${HOST_IP}:${HOST_PORT}/cpg/v1/provisioning/v2/subscribers"

    val reqBody =
      s"""{
         |  "MSISDN": "${subscriber.msidn}",
         |  "IMSI": "${subscriber.imsi}",
         |}
         |""".stripMargin

    for {
      res <- Client.request(
        url,
        method = Method.POST,
        content = Body.fromString(reqBody)
      )

      _ <- res.body.asString.tap(strResp => ZIO.logDebug(strResp))
    } yield res
  }

  val program = {

    for {
      args <- ZIOAppArgs.getArgs
      _ <- ZIO.logInfo(s"args: ${args.mkString(",")}")

      _ <- ZIO
        .foreachPar(genSubscribers(nbSubscribers = args(0).toInt)) {
          subscriberCsv =>
            createSubscriber(subscriberCsv)
        }
        .withParallelism(args(1).toInt)
        .map(responses => responses.length)
        .tap(responsesCount => ZIO.logInfo(s"Done ${responsesCount}"))
    } yield ()
  }

  def run = program.provideSomeLayer[ZIOAppArgs](Client.default.map { env =>
    ZEnvironment(env.get.withDisabledStreaming)
  })
}
