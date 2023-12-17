package com.example.askarcinema

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.askarcinema.databinding.FragmentHomeBinding
import com.example.askarcinema.roomDatabase.MovieEntity
import com.google.firebase.database.*

// ... existing imports ...

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movieList: MutableList<MovieEntity>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieList = mutableListOf()
        movieAdapter = MovieAdapter(movieList)

        val recyclerView: RecyclerView = binding.filmRecyclerView
        recyclerView.adapter = movieAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Fetch data from Firebase and update movieList
        fetchMoviesFromFirebase()
    }

    private fun fetchMoviesFromFirebase() {
        val databaseReference = FirebaseDatabase.getInstance("https://askarcinema-cd517-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("movies")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the existing data
                movieList.clear()

                // Iterate through the snapshot and add MovieData objects to movieList
                for (dataSnapshot in snapshot.children) {
                    val movieEntity = dataSnapshot.getValue(MovieEntity::class.java)
                    movieEntity?.let { movieList.add(it) }
                }

                // Notify the adapter that the data has changed
                movieAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, if any
                Log.e("Firebase", "Error fetching movies from Firebase", error.toException())
            }
        })
    }
}
