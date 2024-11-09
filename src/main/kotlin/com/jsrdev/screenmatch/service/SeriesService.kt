package com.jsrdev.screenmatch.service

import com.jsrdev.screenmatch.dto.EpisodeResponse
import com.jsrdev.screenmatch.dto.SeriesResponse
import com.jsrdev.screenmatch.mappers.EpisodeResponseMapper
import com.jsrdev.screenmatch.mappers.SeriesResponseMapper
import com.jsrdev.screenmatch.model.Episode
import com.jsrdev.screenmatch.model.Genre
import com.jsrdev.screenmatch.model.Series
import com.jsrdev.screenmatch.repository.SeriesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class SeriesService @Autowired constructor(private val repository: SeriesRepository) {

    fun getAllSeries(): List<SeriesResponse> =
        seriesResponses(repository.findAll())

    fun getTop5Series(): List<SeriesResponse> =
        seriesResponses(repository.findTop5ByOrderByEvaluationDesc())

    fun getRecentSeriesReleases(): List<SeriesResponse> =
        seriesResponses(repository.mostRecentReleases(10))

    fun getSeriesByGenre(genre: String): List<SeriesResponse> {
        val genreParse: Genre = Genre.parseGenres(genre)

        return seriesResponses(repository.findByGenre(genreParse))
    }

    private fun seriesResponses(seriesList: List<Series>): List<SeriesResponse> =
        seriesList.asSequence()
        .map { SeriesResponseMapper().mapToSeriesResponse(it) }
        .toList()

    fun getSeriesById(id: UUID): SeriesResponse? {
        val series: Optional<Series> = repository.findById(id)

        return if (series.isPresent) seriesResponse(series.get()) else null
    }

    private fun seriesResponse(series: Series): SeriesResponse =
        SeriesResponseMapper().mapToSeriesResponse(series)

    fun getAllEpisodes(id: UUID): List<EpisodeResponse>? {
        val series: Optional<Series> = repository.findById(id)

        return if (series.isPresent) {
            episodesResponses(series.get().episodes)
        } else null
    }

    fun getEpisodesBySeason(id: UUID, season: Int): List<EpisodeResponse> {
        /*val series: Optional<Series> = repository.findById(id)

        return if (series.isPresent) {
            val episodes = series.get().episodes.asSequence()
                .filter { it.season == season }
                .toList()
            episodesResponses(episodes)
        } else null */
        return episodesResponses(repository.getEpisodesBySeason(id, season))
    }

    private fun episodesResponses(episodeList: List<Episode>): List<EpisodeResponse> =
        episodeList.asSequence()
            .map { EpisodeResponseMapper().mapToEpisodeResponse(it) }
            .toList()
}