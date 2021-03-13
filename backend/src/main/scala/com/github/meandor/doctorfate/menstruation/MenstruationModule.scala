package com.github.meandor.doctorfate.menstruation
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.meandor.doctorfate.auth.AuthModule
import com.github.meandor.doctorfate.core.{DatabaseModule, Module}
import com.github.meandor.doctorfate.core.presentation.Controller
import com.github.meandor.doctorfate.menstruation.data.MenstruationRepository
import com.github.meandor.doctorfate.menstruation.domain.MenstruationService
import com.github.meandor.doctorfate.menstruation.presentation.{
  MenstruationController,
  PredictionController
}
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext

final case class MenstruationModule(
    config: Config,
    authModule: AuthModule,
    databaseModule: DatabaseModule
)(
    implicit executionContext: ExecutionContext
) extends Module {
  val menstruationRepository = new MenstruationRepository(databaseModule.executionContext)
  val menstruationService    = new MenstruationService(menstruationRepository)

  override def start(): Option[Controller] = {
    logger.info("Start loading MenstruationModule")
    val predictionController = new PredictionController(authModule.accessTokenAuthenticator)
    val menstruationController = new MenstruationController(
      authModule.accessTokenAuthenticator,
      menstruationService
    )
    object CombinedController extends Controller {
      override def routes: Route = pathPrefix("menstruation") {
        predictionController.routes ~ menstruationController.routes
      }
    }
    logger.info("Done loading MenstruationModule")
    Option(CombinedController)
  }
}
