package com.mcelayir.largefile

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._

object SupervisorActor {
  def apply(): Behavior[ProtocolMessage] =
    Behaviors.setup { context =>
      val reporter = context.spawn(ReportActor(Map.empty), "report")
      val router = context.spawn(new ActorRouter(reporter).apply(Map.empty), "router")

      Behaviors.receiveMessage { message =>
        message match {
          case a: ProductMessage =>
            router ! a
            Behaviors.same
          case _: Report =>
            router ! Report(context.self)
            context.scheduleOnce(20.seconds, reporter, Report(context.self))
            Behaviors.same
          case _: ErichedProductMessage =>
            println("Ignore Eriched User Message")
            Behaviors.ignore
        }
      }
    }
}
