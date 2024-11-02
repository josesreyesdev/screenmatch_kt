package com.jsrdev.screenmatch.mappers

import com.jsrdev.screenmatch.model.Episode
import com.jsrdev.screenmatch.model.EpisodeData
import com.jsrdev.screenmatch.model.Series
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

class EpisodeMapper {
    fun mapToEpisode(seriesId: Series, season: String, episodeData: EpisodeData): Episode {
        return Episode(
            id = UUID.randomUUID(),
            title = episodeData.title,
            season = season.toIntOrNull() ?: 0,
            episodeNumber = episodeData.episode.toIntOrNull() ?: 0,
            evaluation = episodeData.evaluation.toDoubleOrNull() ?: 0.0,
            releaseDate = parseToDate(episodeData.released),
            series = seriesId
        )
    }

    private fun parseToDate(dateStr: String): LocalDate? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            LocalDate.parse(dateStr, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }
}