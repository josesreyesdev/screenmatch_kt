package com.jsrdev.screenmatch.controller

import com.jsrdev.screenmatch.dto.EpisodeResponse
import com.jsrdev.screenmatch.dto.SeriesResponse
import com.jsrdev.screenmatch.service.SeriesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/series")
class SeriesController @Autowired constructor(private val service: SeriesService) {

    @GetMapping
    fun getAllSeries(): List<SeriesResponse> =
        service.getAllSeries()

    @GetMapping("/top5")
    fun getTop5Series(): List<SeriesResponse> =
        service.getTop5Series()

    @GetMapping("/releases")
    fun getRecentSeriesReleases(): List<SeriesResponse> =
        service.getRecentSeriesReleases()

    @GetMapping("/{id}")
    fun getSeriesById(@PathVariable id: UUID): SeriesResponse? =
        service.getSeriesById(id)

    @GetMapping("/{id}/seasons/all")
    fun getAllEpisodes(@PathVariable id: UUID): List<EpisodeResponse>? =
        service.getAllEpisodes(id)
    
    @GetMapping("/{id}/seasons/{season}")
    fun getEpisodesBySeason(@PathVariable id: UUID, @PathVariable season: Int): List<EpisodeResponse> =
        service.getEpisodesBySeason(id, season)

    @GetMapping("/genre/{genre}")
    fun getSeriesByGenre(@PathVariable genre: String): List<SeriesResponse>? =
        service.getSeriesByGenre(genre)
}