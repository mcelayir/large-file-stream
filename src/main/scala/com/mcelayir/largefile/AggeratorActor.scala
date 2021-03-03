package com.mcelayir.largefile

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

sealed trait ProtocolMessage
case class ProductMessage(user: Product) extends ProtocolMessage
case class Report(replyTo: ActorRef[ProtocolMessage]) extends ProtocolMessage
case class ErichedProductMessage(enrichedUser: EnrichedProduct) extends ProtocolMessage

class UserActor(id: Int) {

  def apply(countries: Map[String, Int]): Behavior[ProtocolMessage] = Behaviors.receive {
    (context, message) =>
      message match {
        case ProductMessage(user) =>
          context.log.info("Hello {}!", user.id)
          if(user.id != id) {
            context.log.error(s"Invalid Id: ${user.id}, actor id: $id")
            Behaviors.same
          }else{
            val newVal = if(countries.contains(user.country)){
              countries(user.country) + 1
            }else {
              1
            }
            apply(countries + (user.country -> newVal))
          }
        case Report(replyTo) =>
          replyTo ! ErichedProductMessage(EnrichedProduct(id, countries))
          Behaviors.same
        case ErichedProductMessage(enrichedUser) =>
          context.log.error(s"Not expecting ErichedUserMessage: ${enrichedUser.id}")
          Behaviors.ignore
      }

  }
}
