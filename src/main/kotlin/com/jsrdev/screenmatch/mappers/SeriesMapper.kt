package com.jsrdev.screenmatch.mappers

import com.jsrdev.screenmatch.model.Genre
import com.jsrdev.screenmatch.model.Series
import com.jsrdev.screenmatch.model.SeriesData
import com.jsrdev.screenmatch.service.OpenAIService
import com.theokanning.openai.OpenAiHttpException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

class SeriesMapper {

    fun mapToSeries(seriesData: SeriesData): Series {

        val year = extractStartYear(seriesData.year)
        val released = parseToDate(seriesData.released)
        val genre = parseGenres(seriesData.genre)
        val synopsis = translateSynopsis(seriesData.plot)
        val evaluation = seriesData.imdbRating.toDoubleOrNull() ?: 0.0
        val totalSeasons = seriesData.totalSeasons.toIntOrNull() ?: 0

        return Series(
            id = UUID.randomUUID(),
            title = seriesData.title,
            year = year,
            rated = seriesData.rated,
            released = released,
            runtime = seriesData.runtime,
            genre = genre,
            director = seriesData.director,
            writer = seriesData.writer,
            actors = seriesData.actors,
            synopsis = synopsis,
            language = seriesData.language,
            country = seriesData.country,
            awards = seriesData.awards,
            poster = seriesData.poster,
            evaluation = evaluation,
            votes = seriesData.imdbVotes,
            imdbID = seriesData.imdbID,
            type = seriesData.type,
            totalSeasons = totalSeasons,
            episodes = mutableListOf()
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
        val genres = genreStr.split(", ").map { it.trim() }

        val foundGenreFromString = genres.firstNotNullOfOrNull { Genre.fromString(it) }
        val foundGenreFromEsp = genres.firstNotNullOfOrNull { Genre.fromEsp(it) }

        return foundGenreFromString ?: foundGenreFromEsp ?: throw IllegalArgumentException("No valid genre found: $genreStr")

        /*genres.firstNotNullOfOrNull {
            Genre.fromString(it) ?: Genre.fromEsp(it)
        } ?: throw IllegalArgumentException("No valid genre found: $genreStr") */
    }

    private fun translateSynopsis(plot: String): String {
        return try {
            OpenAIService.getTranslation(plot)
        } catch (e: OpenAiHttpException) {
            //println("Error al traducir la sinopsis: ${e.message}")
            plot
        }
    }
}