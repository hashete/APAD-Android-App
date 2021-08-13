package com.example.apadandroidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchCuisines()
    }

    fun fetchCuisines() {
        println("Attempting to fetch cuisines")

        var url = "https://apadgroup2project.uc.r.appspot.com/api/getcuisines"

        var request = Request.Builder().url(url).build()

        var client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: okhttp3.Call, response: Response) {
                val body = response.body()?.string()
                println(body)
                val gson = GsonBuilder().create()

                val cuisineList = gson.fromJson(body, Array<Cuisines>::class.java).toList()
                println(cuisineList)


                runOnUiThread {
                    val cuisine_recycler_view: RecyclerView = findViewById(R.id.cuisine_recycler_view)
                    cuisine_recycler_view.layoutManager = LinearLayoutManager(this@MainActivity)
                    cuisine_recycler_view.adapter = CuisineRecyclerAdapter(cuisineList)
                }

            }
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("Failed to get cuisines")
                println(e)
            }

        })
    }

}