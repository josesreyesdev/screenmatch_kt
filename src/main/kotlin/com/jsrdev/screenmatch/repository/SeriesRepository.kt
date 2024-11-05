package com.jsrdev.screenmatch.repository

import com.jsrdev.screenmatch.model.Episode
import com.jsrdev.screenmatch.model.Genre
import com.jsrdev.screenmatch.model.Series
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface SeriesRepository : JpaRepository<Series, UUID> {

    /* derived queries */
    fun findByTitleContainsIgnoreCase(title: String): Series?
    fun findTop5ByOrderByEvaluationDesc(): List<Series>
    fun findByGenre(genre: Genre): List<Series>
    fun findByTotalSeasonsLessThanEqualAndEvaluationGreaterThanEqual(totalSeason: Int, evaluation: Double): List<Series>

    /* native query = is not flexible*/
    @Query(
        value = "SELECT * FROM series WHERE series.total_seasons <= 10 AND series.evaluation >= 7.8",
        nativeQuery = true
    )
    fun seriesBySeasonAndEvaluation(): List<Series>

    /* JPQL (The java persistence query language) */
    @Query("SELECT s FROM Series s WHERE s.totalSeasons <= :totalSeason AND s.evaluation >= :evaluation")
    fun selectSeriesBySeasonAndEvaluation(totalSeason: Int, evaluation: Double): List<Series>

    @Query("SELECT e FROM Series s JOIN s.episodes e WHERE e.title ILIKE %:episodeTitle%")
    fun episodesByTitle(episodeTitle: String): List<Episode>

    @Query("SELECT e FROM Series AS s JOIN s.episodes e WHERE s = :series ORDER BY e.evaluation DESC LIMIT 5")
    fun top5EpisodesBySeries(series: Series): List<Episode>
}