package com.github.meandor.doctorfate
import org.apache.commons.dbcp2.BasicDataSource
import org.flywaydb.core.Flyway
import org.scalatest.Outcome
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import scalikejdbc.{ConnectionPool, DataSourceConnectionPool}

trait DatabaseIntegrationSpec extends FixtureAnyFeatureSpec with Matchers with ScalaFutures {
  case class FixtureParam(dataSource: BasicDataSource)

  override def withFixture(test: OneArgTest): Outcome = {
    val dataSource: BasicDataSource = new BasicDataSource()
    dataSource.setUrl(
      "jdbc:postgresql://127.0.0.1:5432/postgres?user=postgres&password=mysecretpassword"
    )
    val flyway = Flyway.configure.dataSource(dataSource).load
    flyway.migrate()
    ConnectionPool.add('default, new DataSourceConnectionPool(dataSource))
    try {
      withFixture(test.toNoArgTest(FixtureParam(dataSource)))
    } finally {
      flyway.clean()
    }
  }
}
