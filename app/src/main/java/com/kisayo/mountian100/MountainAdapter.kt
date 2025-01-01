package com.kisayo.mountian100

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kisayo.mountian100.databinding.ItemMountainBinding
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
                    tvTemperature.text = "${weather.main.temp.roundToInt()}°C"
                    tvWeatherDesc.text = weather.weather.firstOrNull()?.description
                    tvHumidity.text = "${weather.main.humidity}%"
                    tvWindSpeed.text = "${weather.wind.speed}m/s"
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