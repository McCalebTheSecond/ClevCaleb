package com.techtree.clevcaleb.logic

data class UsCity(val city: String, val state: String, val timezone: String) {
    val label: String get() = "$city, $state"
}

/** US time zones and major cities (English, US-only build). */
object WorldTimeData {
    val cityDropdownOptions: List<Pair<String, String>> by lazy {
        cities.map { it.timezone to it.label }
    }

    val cities = listOf(
        UsCity("New York", "NY", "America/New_York"),
        UsCity("Washington", "DC", "America/New_York"),
        UsCity("Atlanta", "GA", "America/New_York"),
        UsCity("Miami", "FL", "America/New_York"),
        UsCity("Boston", "MA", "America/New_York"),
        UsCity("Detroit", "MI", "America/Detroit"),
        UsCity("Chicago", "IL", "America/Chicago"),
        UsCity("Bolivar", "MO", "America/Chicago"),
        UsCity("Dallas", "TX", "America/Chicago"),
        UsCity("Houston", "TX", "America/Chicago"),
        UsCity("Minneapolis", "MN", "America/Chicago"),
        UsCity("Denver", "CO", "America/Denver"),
        UsCity("Phoenix", "AZ", "America/Phoenix"),
        UsCity("Salt Lake City", "UT", "America/Denver"),
        UsCity("Los Angeles", "CA", "America/Los_Angeles"),
        UsCity("Seattle", "WA", "America/Los_Angeles"),
        UsCity("San Francisco", "CA", "America/Los_Angeles"),
        UsCity("Las Vegas", "NV", "America/Los_Angeles"),
        UsCity("Anchorage", "AK", "America/Anchorage"),
        UsCity("Honolulu", "HI", "Pacific/Honolulu"),
    )
}
