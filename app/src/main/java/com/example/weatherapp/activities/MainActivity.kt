package com.example.weatherapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.R
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.network.WeatherService
import com.example.weatherapp.utils.Constants
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class MainActivity : AppCompatActivity() {

    //private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

//        if (!isLocationEnabled()) {
//            Toast.makeText(
//                    this, "Your location provider is turned off. Please turn it on.",
//                    Toast.LENGTH_SHORT
//            ).show()
//
//            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            startActivity(intent)
//        } else {
//            Dexter.withActivity(this)
//                .withPermissions(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                )
//                .withListener(object : MultiplePermissionsListener {
//                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
//                        if (report!!.areAllPermissionsGranted()) requestLocationData()
//                        if (report.isAnyPermissionPermanentlyDenied)
//                            Toast.makeText(
//                                    this@MainActivity,
//                                    "You have denied location permission. Please allow it.",
//                                    Toast.LENGTH_SHORT
//                            ).show()
//                    }
//
//                    override fun onPermissionRationaleShouldBeShown(
//                            permissions: MutableList<PermissionRequest>?,
//                            token: PermissionToken?
//                    ) {
//                        showRationalDialogForPermissions()
//                    }
//                }).onSameThread().check()
//        }

        


    }

//    private fun isLocationEnabled(): Boolean {
//        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//    }
//
//    private fun showRationalDialogForPermissions() {
//        AlertDialog.Builder(this)
//            .setMessage("Permissions required")
//            .setPositiveButton("GO TO SETTINGS") { _, _ ->
//                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                val uri = Uri.fromParts("package", packageName, null)
//                intent.data = uri
//                startActivity(intent)
//            }
//            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
//            .show()
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun requestLocationData() {
//        val locationRequest = LocationRequest()
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        fusedLocationClient.requestLocationUpdates(
//                locationRequest,
//                locationCallback,
//                Looper.myLooper()
//        )
//    }

//    private val locationCallback = object : LocationCallback() {
//        override fun onLocationResult(p0: LocationResult) {
//            val lastLocation = p0.lastLocation
//            val latitude = lastLocation.latitude
//            Log.i("Current latitude", latitude.toString())
//            val longitude = lastLocation.longitude
//            Log.i("Current longitude", longitude.toString())
//
//            getLocationWeatherDetails(latitude, longitude)
//        }
//    }

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        if (Constants.isNetworkAvailable(this)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: WeatherService =
                retrofit.create(WeatherService::class.java)

            val listCall: Call<WeatherResponse> = service.getWeather(
                    latitude, longitude, Constants.METRIC_UNIT, Constants.APP_ID
            )

            listCall.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherList = response.body()
                        Log.i("Response result", "$weatherList")
                        setOnActivity(weatherList)
                    } else {
                        val sc = response.code()
                        when (sc) {
                            400 -> {
                                Log.e("Error 400", "Bad Request")
                            }
                            404 -> {
                                Log.e("Error 404", "Not found")
                            }
                            else -> {
                                Log.e("Error", "Generic error")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("Failure Error", t.message.toString())
                }


            })
        } else {
            Toast.makeText(
                    this,
                    "No internet connection avaliable.",
                    Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun setOnActivity(weatherList: WeatherResponse?) {
        if (weatherList != null) {
            address.text = weatherList.sys.country
            updated_at.text = LocalDateTime.now().toString()
            status.text = weatherList.weather[weatherList.weather.lastIndex].description.toUpperCase()
            when(weatherList.weather[weatherList.weather.lastIndex].icon){

                "01d" -> imageView.setImageResource(R.drawable.d01)
                "02d" -> imageView.setImageResource(R.drawable.d02)
                "03d" -> imageView.setImageResource(R.drawable.d03)
                "04d" -> imageView.setImageResource(R.drawable.d04)
                "09d" -> imageView.setImageResource(R.drawable.d09)
                "10d" -> imageView.setImageResource(R.drawable.d10)
                "11d" -> imageView.setImageResource(R.drawable.d11)
                "13d" -> imageView.setImageResource(R.drawable.d13)
                "50d" -> imageView.setImageResource(R.drawable.d50)

                "01n" -> imageView.setImageResource(R.drawable.n01)
                "02n" -> imageView.setImageResource(R.drawable.n02)
                "03n" -> imageView.setImageResource(R.drawable.d03)
                "04n" -> imageView.setImageResource(R.drawable.d04)
                "09n" -> imageView.setImageResource(R.drawable.d09)
                "10n" -> imageView.setImageResource(R.drawable.n10)
                "11n" -> imageView.setImageResource(R.drawable.d11)
                "13n" -> imageView.setImageResource(R.drawable.d13)
                "50n" -> imageView.setImageResource(R.drawable.d50)

                else -> Toast.makeText(this,"Smth wrong",Toast.LENGTH_SHORT).show()
            }
            var t = weatherList.main.temp.toString()
            t = t.substring(0, 2)
            temp.text = "$t°C"
            var tmax = weatherList.main.temp_max.toString()
            var tmin = weatherList.main.temp_min.toString()
            tmax = tmax.substring(0, 2)
            tmin = tmin.substring(0, 2)
            temp_max.text = "Max temp: $tmax°C"
            temp_min.text = "Min temp: $tmin°C"
            val formatter = SimpleDateFormat("hh:mm")
            sunrise.text = formatter.format(weatherList.sys.sunrise).toString()
            sunset.text = formatter.format(weatherList.sys.sunset).toString()
            var w = weatherList.wind.speed.toString()
            wind.text = "$w m/s"
            pressure.text = weatherList.main.pressure.toString()
            humidity.text = weatherList.main.humidity.toString()
            about.text = "Me"
        }
    }
}