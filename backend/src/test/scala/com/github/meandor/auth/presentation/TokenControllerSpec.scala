package com.github.meandor.auth.presentation
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import akka.http.scaladsl.server._
import akka.http.scaladsl.model.StatusCodes
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.meandor.doctorfate.auth.domain.{AccessToken, IDToken, TokenService, Tokens}
import com.github.meandor.doctorfate.auth.presentation.{TokenController, TokenDTO, TokenRequestDTO}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import org.mockito.IdiomaticMockito

import scala.concurrent.Future

class TokenControllerSpec
    extends AnyFeatureSpec
    with Matchers
    with IdiomaticMockito
    with ScalatestRouteTest {
  val tokenServiceMock: TokenService = mock[TokenService]
  val secret: String                 = "secret"
  val controller: TokenController    = new TokenController(secret, tokenServiceMock)

  Feature("createToken") {
    Scenario("should return id and access token for valid user") {
      val email           = "foo@bar.com"
      val password        = "password"
      val tokenRequestDTO = TokenRequestDTO(email, password)
      val accessToken     = AccessToken()
      val idToken         = IDToken("foo bar", "foo@bar.com", emailIsVerified = true)
      val token           = Tokens(idToken, accessToken)
      tokenServiceMock.createToken(email, password) shouldReturn Future.successful(token)
      val algorithm = Algorithm.HMAC512(secret)
      val verifier  = JWT.require(algorithm).withIssuer("doctor-fate").acceptLeeway(1).build()

      Post("/token", tokenRequestDTO) ~> Route.seal(controller.routes) ~> check {
        val actualToken   = responseAs[TokenDTO]
        val actualIDToken = verifier.verify(actualToken.idToken)

        status shouldEqual StatusCodes.Created
        actualIDToken.getClaim("name").asString() shouldBe idToken.name
        actualIDToken.getClaim("email").asString() shouldBe idToken.email
        actualIDToken.getClaim("email_verified").asBoolean() shouldBe idToken.emailIsVerified
      }
    }
  }
}
