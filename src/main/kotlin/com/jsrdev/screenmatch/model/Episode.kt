package com.jsrdev.screenmatch.model

import java.time.LocalDate

data class Episode(
    val title: String = "",
    val season: Int = 0,
    val episodeNumber: Int = 0,
    val evaluation: Double = 0.0,
    val releaseDate: LocalDate? = null
)
