package com.mcelayir.largefile

import akka.actor.typed.ActorSystem

import scala.concurrent.duration._

object Main extends App {

  implicit val system: ActorSystem[ProtocolMessage] = ActorSystem(SupervisorActor(), "hello")
  implicit val ec = system.executionContext
  val fileName =  "./largefile.csv"

  val solution = system.settings.config.getString("solution")

  val future = solution match {
    case "kafka" => kafka()
    case _ => streamReader()
  }

  future.onComplete(t => t.map{_ => system ! Report(system)})

  system.scheduler.scheduleAtFixedRate(10.seconds, 10.seconds)(() => {
    system ! Report(system)

  })


  def streamReader() = {
    println("Running stream solution")
    new FileProducer(fileName, system).start
  }

  def kafka() = {
    println("Running kafka solution")
    val producerConfig = system.settings.config.getConfig("akka.kafka.producer")
    val consumerConfig = system.settings.config.getConfig("akka.kafka.consumer")
    val bs = "localhost:19092"
    val producer = ProductProducer.create("largefile",
      fileName,
      producerConfig,
      bs
    )

    val consumer = ProductConsumer.create("largefile", consumerConfig, bs, system)


    producer.start
    consumer.start
  }
}
