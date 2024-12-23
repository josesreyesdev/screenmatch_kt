package com.jsrdev.screenmatch.model

import java.text.Normalizer

enum class Genre(val genreOmdb: String, val genreEsp: String) {
    ACTION("Action", "Acción"),
    ROMANCE("Romance", "Romance"),
    COMEDY("Comedy", "Comedia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crimen"),
    SHORT("Short", "Corto"),
    HORROR("Horror", "Terror"),
    ADVENTURE("Adventure", "Aventura");

    companion object {
        fun fromString(text: String): Genre? {
            return entries.find { it.genreOmdb.equals(text, ignoreCase = true) }
        }

        fun fromEsp(text: String): Genre? {
            val normalizedText = text.normalize()
            return entries.find { it.genreEsp.normalize().equals(normalizedText, ignoreCase = true) }
        }

        // Función para normalizar el texto eliminando tildes
        private fun String.normalize(): String {
            return Normalizer.normalize(this, Normalizer.Form.NFD)
                .replace(Regex("\\p{M}"), "")
                .lowercase()
        }

        fun parseGenres(genre: String): Genre =
            Genre.fromEsp(genre) ?: Genre.fromString(genre)
            ?: throw IllegalArgumentException("Genre: $genre not found")
    }
}

