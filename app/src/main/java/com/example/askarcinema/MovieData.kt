package com.example.askarcinema

data class MovieData(
    var title: String = "",
    var imageUrl: String = "",
    var movieId: String? = null // Make movieId nullable
) {
    constructor() : this("", "", null)
}
