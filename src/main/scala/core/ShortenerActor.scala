package core

import akka.actor.Actor
import core.ShortenerActor.{Shorted, NotShorted, Short}
import core.UnshortenerActor._

class ShortenerActor extends Actor {

  override def receive: Receive = {
    case Short(longUrl) if longUrl == null || longUrl.isEmpty => sender ! Left(NotShorted)
    case Short(longUrl) => sender ! Right(Shorted(id = "<SHORTED URL>", longUrl = longUrl))
  }
}

object ShortenerActor {

  case class Short(longUrl: String)

  case class Shorted(id: String, longUrl: String)

  case class NotShorted(feedbackMessage: String = "URL cannot be empty", status: String = "BadRequest")

}