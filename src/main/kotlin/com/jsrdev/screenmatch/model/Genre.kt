package com.jsrdev.screenmatch.model

import java.text.Normalizer

enum class Genre(val genreOmdb: String, val genreEsp: String) {
    ACTION("Action", "Acción"),
    ROMANCE("Romance", "Romance"),
    COMEDY("Comedy", "Comedia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crimen");

    companion object {
        fun fromString(text: String): Genre {
            return entries.find { it.genreOmdb.equals(text, ignoreCase = true) }
                ?: throw IllegalArgumentException("No genre found: $text")
        }

        fun fromEsp(text: String): Genre {
            val normalizedText = text.normalize()
            return entries.find { it.genreEsp.normalize().equals(normalizedText, ignoreCase = true) }
                ?: throw IllegalArgumentException("No genre found: $text")
        }

        // Función para normalizar el texto eliminando tildes
        private fun String.normalize(): String {
            return Normalizer.normalize(this, Normalizer.Form.NFD)
                .replace(Regex("\\p{M}"), "")
                .lowercase()
        }
    }
}

