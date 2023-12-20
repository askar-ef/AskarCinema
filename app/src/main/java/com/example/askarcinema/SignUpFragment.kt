package com.example.askarcinema

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.askarcinema.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firestore: FirebaseFirestore
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        firestore = FirebaseFirestore.getInstance()

        // Inisialisasi NotificationHelper
        notificationHelper = NotificationHelper(requireContext())

        with(binding) {
            btnSignup.setOnClickListener {
                val email = etEmail.text.toString()
                val fullname = etFullname.text.toString()
                val username = etUsername.text.toString()
                val password = etPassword.text.toString()
                val role = "user"
                val newAccount = Account(email, fullname, username, password, role)

                if (email.isNotEmpty() && fullname.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Save user data to Firestore
                                saveUserDataToFirestore(newAccount)

                                // Save login status to SharedPreferences
                                saveLoginStatus(true)

                                // Tampilkan notifikasi
                                notificationHelper.showNotification(
                                    "Selamat!",
                                    "Hi! Selamat Bergabung!"
                                )

                                val intent = Intent(requireContext(), UserActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    task.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }

    private fun saveUserDataToFirestore(account: Account) {
        // Implementasi menyimpan data ke Firestore
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }
}