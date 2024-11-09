package com.jsrdev.screenmatch.dto

import com.jsrdev.screenmatch.model.Genre
import java.time.LocalDate
import java.util.UUID

data class SeriesResponse(
    val id: UUID,
    val title: String,
    val year: Int,
    val rated: String,
    val released: LocalDate,
    val runtime: String,
    val genre: Genre,
    val director: String,
    val writer: String,
    val actors: String,
    val synopsis: String,
    val language: String,
    val country: String,
    val awards: String,
    val poster: String,
    val evaluation: Double,
    val votes: String,
    val imdbID: String,
    val type: String,
    val totalSeasons: Int,
)
