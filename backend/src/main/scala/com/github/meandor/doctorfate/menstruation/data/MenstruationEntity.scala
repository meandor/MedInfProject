package com.github.meandor.doctorfate.menstruation.data
import java.time.LocalDate
import java.util.UUID

final case class MenstruationEntity(userId: UUID, start: LocalDate, end: LocalDate)
