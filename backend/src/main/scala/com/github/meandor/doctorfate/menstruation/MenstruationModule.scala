package com.github.meandor.doctorfate.menstruation
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.meandor.doctorfate.auth.AuthModule
import com.github.meandor.doctorfate.core.Module
import com.github.meandor.doctorfate.core.presentation.Controller
import com.github.meandor.doctorfate.menstruation.domain.MenstruationService
import com.github.meandor.doctorfate.menstruation.presentation.{
  MenstruationController,
  PredictionController
}
import com.typesafe.config.Config

final case class MenstruationModule(config: Config, authModule: AuthModule) extends Module {
  override def start(): Option[Controller] = {
    logger.info("Start loading MenstruationModule")
    val predictionController = new PredictionController(authModule.accessTokenAuthenticator)
    val menstruationService  = new MenstruationService()
    val menstruationController = new MenstruationController(
      authModule.accessTokenAuthenticator,
      menstruationService
    )
    object CombinedController extends Controller {
      override def routes: Route = menstruationController.routes ~ predictionController.routes
    }
    logger.info("Done loading MenstruationModule")
    Option(CombinedController)
  }
}
