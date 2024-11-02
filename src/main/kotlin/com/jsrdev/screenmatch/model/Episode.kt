package com.jsrdev.screenmatch.model

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "episodes")
data class Episode(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,
    val title: String,
    val season: Int,
    @Column(name = "episode_number")
    val episodeNumber: Int,
    val evaluation: Double,
    @Column(name = "release_date")
    val releaseDate: LocalDate?,
    //@Column(name = "series_id", nullable = false)
    @ManyToOne
    val series: Series
) {
    override fun toString(): String {
        return "Episode(id=$id, title='$title', season=$season, episodeNumber=$episodeNumber, " +
                "evaluation=$evaluation, releaseDate=$releaseDate, seriesId='${series.id}')"
    }
}
