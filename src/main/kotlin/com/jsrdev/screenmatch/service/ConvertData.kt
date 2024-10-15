package com.jsrdev.screenmatch.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class ConvertData : IConvertData {

    private val objectMapper = jacksonObjectMapper()

    override fun <T> getData(json: String, genericClass: Class<T>): T {
        return objectMapper.readValue(json, genericClass)
    }
}