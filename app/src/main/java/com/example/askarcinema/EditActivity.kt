package com.example.askarcinema

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.askarcinema.databinding.ActivityEditBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
class EditActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MOVIE_DATA = "extra_movie_data"
    }

    private lateinit var binding: ActivityEditBinding
    private var selectedImageUri: Uri? = null

    private val genreOptions = arrayOf(
        "Choose Genre", "Action", "Comedy", "Drama", "Romance",
        "Science Fiction", "Horror", "Mystery", "Adventure", "Thriller"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan data film dari intent
        val movieData = intent.extras?.getSerializable(EXTRA_MOVIE_DATA) as? MovieData

        // Mengatur nilai default untuk field-field yang ada
        movieData?.let {
            binding.etMovieTitle.setText(it.title)
            binding.etMovieDesc.setText(it.desc)
            binding.spinnerMovieGenre.setSelection(getGenreIndex(it.genre))
            Glide.with(this)
                .load(it.imageUrl)
                .into(binding.movieImagePreview)
        }

        // Membuat adapter untuk spinner
        val genreAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genreOptions)
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMovieGenre.adapter = genreAdapter

        // Memastikan bahwa spinner dipilih sesuai dengan data yang ada
        movieData?.let {
            val genreIndex = getGenreIndex(it.genre)
            if (genreIndex != -1) {
                binding.spinnerMovieGenre.setSelection(genreIndex, false)
            }
        }

        // Implementasi fungsi tombol Edit
        binding.btnEdit.setOnClickListener {
            movieData?.let {
                // Mendapatkan nilai terbaru dari UI
                val updatedTitle = binding.etMovieTitle.text.toString()
                val updatedDesc = binding.etMovieDesc.text.toString()

                // Memperbarui objek movieData dengan nilai baru
                it.title = updatedTitle
                it.desc = updatedDesc

                // Memeriksa apakah gambar baru dipilih
                selectedImageUri?.let { uri ->
                    // Menyimpan URL gambar lama
                    val oldImageUrl = it.imageUrl

                    // Menghapus gambar sebelumnya dari penyimpanan
                    deleteFromStorage(oldImageUrl)

                    // Mengunggah gambar baru dan memperbarui movieData dengan URL gambar baru
                    checkAndUploadImage(uri) { imageUrl ->
                        it.imageUrl = imageUrl
                        // Melakukan pembaruan di Firebase
                        updateDataInFirebase(movieData)
                    }
                } ?: run {
                    // Tidak ada gambar baru yang dipilih, melakukan pembaruan tanpa mengubah gambar
                    updateDataInFirebase(movieData)
                }
            }
        }

        // Implementasi fungsi tombol Delete
        binding.btnDelete.setOnClickListener {
            movieData?.let {
                // Menghapus data dari Firebase
                deleteDataFromFirebase(movieData)
            }
        }

        // Implementasi fungsi pemilihan gambar
        binding.btnChooseImage.setOnClickListener {
            openGallery()
        }
    }

    // Fungsi pembaruan data di Firebase
    private fun updateDataInFirebase(movieData: MovieData) {
        val databaseReference = FirebaseDatabase.getInstance("https://askarcinema-cd517-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("movies").child(movieData.movieId ?: "")

        // Memperbarui nilai di Firebase
        databaseReference.setValue(movieData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "Data updated successfully")
                finish()
            } else {
                Log.e("Firebase", "Error updating data", task.exception)
            }
        }
    }

    // Fungsi penghapusan data dari Firebase
    private fun deleteDataFromFirebase(movieData: MovieData) {
        val databaseReference = FirebaseDatabase.getInstance("https://askarcinema-cd517-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("movies").child(movieData.movieId ?: "")

        databaseReference.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "Data deleted successfully")
                // Menghapus gambar dari penyimpanan
                deleteFromStorage(movieData.imageUrl)
                showToast("Data deleted successfully")
                finish()
            } else {
                Log.e("Firebase", "Error deleting data", task.exception)
                showToast("Error deleting data")
            }
        }
    }

    // Fungsi penghapusan gambar dari penyimpanan
    private fun deleteFromStorage(imageUrl: String) {
        showToast(imageUrl)
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageReference.delete().addOnSuccessListener {
            Log.d("Firebase", "Image deleted from Storage")
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Error deleting image from Storage", exception)
        }
    }

    // Fungsi menampilkan pesan Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Fungsi pilihan gambar dari galeri
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imageActivityResult.launch(intent)
    }

    // Fungsi pengaturan pemilihan gambar
    private val imageActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    binding.movieImagePreview.setImageURI(selectedImageUri)
                }
            }
        }

    // Fungsi mendapatkan indeks genre
    private fun getGenreIndex(genre: String): Int {
        return genreOptions.indexOf(genre)
    }

    // Fungsi pengunggahan gambar dan mendapatkan URL gambar
    private fun checkAndUploadImage(imageUri: Uri, callback: (String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("images/${System.currentTimeMillis()}_image.jpg")

        // Melanjutkan proses pengunggahan
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Gambar berhasil diunggah
                getImageUrlAndSaveData(imageRef, callback)
            }
            .addOnFailureListener { exception ->
                // Penanganan kesalahan selama pengunggahan gambar
                Log.e("Firebase", "Failed to upload image", exception)
            }
    }

    // Fungsi mendapatkan URL gambar setelah diunggah
    private fun getImageUrlAndSaveData(imageRef: StorageReference, callback: (String) -> Unit) {
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            // File berhasil diunggah, mendapatkan URL gambar
            val imageUrl = uri.toString()
            callback(imageUrl)
        }
    }
}