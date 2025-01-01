package com.kisayo.mountian100

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class HourlyWeatherAdapter(
    private val hourlyWeatherList: List<HourlyForecastResponse.HourlyWeatherItem>
) : RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder>() {

    class HourlyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvTemperature: TextView = itemView.findViewById(R.id.tvTemperature)
        val tvWeatherDesc: TextView = itemView.findViewById(R.id.tvWeatherDesc)

        fun bind(hourlyWeather: HourlyForecastResponse.HourlyWeatherItem) {
            val sdf = SimpleDateFormat("HH:00", Locale.getDefault())
            val time = sdf.format(Date(hourlyWeather.dt * 1000))
            tvTime.text = time

            tvTemperature.text = "${hourlyWeather.main.temp.roundToInt()}°C"
            tvWeatherDesc.text = hourlyWeather.weather.firstOrNull()?.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly_weather, parent, false)
        return HourlyWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        holder.bind(hourlyWeatherList[position])
    }

    override fun getItemCount() = hourlyWeatherList.size
}

class WeeklyWeatherAdapter(
    private val weekWeatherList: List<WeeklyForecastResponse.WeeklyWeatherItem>
) : RecyclerView.Adapter<WeeklyWeatherAdapter.WeeklyWeatherViewHolder>() {

    class WeeklyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTemperature: TextView = itemView.findViewById(R.id.tvTemperature)
        val tvWeatherDesc: TextView = itemView.findViewById(R.id.tvWeatherDesc)

        fun bind(weekWeather: WeeklyForecastResponse.WeeklyWeatherItem) {
            val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())
            val date = sdf.format(Date(weekWeather.dt * 1000))
            tvDate.text = date

            tvTemperature.text = "${weekWeather.temp.day.roundToInt()}°C"
            tvWeatherDesc.text = weekWeather.weather.firstOrNull()?.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyWeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weekly_weather, parent, false)
        return WeeklyWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeeklyWeatherViewHolder, position: Int) {
        holder.bind(weekWeatherList[position])
    }

    override fun getItemCount() = weekWeatherList.size
}