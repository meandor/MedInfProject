package com.github.meandor.auth.data
import java.util.UUID

import com.github.meandor.doctorfate.auth.data.AuthenticationRepository
import org.apache.commons.dbcp2.BasicDataSource
import org.flywaydb.core.Flyway
import org.scalatest.Outcome
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import scalikejdbc.{ConnectionPool, DataSourceConnectionPool}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthenticationRepositorySpec extends FixtureAnyFeatureSpec with Matchers with ScalaFutures {
  case class FixtureParam(dataSource: BasicDataSource) {
    val authenticationRepository = new AuthenticationRepository(global)
  }

  override def withFixture(test: OneArgTest): Outcome = {
    val dataSource: BasicDataSource = new BasicDataSource()
    dataSource.setUrl(
      "jdbc:postgresql://127.0.0.1:5432/postgres?user=postgres&password=mysecretpassword"
    )
    val flyway = Flyway.configure.dataSource(dataSource).load
    flyway.clean()
    flyway.migrate()
    ConnectionPool.add('default, new DataSourceConnectionPool(dataSource))
    withFixture(test.toNoArgTest(FixtureParam(dataSource)))
  }

  Feature("findUserId") {
    Scenario("should return empty result when no user exists") { f =>
      val actual   = f.authenticationRepository.findUserId("foo@bar.com", "password")
      val expected = Future.successful(None)

      actual.futureValue shouldBe expected.futureValue
    }

    Scenario("should return user when user exists") { f =>
      val stmt     = f.dataSource.getConnection.createStatement
      val userID   = UUID.randomUUID()
      val email    = "foo@bar.com"
      val password = "password"

      val sql =
        s"INSERT INTO users (id, name, email, password) VALUES ('${userID.toString}', 'foo bar', '${email}', '${password}')"
      stmt.executeUpdate(sql)
      val actual   = f.authenticationRepository.findUserId(email, password)
      val expected = Future.successful(Option(userID))

      actual.futureValue shouldBe expected.futureValue
    }
  }
}
