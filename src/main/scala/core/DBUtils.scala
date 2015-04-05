package core

import com.datastax.driver.core.{Cluster, ResultSet, ResultSetFuture}
import com.google.common.util.concurrent.{FutureCallback, Futures}

import scala.concurrent.{Future, Promise}

object DBUtils {
  implicit def resultSetFutureToScala(f: ResultSetFuture): Future[ResultSet] = {
    val p = Promise[ResultSet]()
    Futures.addCallback(f, new FutureCallback[ResultSet] {
      def onSuccess(r: ResultSet) = p success r

      def onFailure(t: Throwable) = p failure t
    })
    p.future
  }

  def asyncQuery(query: String)(implicit cluster: Cluster) = {
    val session = cluster connect "shorter"
    val exe = session executeAsync query
    session.closeAsync
    exe
  }
}
