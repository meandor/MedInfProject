package com.github.meandor.doctorfate.menstruation.presentation

final case class PredictionDTO(
    ovulation: OvulationPredictionDTO,
    menstruation: MenstruationPredictionDTO
)
