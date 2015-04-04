package core

import akka.actor.Actor
import core.UnshortenerActor._

class UnshortenerActor extends Actor {

  override def receive: Receive = {
    case Unshort(url) if url == null || url.isEmpty => sender ! Left(NotUnshorted)
    case Unshort(url) => sender ! Right(Unshorted(id = url, longUrl = "<UNSHORTED URL>"))
  }
}

object UnshortenerActor {

  case class Unshort(longUrl: String)

  case class Unshorted(id: String, longUrl: String, status: String = "OK")

  case class NotUnshorted(feedbackMessage: String = "URL cannot be empty", status: String = "BadRequest")

}