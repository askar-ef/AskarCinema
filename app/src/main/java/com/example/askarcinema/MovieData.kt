package com.example.askarcinema

import java.io.Serializable

data class MovieData(
    var title: String = "",
    var imageUrl: String = "",
    var genre: String = "",
    var desc: String = "",
    var movieId: String? = null
) : Serializable
