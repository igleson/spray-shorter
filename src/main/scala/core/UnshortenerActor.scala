package core

import akka.actor.Actor
import core.UnshortenerActor._

class UnshortenerActor extends Actor {

  override def receive: Receive = {
    case Unshort(url) if url == null || url.isEmpty => sender ! Left(NotUnshorted("Invalid URL"))
    case Unshort(url) => sender ! Right(Unshorted(id = s"shorted: $url", longUrl = url))
  }
}

object UnshortenerActor {

  case class Unshort(longUrl: String)

  case class Unshorted(id: String, longUrl: String, status: String = "OK")

  case class NotUnshorted(feedbackMessage: String, status: String = "BadRequest")

}