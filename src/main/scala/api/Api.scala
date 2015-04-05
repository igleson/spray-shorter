package api

import akka.actor.Props
import core.{ConfigCassandraCluster, Core, CoreActors}
import spray.routing.RouteConcatenation

trait Api extends RouteConcatenation {
  this: Api with CoreActors with Core with ConfigCassandraCluster =>

  private implicit val _ = system.dispatcher

  lazy val unshortenerService = new UnshortenerService(unshortener)
  lazy val shortenerService = new ShortenerService(shortener)

  val routes = unshortenerService.unshortRoute ~ shortenerService.shortRoute

  val rootService = system.actorOf(Props(new RoutedHttpService(routes)))
}