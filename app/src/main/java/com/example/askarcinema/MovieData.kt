package com.example.askarcinema

//data class MovieData(val title: String, val imageUrl: String)

data class MovieData(
    var title: String = "",
    var imageUrl: String = ""
) {
    // Add a no-argument constructor
    constructor() : this("", "")
}
