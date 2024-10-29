package com.jsrdev.screenmatch.main

import com.jsrdev.screenmatch.mappers.SeriesMapper
import com.jsrdev.screenmatch.model.*
import com.jsrdev.screenmatch.service.ConvertData
import com.jsrdev.screenmatch.service.GetFilmData
import com.jsrdev.screenmatch.utils.Config
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MenuMain(
    private val apiService: GetFilmData = GetFilmData(),
    private val deserializeData: ConvertData = ConvertData()
) {

    private val seriesList: MutableList<SeriesData> = mutableListOf()

    fun showMenu() {

        val seriesData: SeriesData = getSeriesData()

        if (seriesData.totalSeasons.isEmpty() || !seriesData.type.equals("Series", ignoreCase = true))
            throw RuntimeException("${seriesData.title} is not a Series")

        seriesList.add(seriesData)
        showSearchedSeries()

        while (true) {
            var option = getEntryOption()
            while (option == null) {
                option = invalidEntry()
            }

            when (option) {
                1 -> searchWebSeries()
                2 -> {
                    val season = getSeasonsData(seriesList[seriesList.size - 1])
                    printEpisodesData(season)
                }
                3 -> showSearchedSeries()
                4 -> {}

                0 -> {
                    println("****** Ejecución Finalizada ******")
                    break
                }
                else -> invalidEntry()
            }

        }
    }

    private fun invalidEntry(): Int? {
        println()
        println("Entrada no válida, intenta de nuevo")
        return getEntryOption()
    }

    private fun getEntryOption(): Int? {
        val menu = """
            1.- Search New Web Series
            2.- Show Episodes
            3.- Show Searched Series
            4.- Show Episodes 2
            
            0.- Exit
        """.trimIndent()

        println()
        println("Escribe la opcion que deseas hacer: ")
        println(menu)
        return readln().toIntOrNull()
    }

    private fun searchWebSeries() {
        val seriesData = getSeriesData()
        if (seriesData.totalSeasons.isEmpty() || !seriesData.type.equals("Series", ignoreCase = true))
            throw RuntimeException("${seriesData.title} is not a Series")

        seriesList.add(seriesData)
        println("$seriesData")
    }

    private fun getSeasonsData(series: SeriesData): MutableList<SeasonData> {
        val seasons = mutableListOf<SeasonData>()

        for (ind: Int in 1..series.totalSeasons.toInt()) {
            val seriesTitle = encodedAndFormatSeriesName(series.title)
            val season: SeasonData = getSeasonData(seriesTitle, ind)
            seasons.add(season)
        }

        return seasons
    }

    private fun printEpisodesData(seasons: List<SeasonData>) {
        // Show episode title for season
        println("************** Series: ${seasons[0].title} ***************")
        seasons.forEachIndexed { i, s ->
            println("Episodes from season ${i + 1} of the ${s.title} series")
            s.episodeData.forEachIndexed { ind, e ->
                println("   Episode ${ind + 1}: ${e.title} --------- released: ${e.released}")
            }
            println()
        }
        //seasons.forEach { it.episodeData.forEach { println(it.title)} }
        println("******************************************************")
    }

    private fun showSearchedSeries() {
        val series = seriesList.asSequence()
            .map { SeriesMapper().mapToSeries(it) }
            .toSet()
            .sortedByDescending { it.genre }

        series.forEachIndexed { i, s -> println("${i + 1}.- $s") }

    }

    /************************************** Fetch *********************************************/
    private fun getSeriesData(): SeriesData {

        var inputSeriesName = getSeriesName()
        while (inputSeriesName.isNullOrEmpty()) {
            println("Entrada no válida, intenta de nuevo")
            inputSeriesName = getSeriesName()
        }
        inputSeriesName = inputSeriesName.trim().lowercase()

        val seriesName = encodedAndFormatSeriesName(inputSeriesName)

        val buildUrl = buildURL(seriesName, null, null)
        val json = apiService.fetchData(url = buildUrl)

        return deserializeData.getData(json, SeriesData::class.java)
    }

    private fun getSeriesName(): String? {
        println()
        println("Escribe el nombre de la serie que deseas buscar: ")
        return readlnOrNull()
    }

    private fun getSeasonData(seriesName: String, season: Int): SeasonData {
        val url = buildURL(seriesName, season, null)
        val json = apiService.fetchData(url = url)

        return deserializeData.getData(json, SeasonData::class.java)
    }

    private fun encodedAndFormatSeriesName(seriesName: String): String =
        URLEncoder.encode(seriesName, StandardCharsets.UTF_8)

    private fun buildURL(seriesName: String, seasonNumber : Int?, episodeNumber: Int?): String {
        val apiKey: String = Config.API_KEY_OMDBAPI
        val urlBuilder: StringBuilder = StringBuilder("https://www.omdbapi.com/?t=")

        urlBuilder.append(seriesName).append("&apikey=").append(apiKey)
        if (seasonNumber != null && episodeNumber != null) {
            urlBuilder.append("&season=").append(seasonNumber).append("&episode=").append(episodeNumber)
        } else if (seasonNumber != null) {
            urlBuilder.append("&season=").append(seasonNumber)
        }
        return urlBuilder.toString()
    }
}