package com.example.express.adapters // Уточните ваш пакет

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.express.R
import com.example.express.data.Restaurant
import com.example.express.databinding.ItemRestaurantBinding // Используем ViewBinding

class RestaurantAdapter(private val onItemClicked: (Restaurant) -> Unit) :
    ListAdapter<Restaurant, RestaurantAdapter.RestaurantViewHolder>(RestaurantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = getItem(position)
        holder.bind(restaurant)
        holder.itemView.setOnClickListener {
            onItemClicked(restaurant)
        }
    }

    inner class RestaurantViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(restaurant: Restaurant) {
            binding.textViewRestaurantName.text = restaurant.name
            binding.textViewRestaurantDescription.text = restaurant.description ?: "" // Показываем пустую строку если null

            Glide.with(binding.imageViewRestaurantLogo.context)
                .load(restaurant.logoUrl)
                .placeholder(R.drawable.ic_placeholder) // Ваш плейсхолдер
                .error(R.drawable.ic_placeholder) // Плейсхолдер на случай ошибки загрузки
                .into(binding.imageViewRestaurantLogo)
        }
    }

    class RestaurantDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem == newItem
        }
    }
} 