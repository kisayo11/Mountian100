package com.kisayo.mountian100

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kisayo.mountian100.adapter.HourlyWeatherAdapter
import com.kisayo.mountian100.adapter.WeeklyWeatherAdapter
import com.kisayo.mountian100.data.MountainRepository
import com.kisayo.mountian100.databinding.ActivityDetailBinding
import com.kisayo.mountian100.network.ApiClient
import com.kisayo.mountian100.data.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val weatherService = ApiClient.weatherService
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    companion object {
        const val EXTRA_MOUNTAIN = "extra_mountain"
        const val EXTRA_WEATHER = "extra_weather"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전 화면에서 전달받은 데이터 처리
        val mountain = intent.getParcelableExtra<MountainRepository.Mountain>(EXTRA_MOUNTAIN)
        val weather = intent.getParcelableExtra<WeatherResponse>(EXTRA_WEATHER)

        // 툴바에 뒤로가기 버튼 추가
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = mountain?.name ?: "산 상세정보"

        setupMountainDetail(mountain, weather)
        setupWeatherToggle(mountain)
    }

    private fun setupMountainDetail(mountain: MountainRepository.Mountain?, weather: WeatherResponse?) {
        mountain?.let {
            binding.tvMountainName.text = it.name
            binding.tvHeight.text = "${String.format("%,d", it.height)}m"
            binding.tvAddress.text = it.address
        }

        weather?.let {
            binding.tvTemperature.text = "${it.main.temp.roundToInt()}°C"
            binding.tvWeatherDesc.text = it.weather.firstOrNull()?.description
            binding.tvHumidity.text = "습도 ${it.main.humidity}%"
            binding.tvWindSpeed.text = "풍속 ${it.wind.speed}m/s"
        }
    }

    private fun setupWeatherToggle(mountain: MountainRepository.Mountain?) {
        binding.weatherToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnHourlyWeather -> {
                        mountain?.let { fetchHourlyWeather(it) }
                    }
                    R.id.btnWeekWeather -> {
                        mountain?.let { fetchWeeklyWeather(it) }
                    }
                }
            }
        }

        // 초기값으로 시간별 날씨 조회
        mountain?.let { fetchHourlyWeather(it) }
    }

    private fun fetchHourlyWeather(mountain: MountainRepository.Mountain) {
        coroutineScope.launch {
            try {
                val hourlyForecast = withContext(Dispatchers.IO) {
                    weatherService.getHourlyForecast(
                        mountain.latitude,
                        mountain.longitude,
                        apiKey = BuildConfig.WEATHER_API_KEY
                    )
                }

                // RecyclerView 설정
                binding.recyclerViewHourlyWeather.layoutManager = LinearLayoutManager(
                    this@DetailActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )

                val adapter = HourlyWeatherAdapter(hourlyForecast.list.take(8))
                binding.recyclerViewHourlyWeather.adapter = adapter
            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, "날씨 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeeklyWeather(mountain: MountainRepository.Mountain) {
        coroutineScope.launch {
            try {
                val weeklyForecast = withContext(Dispatchers.IO) {
                    weatherService.getWeeklyForecast(
                        mountain.latitude,
                        mountain.longitude,
                        apiKey = BuildConfig.WEATHER_API_KEY
                    )
                }

                // RecyclerView 설정
                binding.recyclerViewHourlyWeather.layoutManager = LinearLayoutManager(this@DetailActivity)

                val adapter = WeeklyWeatherAdapter(weeklyForecast.list)
                binding.recyclerViewHourlyWeather.adapter = adapter
            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, "날씨 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}