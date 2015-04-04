package api

import core.UnshortenerActor.{NotUnshorted, Unshorted, Unshort}
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport
import spray.httpx.marshalling.{CollectingMarshallingContext, Marshaller, MetaMarshallers}
import spray.json._


/**
 * Contains useful JSON formats: ``j.u.Date``, ``j.u.UUID`` and others; it is useful
 * when creating traits that contain the ``JsonReader`` and ``JsonWriter`` instances
 * for types that contain ``Date``s, ``UUID``s and such like.
 */
trait DefaultJsonFormats extends DefaultJsonProtocol with SprayJsonSupport with MetaMarshallers {

  implicit def string2JsString(str: String) = JsString(str)

  implicit val unshortFormat = jsonFormat1(Unshort)

  implicit val unshortedFormat = new RootJsonFormat[Unshorted] {
    def write(unshorted: Unshorted): JsValue = JsObject("id" -> unshorted.id,
                                                         "longUrl" -> unshorted.longUrl,
                                                         "status" -> unshorted.status)

    def read(json: JsValue) = null
  }
  implicit val notUnshortedFormat = new RootJsonFormat[NotUnshorted] {
    def write(notUnshorted: NotUnshorted): JsValue = JsObject("feedbackMessage" -> notUnshorted.feedbackMessage,
                                                               "status" -> notUnshorted.status)

    def read(json: JsValue) = null
  }


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
}