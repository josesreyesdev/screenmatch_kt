package com.jsrdev.screenmatch.model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SeriesData(
    @JsonAlias("Title") val title: String = "",
    @JsonAlias("Year") val year: String = "",
    @JsonAlias("Rated") val rated: String = "",
    @JsonAlias("Released") val released: String = "",
    @JsonAlias("Runtime") val runtime: String = "",
    @JsonAlias("Genre") val genre: String = "",
    @JsonAlias("Director") val director: String = "",
    @JsonAlias("Writer") val writer: String = "",
    @JsonAlias("Actors") val actors: String = "",
    @JsonAlias("Plot") val plot: String = "",
    @JsonAlias("Language") val language: String = "",
    @JsonAlias("Country") val country: String = "",
    @JsonAlias("Awards") val awards: String = "",
    @JsonAlias("Poster") val poster: String = "",
    @JsonAlias("Ratings") val ratingData: List<RatingData> = emptyList(),
    @JsonAlias("Metascore") val metascore: String = "",
    @JsonAlias("imdbRating") val imdbRating: String = "", // evaluation
    @JsonAlias("imdbVotes") val imdbVotes: String = "",
    @JsonAlias("imdbID") val imdbID: String = "",
    @JsonAlias("Type") val type: String = "",
    @JsonAlias("totalSeasons") val totalSeasons: String = "",
    @JsonAlias("Response") val response: Boolean = false
)