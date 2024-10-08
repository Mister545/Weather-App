package com.exemple.weatherapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exemple.weatherapp.R
import com.exemple.weatherapp.databinding.ListItemBinding
import com.squareup.picasso.Picasso

class WeatherAdapter(val listener: Listener?) : ListAdapter<WeatherModel, WeatherAdapter.WeatherHolder>(Comparator()) {

    class WeatherHolder(view: View, val listener: Listener?) : RecyclerView.ViewHolder(view){
        val binding = ListItemBinding.bind(view)
        var itemTemp: WeatherModel? = null

        init {
            itemView.setOnClickListener {
                itemTemp?.let { it1 -> listener?.onClick(it1) }
            }
        }

        fun bind(item: WeatherModel) = with(binding){
            itemTemp = item
            tvData.text = item.time
            tvTemp.text = item.currentTemp.ifEmpty { "${item.maxTemp} C / ${item.minTemp}" }
            tvCondition.text = item.condition
            Picasso.get().load("https:"+item.imageUrl).into(im)

        }
    }
    class Comparator : DiffUtil.ItemCallback<WeatherModel>(){

        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return WeatherHolder(view, listener )
    }

    override fun onBindViewHolder(holder: WeatherHolder, position: Int) {
        holder.bind(getItem(position))
    }
    interface Listener{
        fun onClick(item: WeatherModel)
    }
}