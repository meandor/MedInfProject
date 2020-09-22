package com.github.meandor.doctorfate.auth.data
import java.time.LocalDateTime
import java.util.UUID

import com.github.meandor.doctorfate.DatabaseIntegrationSpec

import scala.concurrent.ExecutionContext.Implicits.global

class TokenRepositorySpec extends DatabaseIntegrationSpec {
  val tokenRepository = new TokenRepository(global)

  Feature("create") {
    Scenario("should create a token") { f =>
      val stmt     = f.dataSource.getConnection.createStatement
      val userID   = UUID.randomUUID()
      val email    = "foo@bar.com"
      val name     = "foo bar"
      val password = "password"
      val sql =
        s"INSERT INTO users (id, name, email, password) VALUES ('${userID.toString}', '$name', '$email', '$password')"
      stmt.executeUpdate(sql)

      val actualFuture = tokenRepository.create(userID)
      val actual       = actualFuture.futureValue

      actual.userID shouldBe userID
      actual.email shouldBe email
      actual.name shouldBe name
      actual.createdAt shouldBe a[LocalDateTime]
      actual.expiresAt shouldBe a[LocalDateTime]
    }
  }
}
