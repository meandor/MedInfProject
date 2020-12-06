package com.github.meandor.doctorfate.menstruation
import com.github.meandor.doctorfate.core.Module
import com.github.meandor.doctorfate.core.presentation.Controller
import com.github.meandor.doctorfate.menstruation.presentation.PredictionController
import com.typesafe.config.Config

final case class MenstruationModule(config: Config) extends Module {
  override def start(): Option[Controller] = {
    logger.info("Start loading MenstruationModule")
    val jwtAccessSecret = config.getString("auth.accessTokenSecret")
    val controller      = new PredictionController(jwtAccessSecret)
    logger.info("Done loading MenstruationModule")
    Option(controller)
  }
}
