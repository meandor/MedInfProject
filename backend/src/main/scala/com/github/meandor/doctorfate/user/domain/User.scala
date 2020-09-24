package com.github.meandor.doctorfate.user.domain

final case class User(email: String, password: String, name: Option[String])
