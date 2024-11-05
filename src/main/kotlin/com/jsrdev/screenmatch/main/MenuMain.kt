package com.jsrdev.screenmatch.main

import com.jsrdev.screenmatch.mappers.EpisodeMapper
import com.jsrdev.screenmatch.mappers.SeriesMapper
import com.jsrdev.screenmatch.model.*
import com.jsrdev.screenmatch.repository.SeriesRepository
import com.jsrdev.screenmatch.service.ConvertData
import com.jsrdev.screenmatch.service.GetFilmData
import com.jsrdev.screenmatch.utils.Config
import org.hibernate.exception.ConstraintViolationException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MenuMain (
    private val seriesRepository: SeriesRepository,
    private val apiService: GetFilmData = GetFilmData(),
    private val deserializeData: ConvertData = ConvertData()
) {

    private var series: MutableList<Series> = mutableListOf()

    fun showMenu() {
        while (true) {
            var option = getEntryOption()
            while (option == null) {
                option = invalidEntry()
            }

            when (option) {
                1 -> searchWebSeries()
                2 -> searchEpisodes()
                3 -> showSearchedSeries()
                4 -> searchSeriesByTitle()
                5 -> searchTop5Series()
                6 -> searchByGenreSeries()
                7 -> filterSeriesBySeasonAndEvaluation()
                8 -> searchEpisodeByTitle()
                9 -> {}

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
        println("Invalid entry, please, try again")
        return getEntryOption()
    }

    private fun getEntryOption(): Int? {
        val menu = """
            1.- Search New Web Series
            2.- Search Episodes
            3.- Show Searched Series
            4.- Search Series By Title
            5.- Top 5 Series
            6.- Search Series By Genre
            7.- Filter Series By Season And Evaluation
            8.- Search Episodes By Title
            9.- Top 5 Episodes By Series
            
            0.- Exit
        """.trimIndent()

        println()
        println("Enter the option: ")
        println(menu)
        return readln().toIntOrNull()
    }

    private fun searchWebSeries() {
        val seriesData = getSeriesData()
        println(seriesData)
        if (seriesData.totalSeasons.isEmpty() || !seriesData.type.equals("Series", ignoreCase = true))
            throw RuntimeException("${seriesData.title}, not found or maybe is not a Series: ${seriesData.type}")

        val series = SeriesMapper().mapToSeries(seriesData)
        println(series)

        try {
            seriesRepository.save(series)
        } catch (ex: ConstraintViolationException) {
            println("Series already exists in database: ${ex.message}")
        }
    }

    private fun searchEpisodes() {
        showSearchedSeries()

        var inputSeriesName = inputSeriesName()

        while (inputSeriesName.isNullOrEmpty()) {
            println("Invalid entry, please try again")
            inputSeriesName = inputSeriesName()
        }
        inputSeriesName = inputSeriesName.trim()

        val seriesInDB: Series? = series.asSequence()
            .filter { it.title.contains(inputSeriesName, ignoreCase = true) }
            .firstOrNull()

        seriesInDB?.let { series ->
            val seasons: MutableList<SeasonData> = getSeasonsData(series)
            val episodeList: MutableList<Episode> = seasons.asSequence()
                .flatMap { season -> season.episodeData.asSequence()
                    .map { episode -> EpisodeMapper().mapToEpisode(series, season.season, episode) }
                }
                .toMutableList()

            if (series.episodes.isEmpty()) {
                val updatedSeries = series.copy(episodes = episodeList)
                seriesRepository.save(updatedSeries)

                printEpisodesData(seasons)
            }
        } ?: println("$inputSeriesName not found")
    }

    private fun inputSeriesName(): String? {
        println("\nEnter the series name to view its episodes: ")
        return readlnOrNull()
    }

    private fun showSearchedSeries() {
        series = seriesRepository.findAll()

        series.asSequence()
            .sortedByDescending { it.genre }
            .forEachIndexed{ i, s -> println("${i + 1}.- $s") }
    }

    private fun searchSeriesByTitle() {
        var title = inputSeriesByTitle()
        while (title.isNullOrEmpty()) {
            println("Invalid entry, please try again")
            title = inputSeriesByTitle()
        }
        title = title.trim()

        val searchedSeries = seriesRepository.findByTitleContainsIgnoreCase(title)

        searchedSeries?.let {
            println("Series found: ")
            println(searchedSeries)
        } ?: println("$title not found")
    }

    private fun inputSeriesByTitle(): String? {
        println("\nEnter the series title: ")
        return readlnOrNull()
    }

    private fun searchTop5Series() {
        val top5Series: List<Series> = seriesRepository.findTop5ByOrderByEvaluationDesc()

        top5Series.forEachIndexed { i, s ->
            println("${i+1}.- Series: ${s.title}, evaluation: ${s.evaluation}")
        }
    }

    private fun searchByGenreSeries() {
        var entryGenre = inputSeriesByGenre()?.trim()
        while (entryGenre.isNullOrEmpty()) {
            println("Invalid entry, please try again")
            entryGenre = inputSeriesByGenre()?.trim()
        }
        val genre = parseGenres(entryGenre)

        val seriesByGenre: List<Series> = seriesRepository.findByGenre(genre)

        if (seriesByGenre.isNotEmpty())
            seriesByGenre.forEachIndexed { i, s -> println("${i+1}.- $s") }
        else
            println("Series not found by genre: $genre")
    }

    private fun inputSeriesByGenre(): String? {
        println("\nEnter the series genre: ")
        return readlnOrNull()
    }

    private fun parseGenres(genre: String): Genre =
        Genre.fromEsp(genre) ?: Genre.fromString(genre)
        ?: throw IllegalArgumentException("Genre: $genre not found")

    private fun filterSeriesBySeasonAndEvaluation() {
        var totalSeason = entrySeason()
        while (totalSeason == null) {
            println("Invalid entry, please try again")
            totalSeason = entrySeason()
        }

        var evaluation = entryEvaluation()
        while (evaluation == null) {
            println("Invalid entry, please try again")
            evaluation = entryEvaluation()
        }

        //val seriesBySeasonAndEvaluation = seriesRepository.
        // findByTotalSeasonsLessThanEqualAndEvaluationGreaterThanEqual(totalSeason, evaluation)
        //val seriesBySeasonAndEvaluation = seriesRepository.seriesBySeasonAndEvaluation()
        val seriesBySeasonAndEvaluation = seriesRepository.selectSeriesBySeasonAndEvaluation(totalSeason, evaluation)

        if (seriesBySeasonAndEvaluation.isNotEmpty())
            seriesBySeasonAndEvaluation.forEachIndexed { i, s ->
                println("${i+1}.- ${s.title} - evaluation: ${s.evaluation}, total season: ${s.totalSeasons}, genre: ${s.genre}")
            }
        else
            println("Series not found")
    }

    private fun entrySeason(): Int? {
        println("\nFilter series with how many seasons")
        return readlnOrNull()?.toIntOrNull()
    }

    private fun entryEvaluation(): Double? {
        println("\nWith evaluation, starting from which value")
        return readlnOrNull()?.toDoubleOrNull()
    }

    private fun searchEpisodeByTitle() {
        var episodeTitle = entryEpisodeByTitle()?.trim()
        while (episodeTitle.isNullOrEmpty()) {
            println("Invalid entry, please try again")
            episodeTitle = entryEpisodeByTitle()?.trim()
        }

        val episodes = seriesRepository.episodesByTitle(episodeTitle)

        if (episodes.isNotEmpty())
            episodes.forEachIndexed { i, e -> println("${i+1}.- ${e.series.title} - $e") }
        else
            println("Episodes not found")
    }

    private fun entryEpisodeByTitle(): String? {
        println("\nEnter title of the episode to search: ")
        return readlnOrNull()
    }

    private fun getSeasonsData(series: Series): MutableList<SeasonData> {
        val seasons = mutableListOf<SeasonData>()

        for (ind: Int in 1..series.totalSeasons) {
            val seriesTitle = encodedAndFormatSeriesName(series.title)
            val season: SeasonData = getSeasonData(seriesTitle, ind)
            seasons.add(season)
        }

        return seasons
    }

    private fun printEpisodesData(seasons: List<SeasonData>) {
        // Show episode title for season
        println("************** Series: ${seasons[0].title} ***************")
        seasons.forEach { s ->
            println("Episodes from season ${s.season} of the ${s.title} series")
            s.episodeData.forEachIndexed { ind, e ->
                println("   Episode ${ind + 1}: ${e.title} --------- released: ${e.released}")
            }
            println()
        }
        //seasons.forEach { it.episodeData.forEach { println(it.title)} }
        println("******************************************************")
    }

    /************************************** Fetch *********************************************/
    private fun getSeriesData(): SeriesData {

        var inputSeriesName = getSeriesName()
        while (inputSeriesName.isNullOrEmpty()) {
            println("Invalid entry, please try again")
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
        println("Enter series name: ")
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