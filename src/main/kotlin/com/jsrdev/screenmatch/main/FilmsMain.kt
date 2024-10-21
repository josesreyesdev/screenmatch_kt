package com.jsrdev.screenmatch.main

import com.jsrdev.screenmatch.model.Episode
import com.jsrdev.screenmatch.model.EpisodeData
import com.jsrdev.screenmatch.model.SeasonData
import com.jsrdev.screenmatch.model.SeriesData
import com.jsrdev.screenmatch.service.ConvertData
import com.jsrdev.screenmatch.service.GetFilmData
import com.jsrdev.screenmatch.utils.Config
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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

        // SeriesData
        val seriesData: SeriesData = getSeriesData(seriesName)

        if (seriesData.totalSeasons.isEmpty() || seriesData.totalSeasons.equals("N/A", ignoreCase = false))
            throw RuntimeException("${seriesData.title} contains N/A or is empty in the field totalSeason")

        // SeasonsData
        val seasonDataList: MutableList<SeasonData> = seasonsData(seriesData)
        // seasonDataList.forEach(::println)

        // EpisodesData
        // printEpisodesData(seasons = seasonDataList)

        val episodeDataList: MutableList<EpisodeData> = episodesData(seasonDataList)

        // Top 5 episodes from EpisodeData
        //println("********************* Top 5 Episodes of ${seriesData.title} ***********************")
        //top5Episodes(episodes = episodeDataList)

        // Episode
        val episodes = episode(seasonDataList)
        episodes.forEach(::println)

        // Search episodes by release date
        searchEpisodesByReleaseDate(episodes)

    }

    private fun seasonsData(series: SeriesData): MutableList<SeasonData> {
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

    private fun episodesData(seasons: List<SeasonData>): MutableList<EpisodeData> {
        return seasons.asSequence()
            .flatMap { s -> s.episodeData.asSequence() }
            .toMutableList()
    }

    private fun top5Episodes(episodes: List<EpisodeData>) =
        episodes.asSequence()
            .filter { e -> e.evaluation.isNotEmpty() && !e.evaluation.equals("N/A", ignoreCase = true) }
            .sortedByDescending { it.evaluation }  // Ordenamos por evaluación de forma descendente
            .take(5)
            .forEachIndexed { i, e ->
                println("${i + 1} -> ${e.title}, season: ${e.season}, evaluation: ${e.evaluation}")
            }

    private fun episode(seasons: List<SeasonData>): MutableList<Episode> {

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return seasons.asSequence()
            .flatMap { s ->
                s.episodeData.asSequence()
                    .map { e ->
                        Episode(
                            title = e.title.ifBlank { "Unknown Title" },
                            season = s.season.toIntOrNull() ?: 0,
                            episodeNumber = e.episode.toIntOrNull() ?: 0,
                            evaluation = e.evaluation.toDoubleOrNull() ?: 0.0,
                            releaseDate = parseReleaseDate(e.released, formatter)
                        )
                    }
            }
            .toMutableList()
    }

    private fun parseReleaseDate(released: String, formatter: DateTimeFormatter): LocalDate? {
        return try {
            LocalDate.parse(released, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    private fun searchEpisodesByReleaseDate(episodes: List<Episode>) {
        var date = getEntryDate()
        while (date == null) {
            println("Entrada no válida, intenta de nuevo")
            date = getEntryDate()
        }

        val searchByDate = LocalDate.of(date, 1, 1)

        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        episodes.asSequence()
            //.filter { it.releaseDate?.isAfter(searchByDate) == true}
            .filter { it.releaseDate != null && it.releaseDate.isAfter(searchByDate)}
            .forEachIndexed {i, e ->
                println(
                    "${i + 1} -> Season: ${e.season}, " +
                            "Episode: ${e.episodeNumber}.- ${e.title}, " +
                            "Released: ${e.releaseDate?.format(dtf)}")
            }

    }

    private fun getEntryDate(): Int?{
        println()
        println("Desde que fecha deseas ver los episodios?: ")
        return readlnOrNull()?.toIntOrNull()
    }

    /************************************** Fetch *********************************************/
    private fun getSeriesData(seriesName: String): SeriesData {

        val url = buildURL(seriesName, null, null)
        val json = GetFilmData().fetchData(url = url)
        //println(json)

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