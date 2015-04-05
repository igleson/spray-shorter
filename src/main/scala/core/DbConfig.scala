package core

import com.datastax.driver.core.{Cluster, ProtocolOptions}

import scala.collection.JavaConversions._

trait CassandraCluster {
  implicit def cluster: Cluster
}

trait ConfigCassandraCluster extends CassandraCluster with BootedCore {
  private def config = system.settings.config

  private val cassandraConfig = config.getConfig("shortener.db.cassandra")
  private val port = cassandraConfig.getInt("port")
  private val hosts: List[String] = cassandraConfig.getStringList("hosts").toList

  implicit val cluster = Cluster.builder().
                         addContactPoints(hosts: _*).
                         withCompression(ProtocolOptions.Compression.SNAPPY).
                         withPort(port).
                         build()
}