package com.github.meandor.doctorfate.user.data
import scala.concurrent.Future

class UserRepository() {
  def create(userEntity: UserEntity): Future[UserEntity] = ???

  def findByMail(email: String): Future[Option[UserEntity]] = ???
}
