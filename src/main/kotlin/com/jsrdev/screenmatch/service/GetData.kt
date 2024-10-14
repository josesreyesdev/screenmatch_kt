package com.jsrdev.screenmatch.service

import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class GetData: ApiService {

    override fun getData(url: String): String {
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build()
        try {
            val jsonResponse = client.send(request, HttpResponse.BodyHandlers.ofString())
            return jsonResponse.body()
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        } catch (ex: InterruptedException) {
            throw RuntimeException(ex)
        }
    }
}