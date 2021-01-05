package com.github.meandor.doctorfate.menstruation.presentation
import java.time.LocalDate

final case class MenstruationDTO(startDate: LocalDate, isActive: Boolean, duration: Int)
