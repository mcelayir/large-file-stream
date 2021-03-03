package com.mcelayir.largefile

import java.nio.file.Paths

import akka.Done
import akka.actor.typed.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.alpakka.csv.scaladsl.CsvParsing
import akka.stream.scaladsl.{FileIO, Sink}
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.Future

class ProductProducer(topic: String,
                      fileName: String,
                      producerSink: Sink[ProducerRecord[String, Product], Future[Done]])
                     (implicit val system: ActorSystem[ProtocolMessage]) {

  implicit val ec = system.executionContext

  def start = FileIO.fromPath(Paths.get(fileName))
    .via(CsvParsing.lineScanner())
    .mapAsync(50)(line => Future(line.map(b => b.utf8String))
      .map(row => ProductFactory.fromCsvRow(row))
     )
    .collect{case Some(user) => user }
    .map(user => new ProducerRecord[String, Product](topic, user))
    .runWith(producerSink)
}

object ProductProducer {

  def create(topic: String, fileName: String, config: Config, bootstrapServers: String)(implicit system: ActorSystem[ProtocolMessage]) = {
    val producerSettings =
      ProducerSettings(config, new StringSerializer, new ProductSerializer)
        .withBootstrapServers(bootstrapServers)

    val sink = Producer.plainSink(producerSettings)

    new ProductProducer(topic, fileName, sink)
  }


}


