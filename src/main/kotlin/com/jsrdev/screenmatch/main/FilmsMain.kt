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
        val series = getSeriesData(seriesName)

        // Episodes
        //val episodes = getEpisodesData(seriesName, 1, 1)
        //println("Episodes: $episodes")

        // Seasons
        val seasons = mutableListOf<SeasonData>()

        if (series.totalSeasons.isEmpty() || series.totalSeasons.equals("N/A", ignoreCase = false))
            throw RuntimeException("${series.title} contains N/A or is empty in the field totalSeason")

        for (ind: Int in 1..series.totalSeasons.toInt()) {
            val seriesTitle = encodedAndFormatSeriesName(series.title)
            val season: SeasonData = getSeasonData(seriesTitle, ind)
            seasons.add(season)
        } //seasons.forEach(::println)

        // Show episode title for season
        println("************** Series: ${series.title} ***************")
        seasons.forEachIndexed { i, s ->
            println("Episodes from season ${i + 1} of the ${s.title} series")
            s.episodeData.forEachIndexed { i2, e ->
                println("   Episode ${i2 + 1}: ${e.title}")
            }
            println()
        } //seasons.forEach(::println)
        //seasons.forEach { it.episodeData.forEach { println(it.title)} }
        println("******************************************************")
    }

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