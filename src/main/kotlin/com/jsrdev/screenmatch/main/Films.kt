package com.jsrdev.screenmatch.main

import com.jsrdev.screenmatch.model.EpisodeData
import com.jsrdev.screenmatch.model.SeasonData
import com.jsrdev.screenmatch.model.SeriesData
import com.jsrdev.screenmatch.service.ConvertData
import com.jsrdev.screenmatch.service.GetFilmData
import com.jsrdev.screenmatch.utils.Config
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class Films {

    fun data() {
        val seriesName = encodedAndFormatSeriesName("Vikings")

        // Series
        val series = getSeriesData(seriesName)
        println("Series: $series")

        // Episodes
        val episodes = getEpisodesData(seriesName, 1, 1)
        println("Episodes: $episodes")

        // Seasons
        val seasons = mutableListOf<SeasonData>()

        for (itemSeason: Int in 0..series.totalSeasons) {
            val season: SeasonData = getSeasonData(series.title, itemSeason)
            seasons.add(season)
        }
        seasons.forEach(::println)
    }

    private fun getSeriesData(seriesName: String): SeriesData{

        val url = buildURL(seriesName, null, null)
        val json = GetFilmData().getData(url = url)

        return ConvertData().getData(json, SeriesData::class.java)
    }

    private fun getEpisodesData(seriesName: String, season: Int, episode: Int): EpisodeData {

        val url = buildURL(seriesName, season, episode)
        val json = GetFilmData().getData(url = url)

        return ConvertData().getData(json, EpisodeData::class.java)
    }

    private fun getSeasonData(seriesName: String, season: Int): SeasonData {
        val url = buildURL(seriesName, season, null)
        val json = GetFilmData().getData(url = url)

        return ConvertData().getData(json, SeasonData::class.java)
    }

    private fun encodedAndFormatSeriesName(seriesName: String): String {
        val encodedSeriesName = URLEncoder.encode(seriesName, StandardCharsets.UTF_8)
        return encodedSeriesName.replace("+", "%20")
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