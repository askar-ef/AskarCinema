package com.example.askarcinema

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.askarcinema.databinding.ActivityUploadBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    // Daftar opsi genre
    private val genreOptions = arrayOf(
        "Choose Genre", "Action", "Comedy", "Drama", "Romance",
        "Science Fiction", "Horror", "Mystery", "Adventure", "Thriller"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi adapter untuk spinner
        val genreAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genreOptions)
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set adapter ke spinner
        binding.spinnerMovieGenre.adapter = genreAdapter

        with(binding) {
            btnChooseImage.setOnClickListener {
                openGallery()
            }

            btnUpload.setOnClickListener {
                selectedImageUri?.let { uri ->
                    checkAndUploadImage(uri)
                } ?: run {
                    Toast.makeText(this@UploadActivity, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            binding.movieImagePreview.setImageURI(selectedImageUri)
        }
    }

    private fun checkAndUploadImage(imageUri: Uri) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("images/${System.currentTimeMillis()}_image.jpg")

        // Continue with the upload process
        uploadImageToFirebaseStorage(imageUri, imageRef)
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, imageRef: StorageReference) {
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Image successfully uploaded
                getImageUrlAndSaveData(imageRef)
            }
            .addOnFailureListener { exception ->
                // Handling error during image upload
                Log.e("Firebase", "Failed to upload image", exception)
            }
    }

    private fun getImageUrlAndSaveData(imageRef: StorageReference) {
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            // File successfully uploaded, get the image URL
            val imageUrl = uri.toString()
            val movieTitle = binding.etMovieTitle.text.toString()
            val movieGenre = binding.spinnerMovieGenre.selectedItem.toString()
            val movieDesc = binding.etMovieDesc.text.toString()

            // Save data to Firebase Database (Realtime Database or Firestore)
            saveDataToFirebaseDatabase(movieTitle, imageUrl, movieGenre, movieDesc)
        }
    }

    private fun saveDataToFirebaseDatabase(movieTitle: String, imageUrl: String, movieGenre: String, movieDesc: String) {
        val databaseReference = FirebaseDatabase.getInstance("https://askarcinema-cd517-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("movies")

        val movieData = MovieData(movieTitle, imageUrl, movieGenre, movieDesc)

        // Save data to the 'movies' node
        databaseReference.push().setValue(movieData)
            .addOnSuccessListener {
                // Data successfully saved
                Toast.makeText(this@UploadActivity, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()

                // After success, call the method to clear the form
                clearForm()
            }
            .addOnFailureListener { exception ->
                // Handling error when saving data
                Log.e("Firebase", "Failed to save data to Firebase Database", exception)
            }
    }

    private fun clearForm() {
        // Clear the form after successful upload
        binding.etMovieTitle.text.clear()
        binding.etMovieDesc.text.clear()
        binding.movieImagePreview.setImageURI(null)
        binding.spinnerMovieGenre.setSelection(0)
        selectedImageUri = null
    }
}
