package com.example.askarcinema.roomDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "movies")
//data class MovieEntity(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    val title: String,
//    val imageUrl: String
//)

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "imageUrl")
    val imageUrl: String
) {
    // Add a no-argument constructor for Firebase deserialization
    constructor() : this(0, "", "")
}
