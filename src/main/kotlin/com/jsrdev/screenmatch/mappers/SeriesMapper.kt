package com.jsrdev.screenmatch.mappers

import com.jsrdev.screenmatch.model.Genre
import com.jsrdev.screenmatch.model.Series
import com.jsrdev.screenmatch.model.SeriesData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

class SeriesMapper {

    fun mapToSeries(seriesData: SeriesData): Series {
        val year = extractStartYear(seriesData.year)
        val genre = parseGenres(seriesData.genre)
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
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
            LocalDate.parse(dateStr/*normalizedDateStr*/, formatter)
        } catch (e: DateTimeParseException) {
            LocalDate.MIN // Valor predeterminado si el formato no coincide
        }
    }

    private fun extractStartYear(yearStr: String): Int {
        return yearStr.trim()
            .split("–")
            .firstOrNull()
            ?.trim()
            ?.toIntOrNull() ?: 0
    }

    private fun parseGenres(genreStr: String): Genre {
        val firstGenre = genreStr.split(", ").firstOrNull()?.trim()
        return firstGenre?.let { Genre.fromString(it) } ?: Genre.ACTION
    }

}