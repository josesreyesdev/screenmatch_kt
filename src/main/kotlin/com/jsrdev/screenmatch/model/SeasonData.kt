package com.jsrdev.screenmatch.model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SeasonData(
    @JsonAlias("Title") val title: String = "",
    @JsonAlias("Season") val season: String = "", // num de temporada
    @JsonAlias("totalSeasons") val totalSeasons: Int = 0,
    @JsonAlias("Episodes") val episodeData: List<EpisodeData> = emptyList(),
    @JsonAlias("Response") val response: String = ""
)
