package com.jsrdev.screenmatch.mappers

import com.jsrdev.screenmatch.dto.SeriesResponse
import com.jsrdev.screenmatch.model.Series

class SeriesResponseMapper {
    fun mapToSeriesResponse(series: Series): SeriesResponse =
        SeriesResponse(
            id = series.id,
            title = series.title,
            year = series.year,
            rated = series.rated,
            released = series.released,
            runtime = series.runtime,
            genre = series.genre,
            director = series.director,
            writer = series.writer,
            actors = series.actors,
            synopsis = series.synopsis,
            language = series.language,
            country = series.country,
            awards = series.awards,
            poster = series.poster,
            evaluation = series.evaluation,
            votes = series.votes,
            imdbID = series.imdbID,
            type = series.type,
            totalSeasons = series.totalSeasons
        )
}