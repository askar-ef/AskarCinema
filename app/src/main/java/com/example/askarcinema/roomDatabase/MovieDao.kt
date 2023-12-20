package com.example.askarcinema.roomDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow



@Dao
interface MovieDao {

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: String): MovieEntity?


    @Query("SELECT * FROM movies")
    fun getAllMovies(): LiveData<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: MutableList<com.example.askarcinema.roomDatabase.MovieEntity>)

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)


    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()
}

