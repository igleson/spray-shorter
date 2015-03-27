package api

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import core.ShortenerActor._
import spray.http.StatusCodes
import spray.httpx.marshalling.{CollectingMarshallingContext, Marshaller}
import spray.routing.Directives

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ShortenerService(shortener: ActorRef)(implicit executionContext: ExecutionContext) extends Directives with DefaultJsonFormats {

  implicit val timeout = Timeout(2.seconds)

  implicit val shortFormat = jsonFormat1(Short)
  implicit val unshortFormat = jsonFormat1(Unshort)

  implicit val shortedFormat = jsonObjectFormat[Shorted.type]
  implicit val notShortedFormat = jsonObjectFormat[NotShorted.type]
  implicit val unshortedFormat = jsonObjectFormat[Unshorted.type]
  implicit val notUnshortedFormat = jsonObjectFormat[NotUnshorted.type]


  implicit def returnMessageMarshaller(implicit shortedMar: Marshaller[Shorted], notShortedMar: Marshaller[NotShorted],
                                       unshortedMar: Marshaller[Unshorted], notUnshortedMar: Marshaller[NotUnshorted]) = {
    Marshaller[ReturnMessage] { (value, ctx) =>
      value match {
        case s: Shorted => shortedMar(s, ctx)
        case u: Unshorted => unshortedMar(u, ctx)
        case n: NotShorted => {
          val mc = new CollectingMarshallingContext()
          notShortedMar(n, mc)
          ctx.handleError(ErrorResponseException(StatusCodes.BadRequest, mc.entity))
        }
        case n: NotUnshorted => {
          val mc = new CollectingMarshallingContext()
          notUnshortedMar(n, mc)
          ctx.handleError(ErrorResponseException(StatusCodes.BadRequest, mc.entity))
        }
      }
    }
  }

  val shortRoute = path("short") {
    post {
      handleWith { short: Short =>
        (shortener ? short).mapTo[ReturnMessage]
      }
    }
  }

  def unshortRoute = path("unshort") {
    get {
      handleWith { unshort: Unshort =>
        (shortener ? unshort).mapTo[ReturnMessage]
      }
    }
  }
}
