package com.jsrdev.screenmatch.main

import com.jsrdev.screenmatch.model.*
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
        println()
        //println("********************* Top 5 Episodes of ${seriesData.title} ***********************")
        // top5Episodes(episodes = episodeDataList)

        // Episode
        val episodes: MutableList<Episode> = episode(seasonDataList)
        //episodes.forEach(::println)

        // Search episodes by release date
        //searchEpisodesByReleaseDate(episodes)

        // Search episodes by title
        //val episode: Episode? = searchEpisodesByTitle(episodes)
        /*episode?.let {
            println("****Episodio Encontrado****")
            println(episode)
            println("Episodio ${it.episodeNumber}: ${it.title}, season: ${it.season}")
        } ?: println("Episodio no encontrado")*/

        // Evaluation By Season
        val evaluationBySeason: Map<Int, Double> = episodes.asSequence()
            .filter { it.evaluation > 0.0 }
            .groupBy { it.season } // agrupamos los episodios por temporada
            .mapValues { (_, episodesBySeason)  ->
                episodesBySeason.map { it.evaluation }.average() // Calculamos el promedio de evaluación por temporada
            }
        evaluationBySeason.forEach { println("Season: ${it.key}, Evaluation: %.2f".format(it.value)) }

        // General Statistics
        val stats: Statistics = episodes.asSequence()
            .filter { it.evaluation > 0.0 }
            .map { it.evaluation } // extraemos evaluaciones
            .toList()
            .let { evaluations ->
                Statistics(
                    count = evaluations.size,
                    sum = evaluations.sum(),
                    average = evaluations.average(),
                    min = evaluations.minOrNull() ?: 0.0,
                    max = evaluations.maxOrNull() ?: 0.0
                )
            }
        println()
        println("Count: ${stats.count}, Sum: ${stats.sum}, Average: ${"%.2f".format(stats.average)}, Min: ${stats.min}, Max: ${stats.max}")

        // General Statistics By Season
        val statsBySeason: Map<Int, Statistics> = episodes.asSequence()
            .filter { it.evaluation > 0.0 }
            .groupBy { it.season } // agrupamos los episodios por temporada
            .mapValues { (_, episodesBySeason)  ->
                episodesBySeason.map { it.evaluation }
                    .toList()
                    .let { evaluations ->
                        Statistics(
                            count = evaluations.size,
                            sum = evaluations.sum(),
                            average = evaluations.average(),
                            min = evaluations.minOrNull() ?: 0.0,
                            max = evaluations.maxOrNull() ?: 0.0
                        )
                    }
            }
        println()
        statsBySeason.forEach { (season, stats) ->
            println("Season: $season => Count: ${stats.count}, Sum: ${stats.sum}, Average: ${"%.2f".format(stats.average)}, Min: ${stats.min}, Max: ${stats.max}")
        }
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
            .onEach { e -> println("Before filter: ${e.title}, evaluation: ${e.evaluation}") }
            .filter { e ->
                e.evaluation.isNotEmpty() && !e.evaluation.equals("N/A", ignoreCase = true)
            }
            .onEach { e -> println("After filter: ${e.title}, evaluation: ${e.evaluation}") }
            .sortedByDescending { it.evaluation }  // Ordenamos por evaluación de forma descendente
            .onEach { e -> println("After sort: ${e.title}, evaluation: ${e.evaluation}") }
            .map { e -> e.copy(title = e.title.uppercase()) }
            .onEach { e -> println("After uppercase: ${e.title}, evaluation: ${e.evaluation}") }
            .take(5)
            .onEach { e -> println("After take: ${e.title}, evaluation: ${e.evaluation}") }
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

    private fun searchEpisodesByTitle(episodes: MutableList<Episode>): Episode? {
        var episodeTitle = getEntryEpisodeTitle()
        while (episodeTitle.isNullOrEmpty()) {
            println("Entrada no válida, intenta de nuevo")
            episodeTitle = getEntryEpisodeTitle()
        }

        return episodes.asSequence()
            //.filter { it.title.uppercase().contains(episodeTitle.uppercase()) }
            .filter { it.title.contains(episodeTitle, ignoreCase = true) }
            .firstOrNull()

    }

    private fun getEntryEpisodeTitle(): String?{
        println()
        println("Ingresa el titulo del episodio que desea sver: ")
        return readlnOrNull()
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