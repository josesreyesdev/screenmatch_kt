package com.jsrdev.screenmatch.controller

import com.jsrdev.screenmatch.dto.SeriesResponse
import com.jsrdev.screenmatch.mappers.SeriesResponseMapper
import com.jsrdev.screenmatch.repository.SeriesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/series")
class SeriesController @Autowired constructor(private val repository: SeriesRepository) {

    @GetMapping
    fun getSeries(): List<SeriesResponse> = repository.findAll().asSequence()
        .map { SeriesResponseMapper().mapToSeriesResponse(it)}
        .toList()
}