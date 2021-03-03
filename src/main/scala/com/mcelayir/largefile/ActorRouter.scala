package com.mcelayir.largefile

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

class ActorRouter(reportActor: ActorRef[ProtocolMessage]) {

  def apply(actors: Map[Int, ActorRef[ProtocolMessage]]): Behavior[ProtocolMessage] = Behaviors.receive {
    (context, message) =>
      message match {
        case ProductMessage(user) =>
          if (actors.contains(user.id)) {
            actors(user.id) ! ProductMessage(user)
            Behaviors.same
          } else {
            val userActor = context.spawn(new UserActor(user.id).apply(Map.empty), s"user-${user.id}")
            userActor ! ProductMessage(user)
            apply(actors + (user.id -> userActor))
          }
        case Report(_) =>
          if (actors.size < 100) {
            println(s"Dropping report message (NotEnoughActors)")
            Behaviors.same
          } else {
            println(s"Dispatching report message actors count: ${actors.size}")
            actors.map(a => a._2 ! Report(reportActor))
            Behaviors.same
          }
        case ErichedProductMessage(_) =>
          println("Not expecting ErichedUserMessage")
          Behaviors.ignore
      }

  }

}
