package com.github.meandor.doctorfate
import akka.http.scaladsl.server.Route

trait Controller {
  def routes: Route
}
