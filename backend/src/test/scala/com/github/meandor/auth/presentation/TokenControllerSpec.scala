package com.github.meandor.auth.presentation
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.meandor.auth.UnitTestSpec
import com.github.meandor.doctorfate.ErrorDTO
import com.github.meandor.doctorfate.auth.domain.{AccessToken, IDToken, TokenService, Tokens}
import com.github.meandor.doctorfate.auth.presentation.{TokenController, TokenDTO, TokenRequestDTO}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import scala.concurrent.Future

class TokenControllerSpec extends UnitTestSpec with ScalatestRouteTest {
  val tokenServiceMock: TokenService = mock[TokenService]
  val secret: String                 = "secret"
  val controller: TokenController    = new TokenController(secret, tokenServiceMock)

  Feature("createToken") {
    val email           = "foo@bar.com"
    val password        = "password"
    val tokenRequestDTO = TokenRequestDTO(email, password)
    val accessToken     = AccessToken(UUID.randomUUID())
    val idToken         = IDToken(UUID.randomUUID(), "foo bar", "foo@bar.com", emailIsVerified = true)
    val token           = Tokens(idToken, accessToken)
    val algorithm       = Algorithm.HMAC512(secret)
    val verifier        = JWT.require(algorithm).withIssuer("doctor-fate").acceptLeeway(1).build()

    Scenario("should return idToken for valid user") {
      tokenServiceMock.createToken(email, password) shouldReturn Future.successful(token)
      Post("/token", tokenRequestDTO) ~> Route.seal(controller.routes) ~> check {

        val actualToken   = responseAs[TokenDTO]
        val actualIDToken = verifier.verify(actualToken.idToken)

        status shouldBe StatusCodes.Created
        actualIDToken.getClaim("name").asString() shouldBe idToken.name
        actualIDToken.getClaim("email").asString() shouldBe idToken.email
        actualIDToken.getClaim("email_verified").asBoolean() shouldBe idToken.emailIsVerified
      }
    }

    Scenario("should 400 for empty email") {
      val invalidEmail = " "
      Post("/token", TokenRequestDTO(invalidEmail, password)) ~> Route.seal(controller.routes) ~> check {
        val actual   = responseAs[ErrorDTO]
        val expected = ErrorDTO("Invalid Request")

        status shouldBe StatusCodes.BadRequest
        actual shouldBe expected
      }
    }

    Scenario("should 400 for invalid email") {
      val invalidEmail = "foo"
      Post("/token", TokenRequestDTO(invalidEmail, password)) ~> Route.seal(controller.routes) ~> check {
        val actual   = responseAs[ErrorDTO]
        val expected = ErrorDTO("Invalid Request")

        status shouldBe StatusCodes.BadRequest
        actual shouldBe expected
      }
    }

    Scenario("should 400 for empty password") {
      val invalidEmail = "  "
      Post("/token", TokenRequestDTO(invalidEmail, password)) ~> Route.seal(controller.routes) ~> check {
        val actual   = responseAs[ErrorDTO]
        val expected = ErrorDTO("Invalid Request")

        status shouldBe StatusCodes.BadRequest
        actual shouldBe expected
      }
    }
  }
}
