package com.mcelayir.largefile

import java.nio.file.Paths

import akka.actor.typed.{ActorRef, ActorSystem, DispatcherSelector}
import akka.stream.OverflowStrategy
import akka.stream.alpakka.csv.scaladsl.CsvParsing
import akka.stream.scaladsl.{FileIO, Sink}
import FileProducer._
import scala.concurrent.Future

class FileProducer(fileName: String, routerActor: ActorRef[ProtocolMessage])
                  (implicit val system: ActorSystem[ProtocolMessage]){

  implicit val ec = system.dispatchers.lookup(DispatcherSelector.fromConfig("my-dispatcher"))

  def start = FileIO.fromPath(Paths.get(fileName))
    .via(CsvParsing.lineScanner())
    .buffer(bufferSize, overflowStrategy)
    .mapAsync(parallelism)(line => Future(line.map(b => b.utf8String))
      .map(row => ProductFactory.fromCsvRow(row))
      .map(_.map(u =>routerActor ! ProductMessage(u)))
    )
    .runWith(Sink.ignore)
}

object FileProducer {
  val bufferSize = 10000
  val overflowStrategy = OverflowStrategy.backpressure
  val parallelism = 100
}
