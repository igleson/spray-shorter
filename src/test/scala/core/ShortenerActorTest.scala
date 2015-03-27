package core

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.specs2.mutable.SpecificationLike
import core.ShortenerActor._

/**
 * Created by igleson on 26/03/15.
 */
class ShortenerActorTest extends TestKit(ActorSystem()) with SpecificationLike with CoreActors with Core
with ImplicitSender {
  sequential

  "Shortener should" >> {

    "reject null url on short" in {
      shortener ! Short(null)
      expectMsg(NotShorted("Invalid URL"))
      success
    }

    "reject empty url on short" in {
      shortener ! Short("")
      expectMsg(NotShorted("Invalid URL"))
      success
    }

    "accept valid url to short" in {
      shortener ! Short("www.google.com")
      expectMsg(Shorted(id = "shorted: www.google.com", longUrl = "www.google.com"))
      success
    }

    "reject null url on unshort" in {
      shortener ! Unshort(null)
      expectMsg(NotUnshorted("Invalid URL"))
      success
    }

    "reject empty url on unshort" in {
      shortener ! Unshort("")
      expectMsg(NotUnshorted("Invalid URL"))
      success
    }

    "accept valid url to unshort" in {
      shortener ! Unshort("www.google.com")
      expectMsg(Unshorted(id = "shorted: www.google.com", longUrl = "www.google.com"))
      success
    }
  }

}