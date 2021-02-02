package com.example.weather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.weather.data.Base
import com.example.weather.network.WeatherApi
import kotlinx.android.synthetic.main.fragment_wheather_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.bumptech.glide.annotation.GlideModule;
import com.example.weather.network.GlideOptions

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WeatherDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
@GlideModule
class WeatherDetails : Fragment() {
    private lateinit var city: String
    private var viewImage: ImageView? = null
    private var imgUrl = "https://openweathermap.org/img/w/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            city = it.getString(ARG_PARAM1).toString()
        }



        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val key = "d44c3246b5694f58d34cc82e8a4e93ee"
        val weatherAPI = retrofit.create(WeatherApi::class.java)
        val weatherCall = weatherAPI.getWeatherDetails(city, key= key)

        val weatherDetails = this


        weatherCall.enqueue(object : Callback<Base> {
            override fun onFailure(call: Call<Base>, t: Throwable) {
                tvCurrentTemp.text = t.message.toString()
            }

            override fun onResponse(call: Call<Base>, response: Response<Base>) {
                val weatherResult = response.body()

                if (weatherResult != null) {
                    imgUrl += weatherResult.weather?.get(0)?.icon + ".png"

                     val currTemp = weatherResult.main?.temp.toString()
                     val description = weatherResult.weather?.get(0)?.description
                    val maxTemp = weatherResult.main?.temp_max.toString()
                    val minTemp = weatherResult.main?.temp_min.toString()

                    tvCurrentTemp.text = "Current temperature: " +  currTemp?.let { currTemp }
                    tvCityName.text = city?. let { city }
                    tvDescription.text =  description?.let {description}
                    tvMaxTemp.text = "Max temp: " + maxTemp?.let {maxTemp}
                    tvMinTemp.text =  "Min temp: "+minTemp?.let {minTemp}
                    tvHumidity.text = "Humidity: " + weatherResult.main?.humidity
                    tvSunrise.text = "Sunrise: " + weatherResult.sys?.sunrise
                    tvSunset.text = "Sunset: " + weatherResult.sys?.sunset

                    viewImage?.let { GlideOptions().downloadImage(weatherDetails, imgUrl, viewImage!!) }

                }
            }

        })








    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_wheather_details, container, false)
        viewImage = view.findViewById(R.id.ivWeather)



        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WheatherDetails.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                WeatherDetails().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                    }
                }
    }


}