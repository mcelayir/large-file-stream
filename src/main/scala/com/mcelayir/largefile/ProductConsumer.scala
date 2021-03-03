package com.mcelayir.largefile

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer

class ProductConsumer(topic: String,
                      routerActor: ActorRef[ProtocolMessage],
                      consumerSource: Source[ConsumerRecord[String, Product], Consumer.Control])
                     (implicit val system: ActorSystem[ProtocolMessage]) {

  def start = consumerSource
    .map(a => a.value())
    .map { u =>
      routerActor ! ProductMessage(u)
      u
    }
    .runWith(Sink.ignore)

}

object ProductConsumer {
  def create(topic: String, config: Config, bootstrapServers: String, routerActor: ActorRef[ProtocolMessage])(implicit system: ActorSystem[ProtocolMessage]): ProductConsumer = {

    val consumerSettings =
      ConsumerSettings(config, new StringDeserializer, new ProductDeserializer)
        .withBootstrapServers(bootstrapServers)
        .withGroupId("group1")

    val cons = Consumer.plainSource(consumerSettings, Subscriptions.topics(topic))
    new ProductConsumer(topic, routerActor, cons)
  }
}

