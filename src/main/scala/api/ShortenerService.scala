package api

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import core.ShortenerActor.{Short, NotShorted, Shorted}
import spray.routing.Directives

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ShortenerService(shortener: ActorRef)(implicit executionContext: ExecutionContext) extends Directives with
JsonFormatsHelpers {

  implicit val timeout = Timeout(2.seconds)

  val shortRoute = path("short") {
    post {
      handleWith { short: Short =>
        (shortener ? short).mapTo[Either[NotShorted, Shorted]]
      }
    }
  }
}
