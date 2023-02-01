package com.example.storyapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.StoryItemBinding

class ListStoryAdapter(private val listStory: List<ListStoryItem>) :
    RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    class ViewHolder(var binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvItemName.text = listStory[position].name.toString()
            tvItemDescription.text = listStory[position].description.toString()
        }

        Glide.with(holder.itemView.rootView)
            .load(listStory[position].photoUrl)
            .into(holder.binding.ivItemPhoto)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listStory[position])
        }
    }

    override fun getItemCount(): Int {
        return listStory.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}