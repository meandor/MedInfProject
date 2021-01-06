package com.github.meandor.doctorfate.core.presentation
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

object Controller {
  val invalidRequestResponse: Route =
    complete(StatusCodes.BadRequest, ErrorDTO("Invalid Request"))
  val failedResponse: Route =
    complete(StatusCodes.InternalServerError)
  val unauthorized: Route =
    complete(StatusCodes.Unauthorized, ErrorDTO("Invalid user"))
}

trait Controller {
  def routes: Route
}
