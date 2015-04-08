package core

import akka.actor.Actor
import com.github.tototoshi.base62.Base62
import core.ShortenerActor.{NotShorted, Short, Shorted}
import DBUtils.asyncQuery
import scala.collection.JavaConversions._

class ShortenerActor extends Actor with ConfigCassandraCluster {

  override def receive: Receive = {
    case Short(longUrl) if longUrl == null || longUrl.isEmpty => sender ! Left(NotShorted)
    case Short(longUrl) => {
      val short: String = (Math.abs((new Base62).decode(longUrl)).toString.split("") map { _.toInt }).toList
      asyncQuery(s"INSERT INTO urls(id, longUrl) VALUES ('$short', '$longUrl');")
      sender ! Right(Shorted(id = "http://localhost:9000/"+short, longUrl = longUrl))
    }
  }

  implicit def intList2string(array: List[Int]): String = {
    array match {
      case Nil => ""
      case h1 :: h2 :: tail if ((h1, h2) + 65 < 91) => ((h1, h2) + 65).toChar + intList2string(tail)
      case h1 :: h2 :: tail if ((h1, h2) + 65 > 96 && (h1, h2) + 65 < 123) => ((h1, h2) + 65).toChar + intList2string(tail)
      case h1 :: tail => (h1 + 65).toChar + intList2string(tail)
    }
  }

  implicit def tuple2Num(tuple: (Int, Int)):Int = tuple._1 * 10 + tuple._2
}

object ShortenerActor {

  case class Short(longUrl: String)

  case class Shorted(id: String, longUrl: String)

  case class NotShorted(feedbackMessage: String = "URL cannot be empty", status: String = "BadRequest")

}