package com.kisayo.mountian100.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val pop: Double
) : Parcelable

@Parcelize
data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int
) : Parcelable

@Parcelize
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
    val pop: Double
) : Parcelable

@Parcelize
data class Wind(
    val speed: Double
) : Parcelable

// 시간별 예보 응답 모델
@Parcelize
data class HourlyForecastResponse(
    val list: List<HourlyWeatherItem>
) : Parcelable {
    @Parcelize
    data class HourlyWeatherItem(
        val dt: Long,
        val main: Main,
        val weather: List<Weather>,
        val wind: Wind,
        val pop: Double
    ) : Parcelable
}

// 주간 예보 응답 모델
@Parcelize
data class WeeklyForecastResponse(
    val list: List<WeeklyWeatherItem>
) : Parcelable {
    @Parcelize
    data class WeeklyWeatherItem(
        val dt: Long,
        val temp: Temperature,
        val weather: List<Weather>,
        val pop: Double
    ) : Parcelable

    @Parcelize
    data class Temperature(
        val day: Double,
        val min: Double,
        val max: Double
    ) : Parcelable

    @Parcelize
    data class Pop(
        val pop: Double
    ) : Parcelable

}