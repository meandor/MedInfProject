package com.github.meandor.doctorfate.menstruation.presentation
import java.time.LocalDate

final case class PeriodDTO(startDate: LocalDate, isActive: Boolean, duration: Int)
