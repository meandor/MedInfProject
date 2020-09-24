package com.github.meandor.doctorfate.user.domain
import java.util.UUID

import com.github.meandor.doctorfate.UnitSpec
import com.github.meandor.doctorfate.user.data.{UserEntity, UserRepository}
import org.mockito.ArgumentMatchers.any
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserServiceSpec extends UnitSpec with ScalaFutures {
  Feature("registerUser") {
    val userRepositoryMock = mock[UserRepository]
    val service            = new UserService(userRepositoryMock)

    Scenario("should return None when user already exists") {
      val user = User("foo@bar.com", "password", None, hasVerifiedEmail = false)
      val existingUser = UserEntity(
        UUID.randomUUID(),
        user.email,
        user.password,
        user.name,
        emailIsVerified = false
      )
      userRepositoryMock.findByMail(any()) shouldReturn Future.successful(Some(existingUser))

      val actual   = service.registerUser(user)
      val expected = Future.successful(None)

      actual.futureValue shouldBe expected.futureValue
    }

    Scenario("should return created user when user did not exist yet") {
      val user = User("foo@bar.com", "password", None, hasVerifiedEmail = false)
      val createdUser = UserEntity(
        UUID.randomUUID(),
        user.email,
        user.password,
        user.name,
        emailIsVerified = false
      )
      userRepositoryMock.findByMail(any()) shouldReturn Future.successful(None)
      userRepositoryMock.create(any()) shouldReturn Future.successful(createdUser)

      val actual = service.registerUser(user)
      val expectedCreatedUser = User(
        createdUser.email,
        createdUser.password,
        createdUser.name,
        createdUser.emailIsVerified
      )
      val expected = Future.successful(Some(expectedCreatedUser))

      actual.futureValue shouldBe expected.futureValue
    }

    Scenario("should fail when finding user fails") {
      val user      = User("foo@bar.com", "password", None, hasVerifiedEmail = false)
      val exception = new Exception()
      userRepositoryMock.findByMail(any()) shouldReturn Future.failed(exception)

      val actual   = service.registerUser(user)
      val expected = Future.failed(exception)

      actual.failed.futureValue shouldBe expected.failed.futureValue
    }

    Scenario("should fail when registering user fails") {
      val user = User("foo@bar.com", "password", None, hasVerifiedEmail = false)
      userRepositoryMock.findByMail(any()) shouldReturn Future.successful(None)
      val exception = new Exception()
      userRepositoryMock.create(any()) shouldReturn Future.failed(exception)

      val actual   = service.registerUser(user)
      val expected = Future.failed(exception)

      actual.failed.futureValue shouldBe expected.failed.futureValue
    }
  }
}
