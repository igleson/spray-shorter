package core

import akka.actor.Actor
import akka.pattern.pipe
import com.datastax.driver.core.Row
import core.DBUtils.{asyncQuery, resultSetFutureToScala}
import core.UnshortenerActor._

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global

class UnshortenerActor extends Actor with ConfigCassandraCluster {

  override def receive: Receive = {
    case Unshort(url) if url == null || url.isEmpty => sender ! Left(NotUnshorted)
    case Unshort(url) => asyncQuery("select * from urls;") map { _.all().toList.head } map row2Unshorted pipeTo sender
  }

  implicit def row2Unshorted(r: Row) = Right(Unshorted(id = r.getString(0), longUrl = r.getString(1)))
}

object UnshortenerActor {

  case class Unshort(longUrl: String)

  case class Unshorted(id: String, longUrl: String, status: String = "OK")

  case class NotUnshorted(feedbackMessage: String = "URL cannot be empty", status: String = "BadRequest")

}