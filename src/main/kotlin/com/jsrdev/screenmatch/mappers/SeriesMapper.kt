package com.jsrdev.screenmatch.mappers

import com.jsrdev.screenmatch.model.Genre
import com.jsrdev.screenmatch.model.Series
import com.jsrdev.screenmatch.model.SeriesData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class SeriesMapper {

    fun mapToSeries(seriesData: SeriesData): Series {
        val year = seriesData.year.toIntOrNull() ?: 0
        val genre = Genre.fromString(seriesData.genre.split(",")[0].trim())
        val released = parseToDate(seriesData.released)
        val evaluation = seriesData.imdbRating.toDoubleOrNull() ?: 0.0
        val totalSeasons = seriesData.totalSeasons.toIntOrNull() ?: 0

        return Series(
            title = seriesData.title,
            year = year,
            rated = seriesData.rated,
            released = released,
            runtime = seriesData.runtime,
            genre = genre,
            director = seriesData.director,
            writer = seriesData.writer,
            actors = seriesData.actors,
            synopsis = seriesData.plot,
            language = seriesData.language,
            country = seriesData.country,
            awards = seriesData.awards,
            poster = seriesData.poster,
            evaluation = evaluation,
            votes = seriesData.imdbVotes,
            imdbID = seriesData.imdbID,
            type = seriesData.type,
            totalSeasons = totalSeasons
        )
    }

    private fun parseToDate(dateStr: String): LocalDate {
        return try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } catch (e: DateTimeParseException) {
            LocalDate.MIN // Usa un valor predeterminado si el formato no coincide
        }
    }
}