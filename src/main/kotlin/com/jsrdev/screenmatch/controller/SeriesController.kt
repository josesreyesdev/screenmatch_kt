package com.jsrdev.screenmatch.controller

import com.jsrdev.screenmatch.dto.SeriesResponse
import com.jsrdev.screenmatch.service.SeriesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/series")
class SeriesController @Autowired constructor(private val service: SeriesService) {

    @GetMapping
    fun getAllSeries(): List<SeriesResponse> = service.getAllSeries()

    @GetMapping("/top5")
    fun getTop5Series(): List<SeriesResponse> = service.getTop5Series()

}