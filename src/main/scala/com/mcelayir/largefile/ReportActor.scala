package com.mcelayir.largefile

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object ReportActor {

  def apply(enrichedUsers: Map[Int, EnrichedProduct]): Behavior[ProtocolMessage] = Behaviors.receive {
    (_, message) =>
      message match {
        case ProductMessage(_) =>
          println("Not expecting UserMessage")
          Behaviors.ignore
        case Report(_) =>
          println(s"Print Ordered List")
          enrichedUsers.toSeq.sortBy(_._1).foreach(println)
          apply(enrichedUsers)
        case ErichedProductMessage(enrichedUser) =>
          apply(enrichedUsers + (enrichedUser.id -> enrichedUser))
      }

  }
}
