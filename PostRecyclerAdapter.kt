package com.example.apadandroidapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class PostRecyclerAdapter (val postList: List<Posts>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    private var cuisineList: List<Cuisines> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostRecyclerAdapter.PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.post_item_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        println(postList.size)
        return postList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PostRecyclerAdapter.PostViewHolder -> {
                holder.bind(postList.get(position))
            }
        }
    }

    class PostViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.post_image)
        val postName: TextView = itemView.findViewById(R.id.post_title)
        val postDesc: TextView = itemView.findViewById(R.id.post_description)
        val postTags: TextView = itemView.findViewById(R.id.post_tags)
        val postLocation: TextView = itemView.findViewById(R.id.post_location)
        val postTimestamp: TextView = itemView.findViewById(R.id.post_timestamp)

        fun bind(post: Posts) {
            postName.setText(post.title)
            postDesc.setText(post.description)
            postTags.setText(post.tags)
            postLocation.setText(post.location)
            postTimestamp.setText(post.time_stamp)

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
            Glide.with(itemView.context)
                .load(post.picture)
                .into(postImage)

        }


    }


}