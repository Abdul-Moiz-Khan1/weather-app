package com.example.weatherappminimal

import android.content.ContentValues.TAG
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weatherappminimal.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("islamabad")
        searchCity()
    }

    private fun searchCity() {
        val searchview = binding.searchView
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(
                        query
                    )
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityname: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response =
            retrofit.getweatherdata(cityname, "b722636a385eb0b7643c69448e1cac83", "metric")
        response.enqueue(object : Callback<weatherapp> {
            override fun onResponse(call: Call<weatherapp>, response: Response<weatherapp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temprature = responseBody.main.temp.toString()
                    val val_humidity = responseBody.main.humidity.toString()
                    val max_temp = responseBody.main.temp_max.toString()
                    val min_temp = responseBody.main.temp_min.toString()
                    val pressure = responseBody.main.pressure.toString()
                    val windspeed = responseBody.wind.speed.toString()
                    val rise = responseBody.sys.sunrise.toLong()
                    val set = responseBody.sys.sunset.toLong()
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"

                    binding.maxtemp.text = " Max Temp: ${max_temp}"
                    binding.mintemp.text = "Min Temp: ${min_temp}"
                    binding.humidty.text = "${val_humidity}%"
                    binding.pressure.text = "${pressure} hpa"
                    binding.windspeed.text = " ${windspeed} m/s"
                    binding.sunset.text = "${time(set)}"
                    binding.sunrise.text = "${time(rise)}"
                    binding.weather.text = condition
                    binding.condition.text = condition

                    changebackground(condition)

                    binding.date.text = date()
                    binding.day.text = dayname(System.currentTimeMillis())
                    binding.city.text = cityname




                    binding.temprature.text = "${temprature}"
//                    Log.d("TAG", "onresponse: $temprature")

                }
            }

            override fun onFailure(call: Call<weatherapp>, t: Throwable) {
                Log.d(
                    "tag: failfailfail", "onresponse: failshitssss" +
                            ""
                )
            }
        })


    }

    private fun changebackground(condition: String) {
        when(condition){
            "Haze" , "Clouds" ,"Cloudy", "Windy" , "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Rain","Drizzle" ,"Rainy" ,"Stormy","Hail", "Storm", "Heavy rain" , "Slight rain" , "Rainstorm" , "Thunderstorm" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Sunny" , "Hot" , "Clear" , "Heat wave" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Snow" , "Frost" , "Snowy" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }


        }
        binding.lottieAnimationView.playAnimation()

    }

    fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayname(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}