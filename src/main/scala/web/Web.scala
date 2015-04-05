package web

import akka.io.IO
import api.Api
import core.{ConfigCassandraCluster, CoreActors, Core}
import spray.can.Http

trait Web {
  this: Api with CoreActors with Core with ConfigCassandraCluster =>

  IO(Http)(system) ! Http.Bind(rootService, "0.0.0.0", port = 9000)
}