package com.exemple.weatherapp.Adapters

data class WeatherModel(
    val city: String,
    val time: String,
    val condition: String,
    val imageUrl: String,
    val currentTemp: String,
    val minTemp: String,
    val maxTemp: String,
    val hours: String,
    )
