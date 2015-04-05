package core

import akka.actor.Actor
import akka.pattern.pipe
import com.datastax.driver.core.{ResultSet, ResultSetFuture, Row}
import com.google.common.util.concurrent.{FutureCallback, Futures}
import core.UnshortenerActor._

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future, Promise}

class UnshortenerActor extends Actor with ConfigCassandraCluster{

  implicit def resultSetFutureToScala(f: ResultSetFuture): Future[ResultSet] = {
    val p = Promise[ResultSet]()
    Futures.addCallback(f,
                         new FutureCallback[ResultSet] {
                           def onSuccess(r: ResultSet) = p success r
                           def onFailure(t: Throwable) = p failure t
                         })
    p.future
  }

  override def receive: Receive = {
    case Unshort(url) if url == null || url.isEmpty => sender ! Left(NotUnshorted)
    case Unshort(url) => {
      val session = cluster.connect("shorter")
      val urls = session.executeAsync("select * from urls;")
      urls map {_.all().toList.head} map row2RightUnshorted pipeTo sender
    }
  }

  implicit def row2RightUnshorted(r: Row) = Right(Unshorted(id = r.getString(0), longUrl = r.getString(1)))
}

object UnshortenerActor {

  case class Unshort(longUrl: String)

  case class Unshorted(id: String, longUrl: String, status: String = "OK")

  case class NotUnshorted(feedbackMessage: String = "URL cannot be empty", status: String = "BadRequest")

}