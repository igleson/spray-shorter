package core

import com.datastax.driver.core.{ProtocolOptions, Cluster}

trait CassandraCluster {
  implicit def cluster: Cluster
}

trait ConfigCassandraCluster extends CassandraCluster with BootedCore{
  private def config = system.settings.config

  import scala.collection.JavaConversions._
  private val cassandraConfig = config.getConfig("shortener.db.cassandra")
  private val port = cassandraConfig.getInt("port")
  private val hosts = cassandraConfig.getStringList("hosts").toList

  implicit lazy val cluster: Cluster =
    Cluster.builder().
    addContactPoints(hosts: _*).
    withCompression(ProtocolOptions.Compression.SNAPPY).
    withPort(port).
    build()
}