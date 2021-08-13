package com.example.apadandroidapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class PostActivity : AppCompatActivity() {


    var selectedCuisine: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        selectedCuisine = intent.getStringExtra("selectedCuisine").toString()
        println(selectedCuisine)
        fetchCuisines()
    }

    fun fetchCuisines() {
        println("Attempting to fetch cuisines")

        var url = "https://apadgroup2project.uc.r.appspot.com/api/getpostsbycuisine/$selectedCuisine"

        var request = Request.Builder().url(url).build()

        var client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: okhttp3.Call, response: Response) {
                val body = response?.body()?.string()
                println(body)
                val gson = GsonBuilder().create()

                val postList = gson.fromJson(body, Array<Posts>::class.java).toList()
                println(postList)


                runOnUiThread {
                    val post_recycler_view: RecyclerView = findViewById(R.id.post_recycler_view)
                    post_recycler_view.layoutManager = LinearLayoutManager(this@PostActivity)
//                    CuisineRecyclerAdapter().submit(cuisineList)
                    post_recycler_view.adapter = PostRecyclerAdapter(postList)
                }

            }
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("Failed to get cuisines")
                println(e)
            }

        })
    }
    fun createPostClick(view: View) {
        val intent= Intent(view.context, CreateRestaurant::class.java)
        intent.putExtra(
            "selectedCuisine", selectedCuisine
        )
        view.context.startActivity(intent)
    }
}