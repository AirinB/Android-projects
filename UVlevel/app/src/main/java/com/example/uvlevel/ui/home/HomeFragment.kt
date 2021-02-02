package com.example.uvlevel.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.uvlevel.MainActivity
import com.example.uvlevel.R
import com.example.uvlevel.data.*
import com.example.uvlevel.network.OpenUvApi
import com.example.uvlevel.network.WeatherApi
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList


class HomeFragment : Fragment(), OnChartValueSelectedListener {

    private lateinit var user: User
    private lateinit var mainContext: MainActivity
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var longitude: String
    private lateinit var latitude: String


    private var uvResult_morning: Result? = null
    private var uvResult_day: Result? = null
    private var uvResult_evenight: Result? = null
    private var uvResult_night: Result? = null
    private var uvResult_current: Result? = null
    var colorMorning = Color.rgb(144, 190, 109)
    var colorDay = Color.rgb(249, 199, 79)
    var colorEvening = Color.rgb(67, 170, 139)
    var colorNight = Color.rgb(87, 117, 144)
    lateinit var address: String
    lateinit var temperature: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
                print("THERE are no permissions")
            return
        }

        if (context is MainActivity){
            mainContext = context
        } else {
            throw RuntimeException(
                    "The Activity is not MainActivity")
        }

        longitude = mainContext.latitude.toString()
        latitude = mainContext.latitude.toString()
        address = mainContext.city + ", " + mainContext.country

    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val tvAddress: TextView = root.findViewById(R.id.tvAddress)
        val tvBurnTime: TextView = root.findViewById(R.id.tvBurnTime)
        val uvChart: PieChart = root.findViewById(R.id.uvChart)
        val tvTemp: TextView = root.findViewById(R.id.tvTemp)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = "The uvLevel is " + uvLevel
        })

        uvChart.setEntryLabelTextSize(12f)

        tvAddress.text = address

        val retrofit_weather = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val key_weather = getString(R.string.key_wheather)
        val weatherAPI = retrofit_weather.create(WeatherApi::class.java)
        val weatherCall = weatherAPI.getWeatherDetails(mainContext.city, key= key_weather)



        weatherCall.enqueue(object : Callback<WeatherApiBase> {
            override fun onFailure(call: Call<WeatherApiBase>, t: Throwable) {
                //tvCurrentTemp.text = t.message.toString()
            }

            override fun onResponse(call: Call<WeatherApiBase>, response: Response<WeatherApiBase>) {
                val weatherResult = response.body()

                if (weatherResult != null) {
                    temperature = weatherResult.main?.temp.toString()
                    tvTemp.text = temperature + "F"
                }
            }
        })

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.openuv.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val current = LocalDateTime.now()

        // "2018-01-24T10:50:52.283Z"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatter_current = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'")
        //'T'HH:mm'Z'
        val date_morning = current.format(formatter).toString() + "T08:00Z"
        val date_day = current.format(formatter).toString() + "T12:00Z"
        val date_evening = current.format(formatter).toString() + "T16:00Z"
        val date_night = current.format(formatter).toString() + "T20:00Z"
        val date_current = current.format(formatter_current).toString()


        val uvAPI = retrofit.create(OpenUvApi::class.java)
        val uvCall_morning = uvAPI.getWeatherDetails(latitude, longitude, date_morning)
        var uvCall_day = uvAPI.getWeatherDetails(latitude, longitude, date_day)
        val uvCall_evening = uvAPI.getWeatherDetails(latitude, longitude, date_evening)
        val uvCall_night = uvAPI.getWeatherDetails(latitude, longitude, date_night)
        val uvCall_current = uvAPI.getWeatherDetails(latitude, longitude, date_current)



        uvCall_morning.enqueue(object : Callback<Base> {
            override fun onFailure(call: Call<Base>, t: Throwable) {


            }

            override fun onResponse(call: Call<Base>, response: Response<Base>) {
                val httpResponse = response.body()
                if (httpResponse != null) {
                    uvResult_morning = httpResponse.result!!

                    colorMorning = uvResult_morning!!.uv?.let { selectColor(it) }!!
                    updateChart(uvChart)
                }

            }
        })

        uvCall_day.enqueue(object : Callback<Base> {
            override fun onFailure(call: Call<Base>, t: Throwable) {

            }

            override fun onResponse(call: Call<Base>, response: Response<Base>) {
                val httpResponse = response.body()
                if (httpResponse != null) {
                    uvResult_day = httpResponse.result!!

                    colorDay = uvResult_day!!.uv?.let { selectColor(it) }!!
                    updateChart(uvChart)

                }

            }
        })

        uvCall_evening.enqueue(object : Callback<Base> {
            override fun onFailure(call: Call<Base>, t: Throwable) {


            }

            override fun onResponse(call: Call<Base>, response: Response<Base>) {
                val httpResponse = response.body()
                if (httpResponse != null) {
                    uvResult_evenight = httpResponse.result!!

                    colorEvening = uvResult_evenight!!.uv?.let { selectColor(it) }!!
                    updateChart(uvChart)
                }

            }
        })

        uvCall_night.enqueue(object : Callback<Base> {
            override fun onFailure(call: Call<Base>, t: Throwable) {

            }

            override fun onResponse(call: Call<Base>, response: Response<Base>) {
                val httpResponse = response.body()
                if (httpResponse != null) {
                    uvResult_night = httpResponse.result!!
                    colorNight = uvResult_night!!.uv?.let { selectColor(it) }!!
                    updateChart(uvChart)
                }

            }
        })
                uvCall_current.enqueue(object : Callback<Base> {
            override fun onFailure(call: Call<Base>, t: Throwable) {
                tvBurnTime.text = t.message.toString()

            }

            override fun onResponse(call: Call<Base>, response: Response<Base>) {
                val httpResponse = response.body()
                if (httpResponse != null) {
                    uvResult_current = httpResponse.result!!
                    val type: Int =  getUserPhototype()
                    val safeTime: String

                    when(type){
                        1->{ safeTime = uvResult_current!!.safe_exposure_time?.st1.toString()}
                        2 ->{safeTime = uvResult_current!!.safe_exposure_time?.st2.toString() }
                        3->{ safeTime = uvResult_current!!.safe_exposure_time?.st3.toString()}
                        4->{safeTime = uvResult_current!!.safe_exposure_time?.st4.toString() }
                        5->{safeTime = uvResult_current!!.safe_exposure_time?.st5.toString()}
                        else->{safeTime = uvResult_current!!.safe_exposure_time?.st6.toString()}
                    }

                    if(uvResult_current!!.safe_exposure_time?.st1 == null){
                        tvBurnTime.text = "You are safe today"
                        tvBurnTime.setTextColor(resources.getColor(R.color.dark_green))
                    }else{
                        tvBurnTime.text = "You will burn in : " + safeTime  + "minutes"
                        tvBurnTime.setTextColor(resources.getColor(R.color.carrot))
                    }


                }

            }
        })


        updateChart(uvChart)
        return root
    }




    fun getUserPhototype():Int {
        var type = 1
        Thread {
            val database = AppDatabase.getInstance(context = mainContext).userDAO()
            val users = database.getUsers()
            if (users.size == 1) {
                user = users[0]
                if (user.freckles && (user.skinColor == "Fair" ||user.skinColor == "White")
                        && (user.hairColor == "Blond" || user.hairColor == "Red")){
                    type = 1
                } else if ((user.skinColor == "Fair" ||user.skinColor == "White")
                        && (user.eyesColor == "Blue" || user.eyesColor == "Green")){
                    type = 2
                }else if ((user.skinColor == "Tanned" ||user.skinColor == "White")){
                    type = 3
                }else if(user.skinColor == "Brown"){
                    type = 4
                }else if(user.skinColor == "Dark brown"){
                    type = 5
                }else if(user.skinColor == "Black"){
                    type = 6
                }
            }
        }.start()

        return type
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    fun updateChart(uvChart: PieChart){

        var morning = 3
        var day = 2
        var night = 7
        var evening = 2
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(morning.toFloat(), "Morning"))
        entries.add(PieEntry(day.toFloat(), "Day"))
        entries.add(PieEntry(evening.toFloat(), "Evening"))
        entries.add(PieEntry(night.toFloat(), "Night"))

        val dataSet = PieDataSet(entries, "UV Level")

        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        uvChart.description.text = " "

        val colors = ArrayList<Int>()
//        colors.add(Color.GREEN)
//        colors.add(Color.RED)

        colors.add(colorMorning )
        colors.add(colorDay )
        colors.add(colorEvening )
        colors.add(colorNight )



        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueTextSize(0f)
        data.setValueTextColor(Color.TRANSPARENT)

//        pieChart.setCenterText("Total Questions 5" );
//        pieChart.setCenterTextSize(14f);
//        pieChart.setCenterTextColor(Color.BLUE);
        uvChart.data = data
        uvChart.setCenterTextColor(R.color.carrot)
        uvChart.setCenterTextSize(22f)
        uvChart.centerText = "NOW           UV " + (uvResult_current?.uv ?: 0)

        uvChart.highlightValues(null)

        uvChart.invalidate()

    }

    fun selectColor(uvLevel: Number): Int {
        val uv = uvLevel.toInt()
        when(uv){
            0 ->{
                return Color.rgb(77, 144, 142)
            }
            1 ->{
                return Color.rgb(77, 144, 142)
            }
            2 ->{
                return Color.rgb(67, 170, 139)
            }
            3 ->{
                return Color.rgb(144, 190, 109)
            }
            4 ->{
                return Color.rgb(249, 199, 79)
            }
            5 ->{
                return Color.rgb(249, 132, 74)
            }
            6 ->{
                return Color.rgb(249, 132, 74)
            }
            7 ->{
                return Color.rgb(243, 114, 44)
            }
            else ->{
                return Color.rgb(87, 117, 144)
            }
        }
    }
    override fun onValueSelected(e: Entry?, h: Highlight?) {
    }

    override fun onNothingSelected() {
        TODO("Not yet implemented")

    }
}