package core

import akka.actor.{Props, ActorSystem}


trait Core {
  protected implicit def system: ActorSystem
}

trait CoreActors {
  this: Core =>

  val unshortener = system.actorOf(Props[UnshortenerActor])
}

trait BootedCore extends Core {
  implicit lazy val system = ActorSystem("akka-spray-shortener")

  sys.addShutdownHook {system.shutdown()}
}