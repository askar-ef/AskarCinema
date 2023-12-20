package com.example.askarcinema

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.askarcinema.roomDatabase.MovieEntity
class MovieAdapter(
    private val movieList: MutableList<MovieEntity>,
    private val clickListener: (String) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txt_movie_title)
        val image: ImageView = itemView.findViewById(R.id.img_movie_photo)
        val genre: TextView = itemView.findViewById(R.id.txt_movie_genre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentItem = movieList[position]

        holder.title.text = currentItem.title
        holder.genre.text = currentItem.genre

        // Handle item click
        holder.itemView.setOnClickListener {
            clickListener.invoke((currentItem.id ?: "").toString())
        }

        // Use Glide or Picasso to load the image from the URL into the ImageView
        Glide.with(holder.itemView.context)
            .load(currentItem.imageUrl)
            .into(holder.image)
    }

    override fun getItemCount() = movieList.size
}
