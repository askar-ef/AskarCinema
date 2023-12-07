package com.example.askarcinema

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.askarcinema.databinding.FragmentUploadBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UploadFragment : Fragment() {

    private lateinit var binding: FragmentUploadBinding
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnChooseImage.setOnClickListener {
                openGallery()
            }

            btnUpload.setOnClickListener {
                selectedImageUri?.let { uri ->
                    checkAndUploadImage(uri)
                } ?: run {
                    Toast.makeText(requireContext(), "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
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

        // Langsung lanjutkan proses upload
        uploadImageToFirebaseStorage(imageUri, imageRef)
    }


    private fun uploadImageToFirebaseStorage(imageUri: Uri, imageRef: StorageReference) {
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Gambar berhasil diunggah
                getImageUrlAndSaveData(imageRef)
            }
            .addOnFailureListener { exception ->
                // Penanganan kesalahan saat mengunggah gambar
                Log.e("Firebase", "Gagal mengunggah gambar", exception)
            }
    }

    private fun getImageUrlAndSaveData(imageRef: StorageReference) {
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            // File berhasil diunggah, dapatkan URL gambar
            val imageUrl = uri.toString()
            val movieTitle = binding.etMovieTitle.text.toString()

            // Simpan data ke Firebase Database (Realtime Database atau Firestore)
            saveDataToFirebaseDatabase(movieTitle, imageUrl)
        }
    }

    private fun saveDataToFirebaseDatabase(movieTitle: String, imageUrl: String) {
        val databaseReference = FirebaseDatabase.getInstance("https://askarcinema-cd517-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("movies")

        val movieData = MovieData(movieTitle, imageUrl)

        // Simpan data ke node 'movies'
        databaseReference.push().setValue(movieData)
            .addOnSuccessListener {
                // Data berhasil disimpan
                Toast.makeText(requireContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Penanganan kesalahan saat menyimpan data
                Log.e("Firebase", "Gagal menyimpan data di Firebase Database", exception)
            }
    }
}