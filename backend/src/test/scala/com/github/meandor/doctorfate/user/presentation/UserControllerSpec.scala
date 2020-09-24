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
    Scenario("should return 201 for created user") {
      val email       = "foo@bar.com"
      val password    = "password"
      val name        = None
      val createdUser = User()
      userServiceMock.createUser(email, any(), name) shouldReturn Future.successful(
        Some(createdUser)
      )

      Post("/user", UserDTO(email, password, name)) ~> Route.seal(controller.routes) ~> check {
        val actual   = responseAs[UserDTO]
        val expected = UserDTO(email, password, name)

        status shouldBe StatusCodes.Created
        actual shouldBe expected
        userServiceMock.createUser(email, any(), name) was called
      }
    }

    Scenario("should return 400 for empty email") {
      val invalidEmail = " "
      val password     = "password"
      val name         = None

      Post("/user", UserDTO(invalidEmail, password, name)) ~> Route.seal(controller.routes) ~> check {
        val actual   = responseAs[ErrorDTO]
        val expected = ErrorDTO("Invalid Request")

        status shouldBe StatusCodes.BadRequest
        actual shouldBe expected
        userServiceMock.createUser(invalidEmail, password, name) wasNever called
      }
    }
  }
}
