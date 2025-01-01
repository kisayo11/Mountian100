package com.kisayo.mountian100.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kisayo.mountian100.R
import com.kisayo.mountian100.data.MountainRepository
import com.kisayo.mountian100.databinding.ItemMountainBinding
import com.kisayo.mountian100.data.WeatherResponse
import kotlin.math.roundToInt

class MountainAdapter(
    private val onItemClick: (MountainWithWeather) -> Unit
) : RecyclerView.Adapter<MountainAdapter.ViewHolder>() {
    private var mountains = listOf<MountainWithWeather>()
    private var filteredList = listOf<MountainWithWeather>()

    data class MountainWithWeather(
        val mountain: MountainRepository.Mountain,
        var weatherInfo: WeatherResponse? = null
    )

    inner class ViewHolder(private val binding: ItemMountainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mountainWithWeather: MountainWithWeather) {
            binding.root.setOnClickListener {
                onItemClick(mountainWithWeather)
            }

            binding.apply {
                tvMountainName.text = mountainWithWeather.mountain.name
                tvHeight.text = "${String.format("%,d", mountainWithWeather.mountain.height)}m"

                mountainWithWeather.weatherInfo?.let { weather ->
                    // 날씨 아이콘
                    val iconCode = weather.weather.firstOrNull()?.icon
                    if (!iconCode.isNullOrEmpty()) {
                        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
                        Glide.with(ivWeatherIcon.context)
                            .load(iconUrl)
                            .error(R.drawable.ic_weather_sunny)
                            .into(ivWeatherIcon)
                    }

                    // 날씨 정보 표시
                    tvTemperature.text = "${weather.main.temp.roundToInt()}°C"
                    tvWeatherDesc.text = weather.weather.firstOrNull()?.description
                    tvHumidity.text = "${weather.main.humidity}%"
                    tvWindSpeed.text = "${weather.wind.speed}m/s"
                    tvRainProbability.text = "${(weather.pop * 100).roundToInt()}%"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMountainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount() = filteredList.size

    fun submitList(newList: List<MountainWithWeather>) {
        mountains = newList
        filteredList = newList.take(10)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            mountains.take(10)
        } else {
            mountains.filter {
                it.mountain.name.contains(query, ignoreCase = true)
            }.take(10)
        }
        notifyDataSetChanged()
    }
}