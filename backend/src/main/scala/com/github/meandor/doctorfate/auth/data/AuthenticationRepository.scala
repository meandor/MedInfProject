package com.github.meandor.doctorfate.auth.data
import java.util.UUID

import scalikejdbc.{DB, scalikejdbcSQLInterpolationImplicitDef}

import scala.concurrent.{ExecutionContext, Future, blocking}

class AuthenticationRepository(executionContext: ExecutionContext) {
  implicit val ec: ExecutionContext = executionContext
  def findUserId(email: String, password: String): Future[Option[UUID]] =
    Future {
      blocking {
        DB localTx { implicit session =>
          sql"""
      SELECT id
      FROM users
      WHERE email = $email AND password = $password 
      """.map(result => UUID.fromString(result.get("id"))).single.apply()
        }
      }
    }
}
