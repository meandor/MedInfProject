package com.github.meandor.doctorfate.user.domain
import scala.concurrent.Future

class UserService() {
  def registerUser(user: User): Future[Option[User]] = ???
}
