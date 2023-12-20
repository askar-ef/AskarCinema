package com.example.askarcinema

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.askarcinema.databinding.FragmentHomeBinding
import com.example.askarcinema.roomDatabase.MovieDao
import com.example.askarcinema.roomDatabase.MovieEntity
import com.example.askarcinema.roomDatabase.MovieRoomDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoviesFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movieList: MutableList<MovieEntity>

    private lateinit var movieDao: MovieDao
    private lateinit var offlineMoviesObserver: Observer<List<MovieEntity>>

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

        val recyclerView: RecyclerView = binding.filmRecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Initialize Room Database
        val appDatabase = MovieRoomDatabase.getDatabase(requireContext())
        movieDao = appDatabase.movieDao()

        // Set up observer for offline movies
        offlineMoviesObserver = Observer<List<MovieEntity>> { offlineMovies ->
            // Clear the existing data
            movieList.clear()
            movieList.addAll(offlineMovies)

            // Notify the adapter that the data has changed
            movieAdapter.notifyDataSetChanged()
        }

        // Observe changes in offline movies
        movieDao.getAllMovies().observe(viewLifecycleOwner, offlineMoviesObserver)

        // Set up the adapter with the click listener
        movieAdapter = MovieAdapter(movieList) { movieId ->
            // Handle movie click
            navigateToDetailActivity(movieId)
        }

        recyclerView.adapter = movieAdapter

        // Fetch and display data based on connectivity
        fetchDataBasedOnConnectivity()
    }

    private fun fetchDataBasedOnConnectivity() {
        if (isNetworkAvailable()) {
            fetchMoviesFromFirebase()
            Toast.makeText(requireContext(), "Movies Updated", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Connection Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun fetchMoviesFromFirebase() {
        val databaseReference = FirebaseDatabase.getInstance(
            "https://askarcinema-cd517-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).reference.child("movies")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the existing data
                movieList.clear()

                val onlineMovies = mutableListOf<MovieEntity>()
                for (dataSnapshot in snapshot.children) {
                    val movieData = dataSnapshot.getValue(MovieEntity::class.java)
                    movieData?.let {
                        movieList.add(it)
                        onlineMovies.add(it)
                    }
                }

                movieAdapter.notifyDataSetChanged()

                CoroutineScope(Dispatchers.IO).launch {
                    movieDao.deleteAllMovies()
                    movieDao.insertMovies(onlineMovies)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching movies from Firebase", error.toException())
            }
        })
    }

    private fun navigateToDetailActivity(selectedMovieId: String) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_MOVIE_ID, selectedMovieId)
        startActivity(intent)
    }
}