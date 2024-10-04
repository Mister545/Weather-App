package com.exemple.weatherapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.exemple.weatherapp.Adapters.WeatherAdapter
import com.exemple.weatherapp.Adapters.WeatherModel
import com.exemple.weatherapp.MainViewModel
import com.exemple.weatherapp.databinding.FragmentDaysBinding

class DaysFragment : Fragment(), WeatherAdapter.Listener {

    lateinit var binding: FragmentDaysBinding
    var adapter = WeatherAdapter(this@DaysFragment)
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaysBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        model.liveDataList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun init() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(activity)
        rcView.adapter = adapter
    }

    companion object {
        fun newInstance() = DaysFragment()
    }

    override fun onClick(item: WeatherModel) {
        model.liveDataCurrent.value = item
    }
}