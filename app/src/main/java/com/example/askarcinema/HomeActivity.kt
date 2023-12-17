package com.example.askarcinema

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.askarcinema.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {
//    private lateinit var binding: FragmentHomeBinding
//    private lateinit var movieAdapter: MovieAdapter
//    private lateinit var movieList: MutableList<MovieData>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = FragmentHomeBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
//
//        movieList = mutableListOf()
//        movieAdapter = MovieAdapter(movieList)
//
//        val recyclerView: RecyclerView = binding.filmRecyclerView
//        recyclerView.adapter = movieAdapter
//        recyclerView.layoutManager = GridLayoutManager(this, 2)
//
//        // Fetch data from Firebase and update movieList
//        fetchMoviesFromFirebase()
//    }
//
//    private fun fetchMoviesFromFirebase() {
//        val databaseReference = FirebaseDatabase.getInstance("https://askarcinema-cd517-default-rtdb.asia-southeast1.firebasedatabase.app/")
//            .reference.child("movies")
//
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // Clear the existing data
//                movieList.clear()
//
//                // Iterate through the snapshot and add MovieData objects to movieList
//                for (dataSnapshot in snapshot.children) {
//                    val movieData = dataSnapshot.getValue(MovieData::class.java)
//                    movieData?.let { movieList.add(it) }
//                }
//
//                // Notify the adapter that the data has changed
//                movieAdapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle the error, if any
//                Log.e("Firebase", "Error fetching movies from Firebase", error.toException())
//            }
//        })
//    }
}
