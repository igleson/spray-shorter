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
    case Unshort(url) => asyncQuery(s"select * from urls where id = '$url';") map { _.all().toList } map {
        case Nil => Left(NotUnshorted(feedbackMessage="URL never shorted", status="Not Found"))
        case l:List[Row] => row2Unshorted(l.head)
      } pipeTo sender
  }

  def row2Unshorted(r: Row) = Right(Unshorted(id = "http://localhost:9000/"+r.getString(0), longUrl = r.getString(1)))
}

object UnshortenerActor {

  case class Unshort(longUrl: String)

  case class Unshorted(var id: String, longUrl: String, status: String = "OK")

  case class NotUnshorted(feedbackMessage: String = "URL cannot be empty", status: String = "BadRequest")

}