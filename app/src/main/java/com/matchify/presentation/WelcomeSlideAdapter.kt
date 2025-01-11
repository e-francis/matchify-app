package com.matchify.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.matchify.databinding.ItemWelcomeSlideBinding

class WelcomeSlideAdapter(
    private val slides: List<String>
) : RecyclerView.Adapter<WelcomeSlideAdapter.SlideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        return SlideViewHolder(
            ItemWelcomeSlideBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        holder.bind(slides[position])
    }

    override fun getItemCount() = slides.size

    class SlideViewHolder(
        private val binding: ItemWelcomeSlideBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String) {
            binding.tvWelcomeText.text = text
        }
    }
}