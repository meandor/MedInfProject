package com.github.meandor.doctorfate.core.presentation
import akka.http.scaladsl.server.Route

trait Controller {
  def routes: Route
}
