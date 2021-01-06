package com.github.meandor.doctorfate.menstruation.data
import java.time.LocalDate
import java.util.UUID

case class MenstruationEntity(user: UUID, start: LocalDate, end: LocalDate)
