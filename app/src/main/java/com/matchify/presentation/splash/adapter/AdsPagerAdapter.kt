package com.matchify.presentation.splash.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.matchify.databinding.ItemAdBinding
import com.matchify.presentation.model.Ad

class AdsPagerAdapter(private val ads: List<Ad>) :
    RecyclerView.Adapter<AdsPagerAdapter.AdViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        val binding = ItemAdBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AdViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        val ad = ads[position]
        with(holder.binding) {
            ivAd.setImageResource(ad.imageResId)
            tvMessage.text = ""
        }
    }

    override fun getItemCount(): Int = ads.size

    inner class AdViewHolder(val binding: ItemAdBinding) :
        RecyclerView.ViewHolder(binding.root)
}