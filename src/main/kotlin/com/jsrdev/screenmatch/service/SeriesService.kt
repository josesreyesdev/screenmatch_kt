package com.jsrdev.screenmatch.service

import com.jsrdev.screenmatch.dto.SeriesResponse
import com.jsrdev.screenmatch.mappers.SeriesResponseMapper
import com.jsrdev.screenmatch.model.Series
import com.jsrdev.screenmatch.repository.SeriesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SeriesService @Autowired constructor(private val repository: SeriesRepository) {

    fun getAllSeries(): List<SeriesResponse> =
        seriesResponses(repository.findAll())

    fun getTop5Series(): List<SeriesResponse> =
        seriesResponses(repository.findTop5ByOrderByEvaluationDesc())

    fun getRecentSeriesReleases(): List<SeriesResponse> =
        seriesResponses(repository.mostRecentReleases(10))

    private fun seriesResponses(seriesList: List<Series>): List<SeriesResponse> =
        seriesList.asSequence()
        .map { SeriesResponseMapper().mapToSeriesResponse(it) }
        .toList()
}