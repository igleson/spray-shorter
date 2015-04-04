package api

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import core.UnshortenerActor._
import spray.http.StatusCodes
import spray.httpx.marshalling.{CollectingMarshallingContext, Marshaller}
import spray.routing.Directives

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ShortenerService(unshortener: ActorRef)(implicit executionContext: ExecutionContext) extends Directives with DefaultJsonFormats {

  implicit val timeout = Timeout(2.seconds)

  implicit val unshortFormat = jsonFormat1(Unshort)

  implicit val unshortedFormat = jsonObjectFormat[Unshorted.type]
  implicit val notUnshortedFormat = jsonObjectFormat[NotUnshorted.type]


  implicit def errorSelectingEitherMarshaller[A, B](implicit ma: Marshaller[A], mb: Marshaller[B]) =
    Marshaller[Either[A, B]] {
      (value, ctx) => value match {
        case Left(a) => {
          val mc = new CollectingMarshallingContext()
          ma(a, mc)
          ctx.handleError(ErrorResponseException(StatusCodes.BadRequest, mc.entity))
        }
        case Right(b) => mb(b, ctx)
      }
    }


  //  val shortRoute = path("short") {
//    post {
//      handleWith { short: Short =>
//        (shortener ? short).mapTo[ReturnMessage]
//      }
//    }
//  }

  def unshortRoute = path("unshort") {
    get {
      parameter("shortUrl".as[String]){ shortUrl =>
        complete{
          (unshortener ? Unshort(shortUrl)).mapTo[Either[NotUnshorted.type, Unshorted.type]]
        }
      }
    }
  }
}
