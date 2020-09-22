package com.github.meandor.doctorfate.auth.domain
import java.util.UUID

import com.github.meandor.doctorfate.auth.data.{
  AuthenticationRepository,
  TokenEntity,
  TokenRepository
}

import scala.concurrent.{ExecutionContext, Future}

class TokenService(authRepository: AuthenticationRepository, tokenRepository: TokenRepository)(
    implicit ec: ExecutionContext
) {
  def toTokens(tokenEntity: TokenEntity): Tokens = {
    val idToken =
      IDToken(tokenEntity.userID, tokenEntity.name, tokenEntity.email, tokenEntity.emailIsVerified)
    val accessToken = AccessToken(tokenEntity.userID)
    Tokens(idToken, accessToken)
  }

  def lift[A](x: Option[Future[A]])(implicit ec: ExecutionContext): Future[Option[A]] =
    x match {
      case Some(f) => f.map(Some(_))
      case None    => Future.successful(None)
    }

  def createToken(email: String, password: String): Future[Option[Tokens]] =
    for {
      maybeUserId: Option[UUID] <- authRepository.findUserId(email, password)
      maybeToken                <- lift(maybeUserId.map(tokenRepository.create))
    } yield maybeToken.map(toTokens)
}
