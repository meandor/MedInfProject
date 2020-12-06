package com.github.meandor.doctorfate.menstruation
import com.github.meandor.doctorfate.core.presentation.Controller
import com.github.meandor.doctorfate.core.{DatabaseModule, Module}
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext

final case class MenstruationModule(config: Config, databaseModule: DatabaseModule)(
    implicit ec: ExecutionContext
) extends Module {
  override def start(): Option[Controller] = {
    logger.info("Start loading MenstruationModule")

    logger.info("Done loading MenstruationModule")
    None
  }
}
