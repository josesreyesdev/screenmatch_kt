package com.jsrdev.screenmatch.dto

import java.time.LocalDate
import java.util.*

data class EpisodeResponse(
    val id: UUID,
    val title: String,
    val season: Int,
    val episodeNumber: Int,
    val evaluation: Double,
    val releaseDate: LocalDate?,
    val seriesId: UUID
)
