package com.github.meandor.doctorfate.core
import com.github.meandor.doctorfate.core.presentation.Controller
import com.typesafe.scalalogging.LazyLogging

trait Module extends LazyLogging {
  def start(): Option[Controller]
}
