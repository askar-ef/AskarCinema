package com.example.askarcinema

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.askarcinema.databinding.ActivityEditBinding
import com.google.firebase.database.FirebaseDatabase

class EditActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MOVIE_DATA = "extra_movie_data"
    }

    private lateinit var binding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the selected movie data from the Bundle
        val movieData = intent.extras?.getSerializable(EXTRA_MOVIE_DATA) as? MovieData

        // Update UI with the selected movie data
        movieData?.let {
            binding.etMovieTitle.setText(it.title)
            Glide.with(this)
                .load(it.imageUrl)
                .into(binding.movieImagePreview)
        }

        // Update UI with the selected movie data
        movieData?.let {
            binding.etMovieTitle.setText(it.title)
            Glide.with(this)
                .load(it.imageUrl)
                .into(binding.movieImagePreview)
        }

        // Implement your edit functionality and update data to Firebase
        binding.btnEdit.setOnClickListener {
            // Get the updated values from UI
            val updatedTitle = binding.etMovieTitle.text.toString()

            // Update the movieData object with the new values
            movieData?.let {
                it.title = updatedTitle
            }

            // Perform the update in Firebase
            updateDataInFirebase(movieData)
        }

        // Rest of the code...
    }

    private fun updateDataInFirebase(movieData: MovieData?) {
        movieData?.let {
            val databaseReference = FirebaseDatabase.getInstance("https://askarcinema-cd517-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference.child("movies").child(it.movieId ?: "")

            // Update the values in Firebase
            databaseReference.setValue(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "Data updated successfully")
                    // Finish the activity or perform any other action
                    finish()
                } else {
                    Log.e("Firebase", "Error updating data", task.exception)
                }
            }
        }
    }
}
