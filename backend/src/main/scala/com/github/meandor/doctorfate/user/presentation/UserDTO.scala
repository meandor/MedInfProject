package com.github.meandor.doctorfate.user.presentation

final case class UserDTO(email: String, password: String, name: Option[String], isVerified: Boolean)
