package com.example.askarcinema

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.askarcinema.databinding.FragmentMonitorBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class MonitorFragment : Fragment(), MovieAdapter.OnItemLongClickListener {

    private lateinit var binding: FragmentMonitorBinding
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movieList: MutableList<MovieData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMonitorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieList = mutableListOf()
        movieAdapter = MovieAdapter(movieList, this)

        val recyclerView: RecyclerView = binding.filmRecyclerView
        recyclerView.adapter = movieAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Fetch data from Firebase and update movieList
        fetchMoviesFromFirebase()
    }



    override fun onItemLongClick(movieData: MovieData) {
        // Hapus data dari Firebase dan Storage
        deleteMovieData(movieData)
    }

    private fun deleteMovieData(movieData: MovieData) {
//        val databaseReference = FirebaseDatabase.getInstance("https://askarcinema-cd517-default-rtdb.asia-southeast1.firebasedatabase.app/")
//            .reference.child("movies").child(movieData.movieId)

        val movieId = movieData.movieId ?: return // or handle the null case appropriately
        val databaseReference = FirebaseDatabase.getInstance("https://askarcinema-cd517-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("movies").child(movieId)


        databaseReference.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Hapus data dari Firebase Storage
                deleteFromStorage(movieData.imageUrl)
            } else {
                Log.e("Firebase", "Error deleting movie data", task.exception)
            }
        }
    }

    private fun deleteFromStorage(imageUrl: String) {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageReference.delete().addOnSuccessListener {
            // Item berhasil dihapus dari Storage
            // Refresh data di RecyclerView jika diperlukan
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
                // Clear the existing data
                movieList.clear()

                // Iterate through the snapshot and add MovieData objects to movieList
                for (dataSnapshot in snapshot.children) {
                    val movieData = dataSnapshot.getValue(MovieData::class.java)
                    movieData?.let {
                        it.movieId = dataSnapshot.key // Set movieId from Firebase key
                        movieList.add(it)
                    }
                }
                movieAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, if any
                Log.e("Firebase", "Error fetching movies from Firebase", error.toException())
            }
        })
    }
}
