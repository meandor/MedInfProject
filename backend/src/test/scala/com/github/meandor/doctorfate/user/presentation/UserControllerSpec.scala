package com.github.meandor.doctorfate.user.presentation

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.meandor.doctorfate.UnitSpec
import com.github.meandor.doctorfate.core.presentation.ErrorDTO
import com.github.meandor.doctorfate.user.domain.{User, UserService}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import org.mockito.ArgumentMatchers.any

import scala.concurrent.Future

class UserControllerSpec extends UnitSpec with ScalatestRouteTest {
  val userServiceMock: UserService = mock[UserService]
  val salt: String                 = "salt"
  val controller: UserController   = new UserController(salt, userServiceMock)

  Feature("POST /user") {
    val hashedPassword =
      "2908D2C28DFC047741FC590A026FFADE237AB2BA7E1266F010FE49BDE548B5987A534A86655A0D17F336588E540CD66F67234B152BBB645B4BB85758A1325D64"
    val password = "password"
    val email    = "foo@bar.com"
    val name     = None
    Scenario("should return 201 for created user") {
      val createdUser = User(email, password, name)
      userServiceMock.registerUser(any()) shouldReturn Future.successful(
        Some(createdUser)
      )

      Post("/user", UserDTO(email, password, name)) ~> Route.seal(controller.routes) ~> check {
        val actual   = responseAs[UserDTO]
        val expected = UserDTO(createdUser.email, createdUser.password, createdUser.name)

        status shouldBe StatusCodes.Created
        actual shouldBe expected
        userServiceMock.registerUser(User(email, hashedPassword, name)) was called
      }
    }

    Scenario("should return 400 for empty email") {
      val invalidEmail = " "

      Post("/user", UserDTO(invalidEmail, password, name)) ~> Route.seal(controller.routes) ~> check {
        val actual   = responseAs[ErrorDTO]
        val expected = ErrorDTO("Invalid Request")

        status shouldBe StatusCodes.BadRequest
        actual shouldBe expected
        userServiceMock.registerUser(User(invalidEmail, hashedPassword, name)) wasNever called
      }
    }

    Scenario("should return 400 for empty password") {
      val email           = "foo@bar1.com"
      val invalidPassword = "  "

      Post("/user", UserDTO(email, invalidPassword, name)) ~> Route.seal(controller.routes) ~> check {
        val actual   = responseAs[ErrorDTO]
        val expected = ErrorDTO("Invalid Request")

        status shouldBe StatusCodes.BadRequest
        actual shouldBe expected
        userServiceMock.registerUser(User(email, hashedPassword, name)) wasNever called
      }
    }

    Scenario("should return 500 when registering fails") {
      val email = "foo@bar2.com"
      val name  = Option("foo bar")
      userServiceMock.registerUser(any()) shouldReturn Future.successful(None)

      Post("/user", UserDTO(email, password, name)) ~> Route.seal(controller.routes) ~> check {
        status shouldBe StatusCodes.InternalServerError
        userServiceMock.registerUser(User(email, hashedPassword, name)) was called
      }
    }

    Scenario("should return 500 when future fails") {
      val email = "foo@bar3.com"
      val name  = Option("foo bar")
      userServiceMock.registerUser(any()) shouldReturn Future.failed(
        new Exception("expected exception")
      )

      Post("/user", UserDTO(email, password, name)) ~> Route.seal(controller.routes) ~> check {
        status shouldBe StatusCodes.InternalServerError
        userServiceMock.registerUser(User(email, hashedPassword, name)) was called
      }
    }
  }
}
