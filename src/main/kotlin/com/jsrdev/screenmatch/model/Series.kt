package com.jsrdev.screenmatch.model

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "series")
data class Series(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,
    @Column(unique = true)
    val title: String,
    val year: Int,
    val rated: String,
    val released: LocalDate,
    val runtime: String,
    @Enumerated(EnumType.STRING)
    val genre: Genre,
    val director: String,
    val writer: String,
    val actors: String,
    val synopsis: String,
    val language: String,
    val country: String,
    val awards: String,
    val poster: String,
    val evaluation: Double,
    val votes: String,
    @Column(name = "imdb_id")
    val imdbID: String,
    val type: String,
    @Column(name = "total_seasons")
    val totalSeasons: Int,
    @OneToMany(mappedBy = "series", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val episodes: MutableList<Episode> = mutableListOf(),
) {
    override fun toString(): String {
        return "Series(id=$id, title='$title', year=$year, rated='$rated', released=$released, " +
                "runtime='$runtime', genre=$genre, director='$director', writer='$writer', " +
                "actors='$actors', synopsis='$synopsis', language='$language', country='$country', " +
                "awards='$awards', poster='$poster', evaluation=$evaluation, votes='$votes', " +
                "imdbID='$imdbID', type='$type', totalSeasons=$totalSeasons)"
    }
}
