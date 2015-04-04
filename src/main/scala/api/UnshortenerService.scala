package api

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import core.UnshortenerActor._
import spray.routing.Directives

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class UnshortenerService(unshortener: ActorRef)(implicit executionContext: ExecutionContext) extends Directives with
JsonFormatsHelpers {

  implicit val timeout = Timeout(2.seconds)

  def unshortRoute = path("unshort") {
    get {
      parameter("shortUrl".as[String]) { shortUrl =>
        complete {
          (unshortener ? Unshort(shortUrl)).mapTo[Either[NotUnshorted, Unshorted]]
        }
      }
    }
  }
}
