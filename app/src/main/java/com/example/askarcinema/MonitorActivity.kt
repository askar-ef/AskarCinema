package com.example.askarcinema

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.askarcinema.databinding.ActivityMonitorBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class MonitorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMonitorBinding
    private lateinit var monitorAdapter: MonitorAdapter
    private lateinit var movieList: MutableList<MovieData>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        movieList = mutableListOf()

        val recyclerView: RecyclerView = binding.filmRecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Create the MonitorAdapter with onItemClick and onDeleteClick listeners
        monitorAdapter = MonitorAdapter(
            movieList,
            onItemClick = { movieData -> navigateToEditActivity(movieData) },
            onDeleteClick = { movieData -> deleteMovieData(movieData) }
        )

        recyclerView.adapter = monitorAdapter

        // Fetch data from Firebase and update movieList
        fetchMoviesFromFirebase()

        binding.btnLogout.setOnClickListener{
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()

            // Show a toast message
            Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show()


            val intent = Intent(this, LoginRegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.fabAddMovie.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToEditActivity(movieData: MovieData) {
        // Pass selected movie data to EditActivity using a Bundle
        val intent = Intent(this, EditActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(EditActivity.EXTRA_MOVIE_DATA, movieData)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun deleteMovieData(movieData: MovieData) {
        val movieId = movieData.movieId ?: return
        val databaseReference = FirebaseDatabase.getInstance("https://askarcinema-cd517-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("movies").child(movieId)

        databaseReference.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Delete data from Firebase Storage
                deleteFromStorage(movieData.imageUrl)
            } else {
                Log.e("Firebase", "Error deleting movie data", task.exception)
            }
        }
    }

    private fun deleteFromStorage(imageUrl: String) {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageReference.delete().addOnSuccessListener {
            fetchMoviesFromFirebase()
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Error deleting image from Storage", exception)
        }
    }

    private fun fetchMoviesFromFirebase() {
        val databaseReference = FirebaseDatabase.getInstance("https://askarcinema-cd517-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("movies")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear existing data
                movieList.clear()

                // Iterate through the snapshot and add MovieData objects to movieList
                for (dataSnapshot in snapshot.children) {
                    val movieData = dataSnapshot.getValue(MovieData::class.java)
                    movieData?.let {
                        it.movieId = dataSnapshot.key
                        movieList.add(it)
                    }
                }
                monitorAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, if any
                Log.e("Firebase", "Error fetching movies from Firebase", error.toException())
            }
        })
    }
}
