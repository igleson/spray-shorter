package core

import akka.actor.Actor
import core.ShortenerActor.{NotShorted, Short, Shorted}
import DBUtils.asyncQuery

class ShortenerActor extends Actor with ConfigCassandraCluster {

  var next = 0

  override def receive: Receive = {
    case Short(longUrl) if longUrl == null || longUrl.isEmpty => sender ! Left(NotShorted)
    case Short(longUrl) => {
      asyncQuery(s"INSERT INTO urls(id, longUrl) VALUES ('<shortUrl$next>', '$longUrl');")
      next += 1
      sender ! Right(Shorted(id = "<SHORTED URL>", longUrl = longUrl))
    }
  }
}

object ShortenerActor {

  case class Short(longUrl: String)

  case class Shorted(id: String, longUrl: String)

  case class NotShorted(feedbackMessage: String = "URL cannot be empty", status: String = "BadRequest")

}