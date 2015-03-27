package core

import akka.actor.Actor
import core.ShortenerActor._

class ShortenerActor extends Actor {

  import core.ShortenerActor.Short

  override def receive: Receive = {
    case Short(url) if url == null || url.isEmpty => sender ! NotShorted("Invalid URL")
    case Short(url) => {
      println(s"shorted URL: $url")
      sender ! Shorted(id = s"shorted: $url", longUrl = url)
    }
    case Unshort(url) if url == null || url.isEmpty => sender ! NotUnshorted("Invalid URL")
    case Unshort(url) => {
      println(s"unshorted URL: $url")
      sender ! Unshorted(id = s"shorted: $url", longUrl = url)
    }
  }
}

object ShortenerActor {

  case class Short(longUrl: String)
  case class Unshort(longUrl: String)

  trait ReturnMessage

  case class Shorted(id: String, longUrl: String) extends ReturnMessage
  case class NotShorted(feedbackMessage: String) extends ReturnMessage
  case class Unshorted(id: String, longUrl: String) extends ReturnMessage
  case class NotUnshorted(feedbackMessage: String) extends ReturnMessage
}