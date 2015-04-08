package api

import core.UnshortenerActor.{NotUnshorted, Unshorted, Unshort}
import core.ShortenerActor.{NotShorted, Shorted, Short}
import spray.http.{StatusCode, StatusCodes}
import spray.httpx.SprayJsonSupport
import spray.httpx.marshalling.{CollectingMarshallingContext, Marshaller, MetaMarshallers}
import spray.json._
import core.ShortenerActor.NotShorted


/**
 * Contains useful JSON formats: ``j.u.Date``, ``j.u.UUID`` and others; it is useful
 * when creating traits that contain the ``JsonReader`` and ``JsonWriter`` instances
 * for types that contain ``Date``s, ``UUID``s and such like.
 */
trait JsonFormatsHelpers extends DefaultJsonProtocol with SprayJsonSupport with MetaMarshallers {

  implicit def string2JsString(str: String) = JsString(str)

  implicit val unshortedFormat = new RootJsonFormat[Unshorted] {
    def write(unshorted: Unshorted) = JsObject("id" -> unshorted.id,
                                                "longUrl" -> unshorted.longUrl,
                                                "status" -> unshorted.status)

    def read(json: JsValue) = null
  }
  implicit val notUnshortedFormat = new RootJsonFormat[NotUnshorted] {
    def write(notUnshorted: NotUnshorted) = JsObject("feedbackMessage" -> notUnshorted.feedbackMessage,
                                                      "status" -> notUnshorted.status)

    def read(json: JsValue) = null
  }

  implicit val shortFormat = jsonFormat1(Short)
  implicit val shortedFormat = new RootJsonFormat[Shorted] {
    def write(shorted: Shorted) = JsObject("id" -> shorted.id,
                                            "longUrl" -> shorted.longUrl)

    def read(json: JsValue) = null
  }
  implicit val notShortedFormat = new RootJsonFormat[NotShorted] {
    def write(notShorted: NotShorted) = JsObject("feedbackMessage" -> notShorted.feedbackMessage,
                                                  "status" -> notShorted.status)

    def read(json: JsValue) = null
  }

  implicit def errorSelectingEitherMarshaller[A, B](implicit ma: Marshaller[A], mb: Marshaller[B]) =
    Marshaller[Either[A, B]] {
      (value, ctx) => value match {
        case Left(a) => {
          val mc = new CollectingMarshallingContext()
          ma(a, mc)

          ctx.handleError(ErrorResponseException(a, mc.entity))
        }
        case Right(b) => mb(b, ctx)
      }
    }

  implicit def any2StatusCode(obj: Any) : StatusCode = obj match {
    case n : NotShorted if n.status equals "BadRequest" => StatusCodes.BadRequest
    case n : NotShorted if n.status equals "Not Found" => StatusCodes.NotFound
    case n : NotUnshorted if n.status equals "BadRequest" => StatusCodes.BadRequest
    case n : NotUnshorted if n.status equals "Not Found" => StatusCodes.NotFound
  }
}