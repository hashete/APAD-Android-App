package com.example.apadandroidapp

import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import okhttp3.*
//import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.MediaType
import java.io.*
import java.util.*
import androidx.core.content.FileProvider
import android.R.attr.path
import android.content.ContentResolver


class CreateRestaurant : AppCompatActivity() {

//    val logging = HttpLoggingInterceptor()
//    logging.level = (HttpLoggingInterceptor.Level.BASIC)
    var selectedCuisine: String = ""
    private var selectedImage: Uri? = null //photo data from onActivityResult

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val GALLERY = 3
        private const val CAMERA_REQUEST_CODE = 2
        private const val Request_Code_Image_Picker = 100
        private val MEDIA_TYPE_PNG = MediaType.parse("image/*")
        private const val IMAGE_DIRECTORY = "Camera"
    }

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var photoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_restaurant)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        selectedCuisine = intent.getStringExtra("selectedCuisine").toString()

        //Upload Image
        val imageUpload = findViewById<ImageButton>(R.id.upload_image)
        imageUpload.setOnClickListener {
// user upload image from album
            openImageChooser()
        }

        val createPost = findViewById<Button>(R.id.create_post)
        createPost.setOnClickListener{
            submitPost()
        }

        //Camera Capture
        val cameraButton = findViewById<Button>(R.id.camera_button)
        cameraButton.setOnClickListener{
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            ){
                val intentCam = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                photoFile = getPhotoFileUri("photo.jpg");
                val fileProvider = FileProvider.getUriForFile(
                    this,
                    "com.example.apadandroidapp.provider_paths",
                    photoFile
                )
                intentCam.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
                if (intentCam.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intentCam, CAMERA_REQUEST_CODE);
                }
            }else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }

    }

    private fun fetchLocation(){
        val task: Task<Location> = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
            return
        }
        task.addOnSuccessListener {
            if(it !=null){
                Toast.makeText(applicationContext,"${it.latitude} ${it.longitude}",Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode ==  CAMERA_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }else{
                Toast.makeText(
                    this,
                    "Oops you denied the permission to use camera",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val temp = File("" + mediaStorageDir + File.separator.toString() + fileName)
        println(temp)
        return temp
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println(resultCode)
        if(resultCode ==  Activity.RESULT_OK){
            println("Entered result_ok")
            if(requestCode == CAMERA_REQUEST_CODE){
                println("Entered CAMERA_REQUEST_CODE")
                val takenImage: Bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                val cameraImage = findViewById<ImageView>(R.id.camera_image)
                cameraImage.setImageBitmap(takenImage)
                selectedImage = Uri.fromFile(getPhotoFileUri("photo.jpg"))
            }
            //if user selected the image
            if(requestCode == Request_Code_Image_Picker){
                    selectedImage = data?.data
                    var imageURI = findViewById<ImageView>(R.id.camera_image)
                    imageURI.setImageURI(selectedImage)
                }
            }
        }



    fun saveImage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }


        fun openImageChooser() {
        //open user album
            Intent(Intent.ACTION_PICK).also {
                it.type = "image/*" //all image type files
                startActivityForResult(it, Request_Code_Image_Picker)
            }
        }

    var location: String = ""
    private fun submitPost() {

        //Location
        val task: Task<Location> = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
        }
        task.addOnSuccessListener {
            if(it !=null){
                location = "${it.latitude} ${it.longitude}"
            } else {
                println("Location is null")
            }
        }


        val view: View = findViewById<ConstraintLayout>(R.id.create_rest_layout)

        //Get all input fields
        val restaurantNameText: TextView = view.findViewById(R.id.restaurant_name);
        val restaurantName = restaurantNameText.text.toString();
        val restaurantDescText: TextView = view.findViewById(R.id.restaurant_description);
        val restaurantDesc = restaurantDescText.text.toString();
        val restaurantTagsText: TextView = view.findViewById(R.id.restaurant_tags);
        val restaurantTags = restaurantTagsText.text.toString();

        if (selectedImage == null) {
            findViewById<ConstraintLayout>(R.id.create_rest_layout).snackbar("Select a restaurant image.")
        }
        println("************")
        println(selectedImage)
        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImage!!, "r", null) ?: return
        val file = File(cacheDir, contentResolver.getFileName(selectedImage!!))
//        val file = File(filesDir, contentResolver.getFileName(selectedImage!!))
//        val file = File(Environment.DIRECTORY_PICTURES + "/photo.png", contentResolver.getFileName(selectedImage!!))
//        val file = File(selectedImage.toString())
        println(file)
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)


        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("title", restaurantName)
            .addFormDataPart("cuisine", "Japanese")
            .addFormDataPart("description", "Random")
            .addFormDataPart("location", location)
            .addFormDataPart("tags", "#work")
            .addFormDataPart("picture", restaurantName,
                RequestBody.create(MEDIA_TYPE_PNG, file))
            .build()
        println(restaurantName)
        println(requestBody)
        val request = Request.Builder()
            .url("https://apadgroup2project.uc.r.appspot.com/api/makePost")
            .post(requestBody)
            .build()
        println(request)
        var client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: okhttp3.Call, response: Response) {
                val body = response.body()?.string()
                println(body)
            }
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("Failed to post")
                println(e)
            }
        })

    }

}

