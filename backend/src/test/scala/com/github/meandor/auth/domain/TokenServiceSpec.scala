package com.github.meandor.auth.domain
import java.time.LocalDateTime
import java.util.UUID

import com.github.meandor.auth.UnitWithFixtureSpec
import com.github.meandor.doctorfate.auth.data.{
  AuthenticationRepository,
  TokenEntity,
  TokenRepository
}
import com.github.meandor.doctorfate.auth.domain.{AccessToken, IDToken, TokenService, Tokens}
import org.mockito.ArgumentMatchers.any
import org.scalatest.Outcome
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TokenServiceSpec extends UnitWithFixtureSpec with ScalaFutures {
  class FixtureParam {
    val authenticationRepository: AuthenticationRepository = mock[AuthenticationRepository]
    val tokenRepository: TokenRepository                   = mock[TokenRepository]
    val tokenService: TokenService                         = new TokenService(authenticationRepository, tokenRepository)
    val email                                              = "foo@bar.com"
    val password                                           = "password"
    val userId: UUID                                       = UUID.randomUUID
  }

  override def withFixture(test: OneArgTest): Outcome = {
    withFixture(test.toNoArgTest(new FixtureParam()))
  }

  Feature("createToken") {
    Scenario("should not create token for invalid credentials") { fixture =>
      fixture.authenticationRepository.findUserId(fixture.email, fixture.password) shouldReturn
        Future.successful(None)

      val actual   = fixture.tokenService.createToken(fixture.email, fixture.password)
      val expected = Future.successful(None)

      actual.futureValue shouldBe expected.futureValue
      fixture.authenticationRepository.findUserId(fixture.email, fixture.password) wasCalled once
      fixture.tokenRepository.create(any()) wasNever called
    }

    Scenario("should not create when findUserId fails") { fixture =>
      val email             = "foo@bar.com"
      val password          = "password"
      val expectedException = new Exception("expected exception")
      fixture.authenticationRepository.findUserId(email, password) shouldReturn
        Future.failed(expectedException)

      val actual   = fixture.tokenService.createToken(email, password)
      val expected = Future.failed(expectedException)

      actual.failed.futureValue shouldBe expected.failed.futureValue
      fixture.authenticationRepository.findUserId(email, password) wasCalled once
      fixture.tokenRepository.create(any()) wasNever called
    }

    Scenario("should not create when create fails") { fixture =>
      val expectedException = new Exception("expected exception")
      fixture.authenticationRepository.findUserId(fixture.email, fixture.password) shouldReturn
        Future.successful(Option(fixture.userId))
      fixture.tokenRepository.create(fixture.userId) shouldReturn Future.failed(expectedException)

      val actual   = fixture.tokenService.createToken(fixture.email, fixture.password)
      val expected = Future.failed(expectedException)

      actual.failed.futureValue shouldBe expected.failed.futureValue
      fixture.authenticationRepository.findUserId(fixture.email, fixture.password) wasCalled once
      fixture.tokenRepository.create(fixture.userId) wasCalled once
    }

    Scenario("should create token successfully") { fixture =>
      fixture.authenticationRepository.findUserId(fixture.email, fixture.password) shouldReturn
        Future.successful(Option(fixture.userId))
      val tokenEntity = TokenEntity(
        fixture.userId,
        fixture.email,
        "foobar foo",
        emailIsVerified = true,
        LocalDateTime.now(),
        LocalDateTime.now()
      )
      fixture.tokenRepository.create(fixture.userId) shouldReturn
        Future.successful(tokenEntity)

      val actual = fixture.tokenService.createToken(fixture.email, fixture.password)
      val expectedIDToken = IDToken(
        tokenEntity.userID,
        tokenEntity.name,
        tokenEntity.email,
        tokenEntity.emailIsVerified
      )
      val expectedAccessToken = AccessToken(tokenEntity.userID)
      val expected            = Future.successful(Option(Tokens(expectedIDToken, expectedAccessToken)))

      actual.futureValue shouldBe expected.futureValue
      fixture.authenticationRepository.findUserId(fixture.email, fixture.password) wasCalled once
      fixture.tokenRepository.create(fixture.userId) wasCalled once
    }
  }
}
