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

  implicit val timeout = Timeout(10 seconds)

  def unshortRoute = path("url") {
    get {
      parameter("shortUrl".as[String]) { shortUrl =>
        complete {
          val host = "http://localhost:9000/"
          val url = shortUrl.substring(host.length, shortUrl.length)
          (unshortener ? Unshort(url)).mapTo[Either[NotUnshorted, Unshorted]]
        }
      }
    }
  }
}
