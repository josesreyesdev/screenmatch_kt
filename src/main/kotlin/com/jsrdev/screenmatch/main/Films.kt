package com.jsrdev.screenmatch.main

import com.jsrdev.screenmatch.service.GetData
import com.jsrdev.screenmatch.utils.Config
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class Films {

    fun data() {
        val seriesName = encodedAndFormatSeriesName("Game of thrones")
        val url = buildURL(seriesName, null, null)

        val json = GetData().getData(url = url)

        println("Response: $json")
    }

    private fun encodedAndFormatSeriesName(seriesName: String): String {
        val encodedSeriesName = URLEncoder.encode(seriesName, StandardCharsets.UTF_8)
        return encodedSeriesName.replace("+", "%20")
    }

    private fun buildURL(seriesName: String, seasonNumber: String?, episodeNumber: String?): String {
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