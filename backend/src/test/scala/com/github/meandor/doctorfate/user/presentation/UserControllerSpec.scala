package com.github.meandor.doctorfate.user.presentation

import akka.Done
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.meandor.doctorfate.UnitSpec
import com.github.meandor.doctorfate.core.presentation.ErrorDTO
import com.github.meandor.doctorfate.user.domain.{User, UserService}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import org.mockito.ArgumentMatchers.any

import java.util.UUID
import scala.concurrent.Future

class UserControllerSpec extends UnitSpec with ScalatestRouteTest {
  val authenticator: Credentials => Option[UUID] = {
    case _ @Credentials.Provided("accessToken") => Some(UUID.randomUUID())
    case _                                      => None
  }
  val userServiceMock: UserService = mock[UserService]
  val salt: String                 = "salt"
  val controller: UserController   = new UserController(salt, userServiceMock, authenticator)

  Feature("POST /user") {
    val path = "/user"
    val hashedPassword =
      "2908D2C28DFC047741FC590A026FFADE237AB2BA7E1266F010FE49BDE548B5987A534A86655A0D17F336588E540CD66F67234B152BBB645B4BB85758A1325D64"
    val password = "password"
    val email    = "foo@bar.com"
    val name     = None
    Scenario("should return 201 for created user") {
      val createdUser = User(email, password, name, hasVerifiedEmail = false)
      userServiceMock.registerUser(any()) shouldReturn Future.successful(
        Some(createdUser)
      )

      Post(path, UserDTO(email, password, name, isVerified = false)) ~> Route.seal(
        controller.routes
      ) ~> check {
        val actual = responseAs[UserDTO]
        val expected = UserDTO(
          createdUser.email,
          createdUser.password,
          createdUser.name,
          createdUser.hasVerifiedEmail
        )

        status shouldBe StatusCodes.Created
        actual shouldBe expected
        userServiceMock.registerUser(User(email, hashedPassword, name, hasVerifiedEmail = false)) was called
      }
    }

    Scenario("should return 400 for empty email") {
      val invalidEmail = " "

      Post(path, UserDTO(invalidEmail, password, name, isVerified = false)) ~> Route.seal(
        controller.routes
      ) ~> check {
        val actual   = responseAs[ErrorDTO]
        val expected = ErrorDTO("Invalid Request")

        status shouldBe StatusCodes.BadRequest
        actual shouldBe expected
        userServiceMock.registerUser(
          User(invalidEmail, hashedPassword, name, hasVerifiedEmail = false)
        ) wasNever called
      }
    }

    Scenario("should return 400 for empty password") {
      val email           = "foo@bar1.com"
      val invalidPassword = "  "

      Post(path, UserDTO(email, invalidPassword, name, isVerified = false)) ~> Route.seal(
        controller.routes
      ) ~> check {
        val actual   = responseAs[ErrorDTO]
        val expected = ErrorDTO("Invalid Request")

        status shouldBe StatusCodes.BadRequest
        actual shouldBe expected
        userServiceMock.registerUser(User(email, hashedPassword, name, hasVerifiedEmail = false)) wasNever called
      }
    }

    Scenario("should return 400 when registering fails") {
      val email = "foo@bar2.com"
      val name  = Option("foo bar")
      userServiceMock.registerUser(any()) shouldReturn Future.successful(None)

      Post(path, UserDTO(email, password, name, isVerified = false)) ~> Route.seal(
        controller.routes
      ) ~> check {
        status shouldBe StatusCodes.InternalServerError
        userServiceMock.registerUser(User(email, hashedPassword, name, hasVerifiedEmail = false)) was called
      }
    }

    Scenario("should return 500 when future fails") {
      val email = "foo@bar3.com"
      val name  = Option("foo bar")
      userServiceMock.registerUser(any()) shouldReturn Future.failed(
        new Exception("expected exception")
      )

      Post(path, UserDTO(email, password, name, isVerified = false)) ~> Route.seal(
        controller.routes
      ) ~> check {
        status shouldBe StatusCodes.InternalServerError
        userServiceMock.registerUser(User(email, hashedPassword, name, hasVerifiedEmail = false)) was called
      }
    }
  }

  Feature("POST /user/confirm") {
    val path = "/user/confirm"

    Scenario("should return 200 for verified user") {
      val id            = "foobar"
      val password      = "password"
      val email         = "foo@bar.com"
      val name          = None
      val confirmedUser = User(email, password, name, hasVerifiedEmail = true)
      userServiceMock.confirm(any()) shouldReturn Future.successful(
        Some(confirmedUser)
      )

      Post(path, ConfirmationDTO(id)) ~> Route.seal(
        controller.routes
      ) ~> check {
        val actual = responseAs[UserDTO]
        val expected = UserDTO(
          confirmedUser.email,
          confirmedUser.password,
          confirmedUser.name,
          confirmedUser.hasVerifiedEmail
        )

        status shouldBe StatusCodes.OK
        actual shouldBe expected
        userServiceMock.confirm(id) was called
      }
    }

    Scenario("should return 400 for empty email") {
      val invalidId = " "

      Post(path, ConfirmationDTO(invalidId)) ~> Route.seal(
        controller.routes
      ) ~> check {
        val actual   = responseAs[ErrorDTO]
        val expected = ErrorDTO("Invalid Request")

        status shouldBe StatusCodes.BadRequest
        actual shouldBe expected
        userServiceMock.confirm(invalidId) wasNever called
      }
    }

    Scenario("should return 400 when confirmation fails") {
      val wrongId = "wrongId"
      userServiceMock.confirm(any()) shouldReturn Future.successful(None)

      Post(path, ConfirmationDTO(wrongId)) ~> Route.seal(
        controller.routes
      ) ~> check {
        status shouldBe StatusCodes.BadRequest
        userServiceMock.confirm(wrongId) was called
      }
    }

    Scenario("should return 500 when future fails") {
      userServiceMock.confirm(any()) shouldReturn Future.failed(
        new Exception("expected exception")
      )
      val id = "exception"

      Post(path, ConfirmationDTO(id)) ~> Route.seal(
        controller.routes
      ) ~> check {
        status shouldBe StatusCodes.InternalServerError
        userServiceMock.confirm(id) was called
      }
    }
  }

  Feature("DELETE /user") {
    val path = "/user"

    Scenario("should return 204 for deleted user") {
      userServiceMock.delete(any[UUID]) shouldReturn Future.successful(Done)

      Delete(path) ~> addCredentials(OAuth2BearerToken("accessToken")) ~> Route.seal(
        controller.routes
      ) ~> check {
        status shouldBe StatusCodes.NoContent
      }
    }
  }

  Feature("DELETE /user/identifiable-data") {
    val path = "/user/identifiable-data"

    Scenario("should return 204 for deleted identifiable user data") {
      userServiceMock.anonymize(any[UUID]) shouldReturn Future.successful(Done)

      Delete(path) ~> addCredentials(OAuth2BearerToken("accessToken")) ~> Route.seal(
        controller.routes
      ) ~> check {
        status shouldBe StatusCodes.NoContent
      }
    }
  }

  Feature("DELETE /user/data") {
    val path = "/user/data"

    Scenario("should return 204 for deleted user data") {
      userServiceMock.deleteData(any[UUID]) shouldReturn Future.successful(Done)

      Delete(path) ~> addCredentials(OAuth2BearerToken("accessToken")) ~> Route.seal(
        controller.routes
      ) ~> check {
        status shouldBe StatusCodes.NoContent
      }
    }
  }
}
