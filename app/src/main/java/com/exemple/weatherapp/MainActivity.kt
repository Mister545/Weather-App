package com.exemple.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.exemple.weatherapp.databinding.ActivityMainBinding
import com.exemple.weatherapp.fragments.MainFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.plaseHolder, MainFragment.newInstance())
            .commit()
    }
}

