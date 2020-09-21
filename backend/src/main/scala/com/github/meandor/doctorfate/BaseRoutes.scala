package com.github.meandor.doctorfate
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Route

object BaseRoutes {
  val statusMessage = """{
    "status": "😀"
  }"""

  def routes: Route = {
    path("health") {
      get {
        complete(HttpEntity(ContentTypes.`application/json`, statusMessage))
      }
    }
  }
}
