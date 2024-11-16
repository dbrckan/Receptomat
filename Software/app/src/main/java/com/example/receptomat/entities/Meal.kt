package com.example.receptomat.entities

enum class Meal (val displayName: String) {
    BREAKFAST("Doručak"),
    LUNCH("Ručak"),
    DINNER("Večera"),
    DESSERT("Desert");

    companion object {
        fun fromDisplayName(displayName: String): Meal {
            return values().firstOrNull { it.displayName == displayName } ?: BREAKFAST
        }
    }
}