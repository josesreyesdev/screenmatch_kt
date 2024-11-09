package com.jsrdev.screenmatch.mappers

import com.jsrdev.screenmatch.dto.EpisodeResponse
import com.jsrdev.screenmatch.model.Episode

class EpisodeResponseMapper {
    fun mapToEpisodeResponse(episode: Episode): EpisodeResponse =
        EpisodeResponse(
            id = episode.id,
            title = episode.title,
            season = episode.season,
            episodeNumber = episode.episodeNumber,
            evaluation = episode.evaluation,
            releaseDate = episode.releaseDate,
            seriesId = episode.series.id
        )
}