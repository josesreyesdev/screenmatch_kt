package com.jsrdev.screenmatch.model

import com.fasterxml.jackson.annotation.JsonAlias

// Classification
data class RatingData(
    @JsonAlias("Source") val source: String = "",
    @JsonAlias("Value") val value: String = ""
)
