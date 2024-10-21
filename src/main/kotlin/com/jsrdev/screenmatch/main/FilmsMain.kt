package com.jsrdev.screenmatch.main

import com.jsrdev.screenmatch.model.EpisodeData
import com.jsrdev.screenmatch.model.SeasonData
import com.jsrdev.screenmatch.model.SeriesData
import com.jsrdev.screenmatch.service.ConvertData
import com.jsrdev.screenmatch.service.GetFilmData
import com.jsrdev.screenmatch.utils.Config
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class FilmsMain {

    fun showMenu() {
        while (true) {
            var inputSeriesName = getEntryData()
            while (inputSeriesName.isNullOrEmpty()) {
                println("Entrada no válida, intenta de nuevo")
                inputSeriesName = getEntryData()
            }
            inputSeriesName = inputSeriesName.trim().lowercase()

            if (inputSeriesName.equals("n", ignoreCase = false)) {
                println("Ejecución Finalizada")
                break
            }

            val seriesName = encodedAndFormatSeriesName(inputSeriesName)

            data(seriesName)
        }
    }

    private fun getEntryData(): String? {
        println()
        println("Escribe el nombre de la serie que deseas buscar o N para salir de la ejecucion: ")
        return readlnOrNull()
    }

    private fun data(seriesName: String) {

        // Series
        val series: SeriesData = getSeriesData(seriesName)

        if (series.totalSeasons.isEmpty() || series.totalSeasons.equals("N/A", ignoreCase = false))
            throw RuntimeException("${series.title} contains N/A or is empty in the field totalSeason")

        // Episodes
        //val episodes = getEpisodesData(seriesName, 1, 1)

        // Seasons
        val seasons: List<SeasonData> = seasonsData(series)
         //seasons.forEach(::println)

        // Episodes
        printEpisodesData(seasons = seasons)

    }

    private fun seasonsData(series: SeriesData): List<SeasonData> {
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
                println("   Episode ${ind + 1}: ${e.title}")
            }
            println()
        }
        //seasons.forEach { it.episodeData.forEach { println(it.title)} }
        println("******************************************************")
    }

    /************************************** Fetch *********************************************/
    private fun getSeriesData(seriesName: String): SeriesData {

        val url = buildURL(seriesName, null, null)
        val json = GetFilmData().fetchData(url = url)
        println(json)

        return ConvertData().getData(json, SeriesData::class.java)
    }

    private fun getEpisodesData(seriesName: String, season: Int, episode: Int): EpisodeData {

        val url = buildURL(seriesName, season, episode)
        val json = GetFilmData().fetchData(url = url)

        return ConvertData().getData(json, EpisodeData::class.java)
    }

    private fun getSeasonData(seriesName: String, season: Int): SeasonData {
        val url = buildURL(seriesName, season, null)
        val json = GetFilmData().fetchData(url = url)

        return ConvertData().getData(json, SeasonData::class.java)
    }

    private fun encodedAndFormatSeriesName(seriesName: String): String {
        // encodedSeriesName.replace("+", "%20")
        return URLEncoder.encode(seriesName, StandardCharsets.UTF_8)
    }

    private fun buildURL(seriesName: String, seasonNumber: Int?, episodeNumber: Int?): String {
        val apiKey: String = Config.API_KEY
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