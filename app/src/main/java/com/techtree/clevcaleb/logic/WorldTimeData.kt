package com.techtree.clevcaleb.logic

data class WorldCity(val city: String, val country: String, val timezone: String)

object WorldTimeData {
    val cities = listOf(
        WorldCity("New York", "USA", "America/New_York"),
        WorldCity("Los Angeles", "USA", "America/Los_Angeles"),
        WorldCity("Chicago", "USA", "America/Chicago"),
        WorldCity("Toronto", "Canada", "America/Toronto"),
        WorldCity("London", "UK", "Europe/London"),
        WorldCity("Paris", "France", "Europe/Paris"),
        WorldCity("Berlin", "Germany", "Europe/Berlin"),
        WorldCity("Tokyo", "Japan", "Asia/Tokyo"),
        WorldCity("Seoul", "South Korea", "Asia/Seoul"),
        WorldCity("Shanghai", "China", "Asia/Shanghai"),
        WorldCity("Hong Kong", "China", "Asia/Hong_Kong"),
        WorldCity("Singapore", "Singapore", "Asia/Singapore"),
        WorldCity("Mumbai", "India", "Asia/Kolkata"),
        WorldCity("Dubai", "UAE", "Asia/Dubai"),
        WorldCity("Sydney", "Australia", "Australia/Sydney"),
        WorldCity("Auckland", "New Zealand", "Pacific/Auckland"),
        WorldCity("São Paulo", "Brazil", "America/Sao_Paulo"),
        WorldCity("Mexico City", "Mexico", "America/Mexico_City"),
        WorldCity("Bolivar", "USA", "America/Chicago"),
        WorldCity("Johannesburg", "South Africa", "Africa/Johannesburg"),
    )
}
