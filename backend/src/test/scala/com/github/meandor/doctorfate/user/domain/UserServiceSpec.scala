package com.github.meandor.doctorfate.user.domain
import java.util.UUID
import akka.Done
import com.github.meandor.doctorfate.UnitSpec
import com.github.meandor.doctorfate.menstruation.data.MenstruationEntity
import com.github.meandor.doctorfate.menstruation.domain.{Menstruation, MenstruationService}
import com.github.meandor.doctorfate.user.data.{MailClient, UserEntity, UserRepository}
import org.mockito.ArgumentMatchers.any
import org.scalatest.concurrent.ScalaFutures

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserServiceSpec extends UnitSpec with ScalaFutures {
  Feature("registerUser") {
    val userRepositoryMock      = mock[UserRepository]
    val mailClientMock          = mock[MailClient]
    val menstruationServiceMock = mock[MenstruationService]
    val service = new UserService(
      userRepositoryMock,
      mailClientMock,
      "salt",
      "http://",
      menstruationServiceMock
    )

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
      mailClientMock.sendConfirmationMail(any(), any()) shouldReturn Future.successful(Done)

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

  Feature("confirm") {
    val userRepositoryMock      = mock[UserRepository]
    val mailClientMock          = mock[MailClient]
    val menstruationServiceMock = mock[MenstruationService]
    val service = new UserService(
      userRepositoryMock,
      mailClientMock,
      "salt",
      "http://",
      menstruationServiceMock
    )
    val id = "KnToMxPEeG1Qubxm1f50fg=="

    Scenario("should successfully confirm user based on given id") {
      val email = "foo@bar.com"
      val confirmedUserEntity = UserEntity(
        UUID.randomUUID(),
        email,
        "password",
        None,
        emailIsVerified = true
      )

      userRepositoryMock.confirm(email) shouldReturn Future.successful(Some(confirmedUserEntity))

      val actual = service.confirm(id)
      val expectedConfirmedUser = User(
        confirmedUserEntity.email,
        confirmedUserEntity.password,
        confirmedUserEntity.name,
        confirmedUserEntity.emailIsVerified
      )
      val expected = Future.successful(Some(expectedConfirmedUser))

      actual.futureValue shouldBe expected.futureValue
    }

    Scenario("should return none when user already confirmed") {
      userRepositoryMock.confirm(any()) shouldReturn Future.successful(None)

      val actual   = service.confirm(id)
      val expected = Future.successful(None)

      actual.futureValue shouldBe expected.futureValue
    }

    Scenario("should return failure when something goes wrong") {
      val exception = new Exception("expected")
      userRepositoryMock.confirm(any()) shouldReturn Future.failed(exception)

      val actual   = service.confirm(id)
      val expected = Future.failed(exception)

      actual.failed.futureValue shouldBe expected.failed.futureValue
    }
  }

  Feature("anonymize") {
    val userRepositoryMock      = mock[UserRepository]
    val mailClientMock          = mock[MailClient]
    val menstruationServiceMock = mock[MenstruationService]
    val service = new UserService(
      userRepositoryMock,
      mailClientMock,
      "salt",
      "http://",
      menstruationServiceMock
    )

    Scenario("should return done when changing all existing period events for user") {
      val userId = UUID.randomUUID()
      val userEntity = UserEntity(
        userId,
        "email",
        "secret",
        Some("name"),
        emailIsVerified = true
      )
      userRepositoryMock.find(userId) shouldReturn Future.successful(Some(userEntity))
      menstruationServiceMock.changeOwner(userId) shouldReturn Future.successful(Done)

      val actual   = service.anonymize(userId)
      val expected = Done

      actual.futureValue shouldBe expected
    }

    Scenario("should return fail when user does not exist") {
      val userId = UUID.randomUUID()
      userRepositoryMock.find(userId) shouldReturn Future.successful(None)

      val actual = service.anonymize(userId)

      actual.failed.futureValue shouldBe a[IllegalArgumentException]
    }

    Scenario("should return fail when changing all existing period events for user fails") {
      val userId = UUID.randomUUID()
      val userEntity = UserEntity(
        userId,
        "email",
        "secret",
        Some("name"),
        emailIsVerified = true
      )
      userRepositoryMock.find(userId) shouldReturn Future.successful(Some(userEntity))
      menstruationServiceMock.changeOwner(userId) shouldReturn Future.failed(
        new RuntimeException("foo")
      )

      val actual = service.anonymize(userId)

      actual.failed.futureValue shouldBe a[RuntimeException]
    }
  }

  Feature("deleteData") {
    val userRepositoryMock      = mock[UserRepository]
    val mailClientMock          = mock[MailClient]
    val menstruationServiceMock = mock[MenstruationService]
    val service = new UserService(
      userRepositoryMock,
      mailClientMock,
      "salt",
      "http://",
      menstruationServiceMock
    )

    Scenario("should return done when removing all events for user") {
      val userId = UUID.randomUUID()
      val userEntity = UserEntity(
        userId,
        "email",
        "secret",
        Some("name"),
        emailIsVerified = true
      )
      userRepositoryMock.find(userId) shouldReturn Future.successful(Some(userEntity))
      val menstruation = Menstruation(LocalDate.now(), LocalDate.now())
      menstruationServiceMock.find(userId) shouldReturn Future.successful(Seq(menstruation))
      menstruationServiceMock.delete(userId, menstruation) shouldReturn Future.successful(
        Some(Done)
      )

      val actual   = service.deleteData(userId)
      val expected = Done

      actual.futureValue shouldBe expected
    }

    Scenario("should return fail when user does not exist") {
      val userId = UUID.randomUUID()
      userRepositoryMock.find(userId) shouldReturn Future.successful(None)

      val actual = service.deleteData(userId)

      actual.failed.futureValue shouldBe a[IllegalArgumentException]
    }

    Scenario("should return fail when deleting all existing period events for user fails") {
      val userId = UUID.randomUUID()
      val userEntity = UserEntity(
        userId,
        "email",
        "secret",
        Some("name"),
        emailIsVerified = true
      )
      userRepositoryMock.find(userId) shouldReturn Future.successful(Some(userEntity))
      val menstruation = Menstruation(LocalDate.now(), LocalDate.now())
      menstruationServiceMock.find(userId) shouldReturn Future.successful(Seq(menstruation))
      menstruationServiceMock.delete(userId, menstruation) shouldReturn Future.failed(
        new RuntimeException("foo")
      )

      val actual = service.deleteData(userId)

      actual.failed.futureValue shouldBe a[RuntimeException]
    }
  }

  Feature("delete") {
    val userRepositoryMock      = mock[UserRepository]
    val mailClientMock          = mock[MailClient]
    val menstruationServiceMock = mock[MenstruationService]
    val service = new UserService(
      userRepositoryMock,
      mailClientMock,
      "salt",
      "http://",
      menstruationServiceMock
    )

    Scenario("should return done when removing user succeeds") {
      val userId = UUID.randomUUID()
      val userEntity = UserEntity(
        userId,
        "email",
        "secret",
        Some("name"),
        emailIsVerified = true
      )
      userRepositoryMock.find(userId) shouldReturn Future.successful(Some(userEntity))
      userRepositoryMock.delete(userId) shouldReturn Future.successful(1)

      val actual   = service.delete(userId)
      val expected = Done

      actual.futureValue shouldBe expected
    }

    Scenario("should return fail when user does not exist") {
      val userId = UUID.randomUUID()
      userRepositoryMock.find(userId) shouldReturn Future.successful(None)

      val actual = service.delete(userId)

      actual.failed.futureValue shouldBe a[IllegalArgumentException]
    }

    Scenario("should return fail when deleting fails") {
      val userId = UUID.randomUUID()
      val userEntity = UserEntity(
        userId,
        "email",
        "secret",
        Some("name"),
        emailIsVerified = true
      )
      userRepositoryMock.find(userId) shouldReturn Future.successful(Some(userEntity))
      userRepositoryMock.delete(userId) shouldReturn Future.failed(new RuntimeException("foo"))

      val actual = service.deleteData(userId)

      actual.failed.futureValue shouldBe a[RuntimeException]
    }
  }
}
