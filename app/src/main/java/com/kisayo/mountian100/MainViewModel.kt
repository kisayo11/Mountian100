package com.kisayo.mountian100

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kisayo.mountian100.adapter.MountainAdapter
import com.kisayo.mountian100.data.MountainRepository
import com.kisayo.mountian100.network.ApiClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _mountainsWithWeather = MutableLiveData<List<MountainAdapter.MountainWithWeather>>()
    val mountainsWithWeather: LiveData<List<MountainAdapter.MountainWithWeather>> = _mountainsWithWeather

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadMountainWeather(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val mountains = MountainRepository(context).loadMountainData().shuffled()

                val mountainsWithWeather = mountains.map { mountain ->
                    async {
                        try {
                            // 날씨 API 요청 전에 로그 추가 (API 호출 URL 확인)
                            Log.d("MainViewModel", "Requesting weather for: Lat=${mountain.latitude}, Lon=${mountain.longitude}")

                            val weather = ApiClient.weatherService.getWeather(
                                latitude = mountain.latitude,
                                longitude = mountain.longitude,
                                apiKey = BuildConfig.WEATHER_API_KEY
                            )

                            // 날씨 정보 정상적으로 받아온 경우
                            Log.d("MainViewModel", "Weather data received for: ${mountain.latitude}, ${mountain.longitude}")

                            MountainAdapter.MountainWithWeather(mountain, weather)
                        } catch (e: Exception) {
                            // 에러 발생 시 로그를 통해 구체적인 에러 메시지 확인
                            Log.e("MainViewModel", "Error fetching weather for ${mountain.latitude}, ${mountain.longitude}: ${e.message}")
                            MountainAdapter.MountainWithWeather(mountain, null)
                        }
                    }
                }.awaitAll()

                _mountainsWithWeather.value = mountainsWithWeather
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}