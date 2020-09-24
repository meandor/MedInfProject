package com.github.meandor.doctorfate.core.presentation
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

trait Controller {
  protected val invalidRequestResponse: Route =
    complete(StatusCodes.BadRequest, ErrorDTO("Invalid Request"))
  protected val failedResponse: Route =
    complete(StatusCodes.InternalServerError)

  def routes: Route
}
