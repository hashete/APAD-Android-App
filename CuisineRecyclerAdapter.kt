package com.example.apadandroidapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso

class CuisineRecyclerAdapter (val cuisineList: List<Cuisines>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    private var cuisineList: List<Cuisines> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CuisineViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cuisine_item_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        println(cuisineList.size)
        return cuisineList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is CuisineViewHolder -> {
                holder.bind(cuisineList.get(position))
            }
        }
    }

    class CuisineViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cuisineImage: ImageView = itemView.findViewById(R.id.cuisine_image)
        val cuisineName: TextView = itemView.findViewById(R.id.cuisine_name)

        init {
            itemView.setOnClickListener {
                val textFromTextView = it.findViewById<TextView>(R.id.cuisine_name)
                val selectedCuisine = textFromTextView.text.toString()
                val intent= Intent(itemView.context, PostActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra(
                    "selectedCuisine", selectedCuisine.toString()
                )
                itemView.context.startActivity(intent)

            }
        }

        fun bind(cuisine: Cuisines) {
            cuisineName.setText(cuisine.name)
//            val thumbnailImageView = holder?.view?.imageView_video_thumbnail
            Picasso.get().load(cuisine.photo).into(cuisineImage)

        }


    }


}