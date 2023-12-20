package com.example.askarcinema

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.askarcinema.databinding.ActivityDetailBinding
import com.example.askarcinema.roomDatabase.MovieEntity
import com.bumptech.glide.Glide
import com.example.askarcinema.roomDatabase.MovieDao
import com.example.askarcinema.roomDatabase.MovieRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MOVIE_ID = "extra_movie_id"
    }

    private lateinit var binding: ActivityDetailBinding
    private lateinit var movieDao: MovieDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getStringExtra(EXTRA_MOVIE_ID)

        // Initialize Room Database
        val appDatabase = MovieRoomDatabase.getDatabase(this)
        movieDao = appDatabase.movieDao()

        // Use the movieId to get data from Room Database or Firebase (implement as needed)
        CoroutineScope(Dispatchers.IO).launch {
            val movieData = movieId?.let { movieDao.getMovieById(it) }
            withContext(Dispatchers.Main) {
                movieData?.let {
                    binding.txtMovieTitle.text = it.title
                    binding.txtMovieGenre.text = it.genre
                    binding.txtMovieDesc.text = it.desc

                    Glide.with(this@DetailActivity)
                        .load(it.imageUrl)
                        .into(binding.imgMoviePhoto)
                }
            }
        }
    }
}