package com.jsrdev.screenmatch.model

enum class Genre(val categoryOmdb: String, val categoryEsp: String) {
    ACTION("Action", "Acción"),
    ROMANCE("Romance", "Romance"),
    COMEDY("Comedy", "Comedia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crimen");

    companion object {
        fun fromString(text: String): Genre {
            return entries.find { it.categoryOmdb.equals(text, ignoreCase = true) }
                ?: throw IllegalArgumentException("No genre found: $text")
        }

        fun fromEsp(text: String): Genre {
            return entries.find { it.categoryEsp.equals(text, ignoreCase = true) }
                ?: throw IllegalArgumentException("No genre found: $text")
        }
    }
}

