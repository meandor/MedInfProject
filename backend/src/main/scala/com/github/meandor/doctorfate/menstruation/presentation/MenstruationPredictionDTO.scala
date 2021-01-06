package com.github.meandor.doctorfate.menstruation.presentation
import java.time.LocalDate

final case class MenstruationPredictionDTO(startDate: LocalDate, isActive: Boolean, duration: Int)
