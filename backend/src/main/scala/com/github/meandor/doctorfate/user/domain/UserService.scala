package com.github.meandor.doctorfate.user.domain
import scala.concurrent.Future

class UserService() {
  def createUser(email: String, password: String, name: Option[String]): Future[Option[User]] = ???
}
