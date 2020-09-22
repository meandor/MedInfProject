package com.github.meandor.doctorfate.auth.data

import java.util.UUID

import com.github.meandor.doctorfate.DatabaseIntegrationSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthenticationRepositorySpec extends DatabaseIntegrationSpec {
  val authenticationRepository = new AuthenticationRepository(global)

  Feature("findUserId") {
    Scenario("should return empty result when no user exists") { f =>
      val actual   = authenticationRepository.findUserId("foo@bar.com", "password")
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

      val actual   = authenticationRepository.findUserId(email, password)
      val expected = Future.successful(Option(userID))

      actual.futureValue shouldBe expected.futureValue
    }
  }
}
