package com.example.hw7m5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Bishkek")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.search
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(WeatherApi::class.java)
        val response = retrofit.getWeather(cityName, "bdb2917eb8179d50d760b162dcdc2e24", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val main = responseBody.main
                        val wind = responseBody.wind
                        val sys = responseBody.sys
                        val weather = responseBody.weather.firstOrNull()
                        val temperature = main.temp
                        val windSpeed = wind.speed
                        val humidity = main.humidity
                        val sunRise = sys.sunrise.toLong()
                        val sunSet = sys.sunset.toLong()
                        val seaLevel = main.pressure
                        val condition = weather?.main ?: "Unknown"
                        val maxTemp = main.temp_max
                        val minTemp = main.temp_min

                        binding.tvTemp.text = "$temperature °C"
                        binding.tvHumidity.text = "$humidity %"
                        binding.tvMaxTemp.text = "$maxTemp °C"
                        binding.tvMinTemp.text = "$minTemp °C"
                        binding.tvWeather.text = condition
                        binding.tvWindSpeed.text = "$windSpeed m/s"
                        binding.tvSunRise.text = time(sunRise)
                        binding.tvSunSet.text = time(sunSet)
                        binding.tvSea.text = "$seaLevel hPa"
                        binding.tvCondition.text = condition
                        binding.tvDay2.text = dayName(System.currentTimeMillis())
                        binding.tvDate.text = date()

                        changeImages(condition)

                    } else {
                        showErrorDialog("Empty response body")
                    }
                } else {
                    showErrorDialog("Failed to get weather data")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                showErrorDialog("Network error: ${t.message}")
            }
        })
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun changeImages(conditions: String) {
        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.back_sun)
                binding.weatherIcon.setImageResource(R.drawable.img_4)
            }

            "Partly Clouds", "Clouds", "Overcast", "Mist", "Forge" -> {
                binding.root.setBackgroundResource(R.drawable.colin)
                binding.weatherIcon.setImageResource(R.drawable.img_1)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.severin)
                binding.weatherIcon.setImageResource(R.drawable.cloud)
            }

            "Light Snow", "Moderate Snow", "heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.back_snow)
                binding.weatherIcon.setImageResource(R.drawable.img_2)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.back_sun)
                binding.weatherIcon.setImageResource(R.drawable.img_4)
            }
        }
    }


    private fun date(): String {
        val sdf = SimpleDateFormat("EEE SSS", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp * 1000)))
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        return sdf.format((Date(timestamp * 1000)))
    }


}
