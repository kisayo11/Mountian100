package com.kisayo.mountian100

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.kisayo.mountian100.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val adapter = MountainAdapter { mountainWithWeather ->
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_MOUNTAIN, mountainWithWeather.mountain)
            putExtra(DetailActivity.EXTRA_WEATHER, mountainWithWeather.weatherInfo)
        }
        startActivity(intent)
    }
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        setupObservers()  // 옵저버 설정 추가
        loadData()  // 데이터 로드 함수 변경
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                adapter.filter(s.toString())
            }
        })
    }

    private fun setupObservers() {
        viewModel.mountainsWithWeather.observe(this) { mountains ->
            binding.progressBar.isVisible = false
            binding.tvLoading.isVisible = false
            adapter.submitList(mountains)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.tvLoading.isVisible = isLoading
            binding.tvLoading.text = "정보를 불러오는 중입니다"
        }
    }

    private fun loadData() {
        binding.progressBar.isVisible = true
        viewModel.loadMountainWeather(this)
    }
}