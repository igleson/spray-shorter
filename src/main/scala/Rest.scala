import api.Api
import core.{ConfigCassandraCluster, BootedCore, CoreActors}
import web.Web

object Rest extends App with BootedCore with CoreActors with Api with Web with ConfigCassandraCluster