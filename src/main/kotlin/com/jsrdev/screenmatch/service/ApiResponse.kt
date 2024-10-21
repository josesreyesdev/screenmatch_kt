package com.jsrdev.screenmatch.service

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiResponse(
    @JsonAlias("Response") val response: String = "",
    @JsonAlias("Error") val error: String? = null
)
