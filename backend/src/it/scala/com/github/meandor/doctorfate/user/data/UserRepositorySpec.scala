package com.github.meandor.doctorfate.user.data
import java.util.UUID

import com.github.meandor.doctorfate.DatabaseIntegrationSpec
import org.postgresql.util.PSQLException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserRepositorySpec extends DatabaseIntegrationSpec {
  val userRepository = new UserRepository(global)

  Feature("confirm") {
    Scenario("should return empty result when no user exists") { _ =>
      val actual   = userRepository.confirm("foo@bar.com")
      val expected = Future.successful(None)

      actual.futureValue shouldBe expected.futureValue
    }

    Scenario("should return empty result when user already confirmed") { f =>
      val userID   = UUID.randomUUID()
      val email    = "foo@bar.com"
      val password = "password"
      val stmt     = f.dataSource.getConnection.createStatement
      val sql =
        s"INSERT INTO users (id, name, email, password, email_is_verified) VALUES ('${userID.toString}', 'foo bar', '$email', '$password', true)"
      stmt.executeUpdate(sql)

      val actual   = userRepository.confirm(email)
      val expected = Future.successful(None)

      actual.futureValue shouldBe expected.futureValue
    }

    Scenario("should return user when user exists and not confirmed") { f =>
      val userID   = UUID.randomUUID()
      val email    = "foo@bar.com"
      val password = "password"
      val stmt     = f.dataSource.getConnection.createStatement
      val sql =
        s"INSERT INTO users (id, name, email, password) VALUES ('${userID.toString}', 'foo bar', '$email', '$password')"
      stmt.executeUpdate(sql)

      val actual   = userRepository.confirm(email)
      val entity   = UserEntity(userID, email, password, Some("foo bar"), emailIsVerified = true)
      val expected = Future.successful(Option(entity))

      actual.futureValue shouldBe expected.futureValue
    }
  }

  Feature("findByMail") {
    Scenario("should return empty result when no user exists") { _ =>
      val actual   = userRepository.findByMail("foo@bar.com")
      val expected = Future.successful(None)

      actual.futureValue shouldBe expected.futureValue
    }

    Scenario("should return user when user exists") { f =>
      val userID   = UUID.randomUUID()
      val email    = "foo@bar.com"
      val password = "password"
      val stmt     = f.dataSource.getConnection.createStatement
      val sql =
        s"INSERT INTO users (id, name, email, password) VALUES ('${userID.toString}', 'foo bar', '$email', '$password')"
      stmt.executeUpdate(sql)

      val actual   = userRepository.findByMail(email)
      val entity   = UserEntity(userID, email, password, Some("foo bar"), emailIsVerified = false)
      val expected = Future.successful(Option(entity))

      actual.futureValue shouldBe expected.futureValue
    }
  }

  Feature("create") {
    Scenario("should create and return created user") { _ =>
      val toBeInsertedEntity = UserEntity(
        UUID.randomUUID(),
        "foo@bar.com",
        "password",
        Some("foo bar"),
        emailIsVerified = true
      )

      val actual   = userRepository.create(toBeInsertedEntity)
      val expected = Future.successful(toBeInsertedEntity)

      actual.futureValue shouldBe expected.futureValue
    }

    Scenario("should return failed future if email already exists") { f =>
      val userID   = UUID.randomUUID()
      val email    = "foo@bar.com"
      val password = "password"
      val toBeInsertedEntity = UserEntity(
        userID,
        email,
        password,
        Some("bar bar"),
        emailIsVerified = true
      )
      val stmt = f.dataSource.getConnection.createStatement
      val sql =
        s"INSERT INTO users (id, name, email, password) VALUES ('${userID.toString}', 'foo bar', '$email', '$password')"
      stmt.executeUpdate(sql)

      val actual = userRepository.create(toBeInsertedEntity)

      actual.failed.futureValue shouldBe a[PSQLException]
    }
  }
}
