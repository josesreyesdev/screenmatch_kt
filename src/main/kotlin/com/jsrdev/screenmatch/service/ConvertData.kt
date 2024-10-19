package com.jsrdev.screenmatch.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.IOException

class ConvertData : IConvertData {

    private val objectMapper = jacksonObjectMapper()

    override fun <T> getData(json: String, genericClass: Class<T>): T {

        return try {
            objectMapper.readValue(json, genericClass)
        } catch (ex: JsonProcessingException) {
            throw RuntimeException("Error processing JSON: ${ex.message}", ex)
        } catch (ex: IOException) {
            throw RuntimeException("Error reading input data: ${ex.message}", ex)
        }
    }
}