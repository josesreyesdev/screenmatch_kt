package com.jsrdev.screenmatch.model

import java.time.LocalDate

data class Series(
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
