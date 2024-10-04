package com.exemple.weatherapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.exemple.weatherapp.Adapters.VpAdapter
import com.exemple.weatherapp.Adapters.WeatherAdapter
import com.exemple.weatherapp.Adapters.WeatherModel
import com.exemple.weatherapp.MainViewModel
import com.exemple.weatherapp.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject


class HoursFragment : Fragment() {

    private val model: MainViewModel by activityViewModels()
    lateinit var binding: FragmentHoursBinding
    val adapter = WeatherAdapter(null)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoursBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        model.liveDataCurrent.observe(viewLifecycleOwner){
            adapter.submitList(getHoursList(it))
        }
    }

    private fun getHoursList(wItem: WeatherModel): List<WeatherModel>{
        val hoursArray = JSONArray(wItem.hours)
        val list = ArrayList<WeatherModel>()
        for (i in 0 until hoursArray.length()){
            val item = WeatherModel(
                "",
                (hoursArray[i] as JSONObject).getString("time"),
                (hoursArray[i] as JSONObject).getJSONObject("condition").getString("text"),
                (hoursArray[i] as JSONObject).getJSONObject("condition").getString("icon"),
                (hoursArray[i] as JSONObject).getString("temp_c"),
                "",
                "",
                ""
            )
            list.add(item)
        }
        return list
    }
    private fun initRcView() = with(binding){
        rcView.layoutManager = LinearLayoutManager(activity)
        rcView.adapter = adapter
        adapter.submitList(listOf(
            WeatherModel(
                "",
                "12.00",
                "Sunny",
                "",
                "25 c",
                "",
                "",
                ""
            ) ,
            WeatherModel(
                "",
                "11 .00",
                "Sunny",
                "",
                "25 c",
                "",
                "",
                ""
            )  ,
            WeatherModel(
                "",
                "13.00",
                "Sunny",
                "",
                "25 c",
                "",
                "",
                ""
            )
        ))
    }

    companion object {
        fun newInstance() = HoursFragment()
    }
}