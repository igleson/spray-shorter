package api

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import core.UnshortenerActor._
import spray.http.StatusCodes
import spray.httpx.marshalling.{CollectingMarshallingContext, Marshaller}
import spray.json.{JsString, JsObject, JsValue, RootJsonFormat}
import spray.routing.Directives

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.reflect.ClassTag

class ShortenerService(unshortener: ActorRef)(implicit executionContext: ExecutionContext) extends Directives with
DefaultJsonFormats {

  implicit val timeout = Timeout(2.seconds)

  //  val shortRoute = path("short") {
  //    post {
  //      handleWith { short: Short =>
  //        (shortener ? short).mapTo[ReturnMessage]
  //      }
  //    }
  //  }

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
