package com.github.meandor.doctorfate.core.presentation
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

trait PasswordEncryption {
  val HASHING_ALGORITHM = "SHA-512"

  def hashPassword(password: String, salt: String): String = {
    val messageDigest = MessageDigest.getInstance(HASHING_ALGORITHM)
    messageDigest.update(salt.getBytes(StandardCharsets.UTF_8))
    val hashedPassword = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8))
    String.format("%032X", new BigInteger(1, hashedPassword))
  }
}
