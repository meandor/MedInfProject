package com.github.meandor.doctorfate.user.data
import courier._
import javax.mail.internet.InternetAddress
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class MailClient(userName: String, password: String)(implicit ec: ExecutionContext)
    extends LazyLogging {
  val mailer: Mailer = Mailer("smtp.gmail.com", 587)
    .auth(true)
    .as(userName, password)
    .startTls(true)()

  def sendConfirmationMail(email: String, confirmationLink: String): Future[Unit] = {
    val sender    = new InternetAddress(userName, "Menstra Period Tracker")
    val recipient = new InternetAddress(email)
    logger.info("Sending confirmation mail")
    mailer(
      Envelope
        .from(sender)
        .to(recipient)
        .subject("Registration Confirmation for Menstra")
        .content(
          Text(
            s"Thank you for registering at Menstra. To confirm the registration, please visit the link: $confirmationLink"
          )
        )
    )
  }
}
