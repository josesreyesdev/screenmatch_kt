package com.jsrdev.screenmatch.service

import com.jsrdev.screenmatch.dto.SeriesResponse
import com.jsrdev.screenmatch.mappers.SeriesResponseMapper
import com.jsrdev.screenmatch.repository.SeriesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SeriesService @Autowired constructor(private val repository: SeriesRepository) {

    fun getAllSeries(): List<SeriesResponse> = repository.findAll().asSequence()
        .map { SeriesResponseMapper().mapToSeriesResponse(it)}
        .toList()



}