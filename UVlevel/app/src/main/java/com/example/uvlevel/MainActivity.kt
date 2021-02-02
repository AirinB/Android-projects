package com.example.uvlevel

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.uvlevel.data.AppDatabase
import com.example.uvlevel.data.User
import com.example.uvlevel.data.UserHandler
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.util.*
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), UserHandler {
    public var latitude by Delegates.notNull<Double>()
    public var longitude by Delegates.notNull<Double>()
    private val INITIAL_REQUEST = 1337
    private val LOCATION_COARSE_REQUEST = INITIAL_REQUEST + 3
    private val LOCATION_FINE_REQUST = INITIAL_REQUEST + 6
    public  var   city: String = ""
    public  var country: String = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.navigation_home, R.id.navigation_dashboard
                )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        requestNeededPermission()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            print("THERE are no permissions")
            return
        }



        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        //val location: Location? = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        longitude = (location?.getLongitude() ?: 0.0)
        Log.i("Longitude", longitude.toString())
        latitude = (location?.getLatitude() ?: 0.0)
        Log.i("Latitude", latitude.toString())


        val geocoder = Geocoder(this, Locale.getDefault())

        val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)

        if (!addresses.isEmpty()){
            city = addresses[0].getLocality()
            country = addresses[0].getCountryName()
        }


    }



    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this,
                        "I need it for location", Toast.LENGTH_SHORT).show()
            }

            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_COARSE_REQUEST)
        } else {
            // we already have permission
            Toast.makeText(this, "Already have location coarse permission", Toast.LENGTH_SHORT).show()
        }


        if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this,
                        "I need it for location", Toast.LENGTH_SHORT).show()
            }

            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_FINE_REQUST)
        } else {
            // we already have permission
            Toast.makeText(this, "Already have location fine permission", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            LOCATION_COARSE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location coarse perm granted", Toast.LENGTH_SHORT).show()

//                    val lm = getSystemService(LOCATION_SERVICE) as LocationManager
//                    //val location: Location? = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//                    val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//                    val longitude = (location?.getLongitude() ?: 0)
//                    Log.i("Longitude", longitude.toString())
//                    val latitude = (location?.getLatitude() ?: 0)
//                    Log.i("Latitude", latitude.toString())

                } else {
                    Toast.makeText(this, "Location perm NOT granted", Toast.LENGTH_SHORT).show()
                }
            }

            LOCATION_FINE_REQUST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location fine perm granted", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Location perm NOT granted", Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

    override fun userCreated(user: User) {

    }

    override fun userUpdated(user: User) {
        Thread{
            val database = AppDatabase.getInstance(this).userDAO()
            val users = database.getUsers()
            if (users.size == 1) {
                database.updateUser(user)


                runOnUiThread {
                    tvSkin.text = user.skinColor
                    tvEyes.text = user.eyesColor
                    tvHair.text = user.hairColor
                }
            }
        }.start()
    }

}



