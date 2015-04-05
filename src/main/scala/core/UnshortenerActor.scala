package core

import akka.actor.Actor
import core.UnshortenerActor._
import scala.collection.JavaConversions._

class UnshortenerActor extends Actor with ConfigCassandraCluster{

  override def receive: Receive = {
    case Unshort(url) if url == null || url.isEmpty => sender ! Left(NotUnshorted)
    case Unshort(url) => {
      val session = cluster.connect("shorter")
      val urls = session.execute("select * from urls;")
      val row1 = urls.all().toList.head
      session.close()
      sender ! Right(Unshorted(id = row1.getString(0), longUrl = row1.getString(1)))
    }
  }
}

object UnshortenerActor {

  case class Unshort(longUrl: String)

  case class Unshorted(id: String, longUrl: String, status: String = "OK")

  case class NotUnshorted(feedbackMessage: String = "URL cannot be empty", status: String = "BadRequest")

}