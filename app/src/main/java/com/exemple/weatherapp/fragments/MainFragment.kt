package com.exemple.weatherapp.fragments

import android.Manifest
import android.app.DownloadManager.Request
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.exemple.weatherapp.Adapters.VpAdapter
import com.exemple.weatherapp.Adapters.WeatherModel
import com.exemple.weatherapp.MainViewModel
import com.exemple.weatherapp.databinding.FragmentMainBinding
import com.exemple.weatherapp.isPermissionGranted
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.lang.reflect.Method


class MainFragment : Fragment() {

    private lateinit var pLauncher: ActivityResultLauncher<String>
    lateinit var fLocationClient: FusedLocationProviderClient
    lateinit var binding: FragmentMainBinding
    private val apiKey = "70f0b010d8fa44d9ab295442242708"
    private val fList = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )
    private val tList = listOf(
        "Hours",
        "Days"
    )
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        updateCurrentCard()
        init()


//        requestWeatherData(getLocation().toString())
    }

    private fun isLocationEnabled(): Boolean{
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    private fun checkLocation(){
        if (isLocationEnabled()) {
            getLocation()
        }else{
            DialogManager.locationSettingsDialog(requireContext(), object: DialogManager.Listener{
                override fun onClick(name: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }
    private fun init() = with(binding){
        ibSync.setOnClickListener{
            tabLayout.selectTab(tabLayout.getTabAt(0))
            checkLocation()
        }
        ibSearch.setOnClickListener{
            DialogManager.searchByName(requireContext(), object: DialogManager.Listener{
                override fun onClick(name: String?) {
                    name?.let { it1 -> requestWeatherData(it1) }
                }

            })
        }
        val adapter = VpAdapter(activity as FragmentActivity, fList)
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        vp.adapter= adapter
        TabLayoutMediator(tabLayout, vp){
            tab, pos -> tab.text = tList[pos]
        }.attach()
    }

    private fun getLocation(){
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener{

                requestWeatherData("${it.result.latitude}, ${it.result.longitude}")
            }
    }

    private fun updateCurrentCard() = with(binding){
        model.liveDataCurrent.observe(viewLifecycleOwner){
            val maxMin = "${it.maxTemp} C/${it.minTemp}"
            tvData.text = it.time
            Picasso.get().load("https:"+it.imageUrl).into(imWeather)
            tvCity.text = it.city
            tvCurrentTemp.text = it.currentTemp.ifEmpty { maxMin}
            tvCondition.text = it.condition
            tvMaxMin.text = if (it.currentTemp.isEmpty()) "" else maxMin

        }

    }
    private fun permissionListener(){
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Toast.makeText(activity, "permasion $it", Toast.LENGTH_LONG).show()
        }
    }
    private fun checkPermission(){
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestWeatherData(cityName: String){
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                apiKey +
                "&q=" +
                "$cityName&days=3" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(com.android.volley.Request.Method.GET, url,
            {
                respons -> parseWeatherData(respons)
            },
            {
                error->
            })
        queue.add(request)
    }

    private fun parseWeatherData(result: String){
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel>{
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")

        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                "",
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel){
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current")
                .getJSONObject("condition")
                .getString("text"),
            mainObject.getJSONObject("current")
                .getJSONObject("condition")
                .getString("icon"),
            mainObject.getJSONObject("current").getString("temp_c").toFloat().toInt().toString(),
            weatherItem.minTemp.toFloat().toInt().toString(),
            weatherItem.maxTemp.toFloat().toInt().toString(),
            weatherItem.hours
        )
        model.liveDataCurrent.value = item
        Log.d("ooo", item.time)
    }

    companion object{
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
