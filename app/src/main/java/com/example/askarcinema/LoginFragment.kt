package com.example.askarcinema

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.askarcinema.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        firestore = FirebaseFirestore.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            fetchUserRole(email)
                        } else {
                            Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchUserRole(email: String) {
        firestore.collection("accounts")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val role = documents.documents[0]["role"] as String
                    saveUserRole(role)
                    navigateToRoleSpecificActivity(role)
                } else {
                    Toast.makeText(requireContext(), "User data not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.w("LoginFragment", "Error getting documents.", e)
                Toast.makeText(requireContext(), "Error getting user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserRole(role: String) {
        val editor = sharedPreferences.edit()
        editor.putString("userRole", role)
        editor.apply()
    }

    private fun navigateToRoleSpecificActivity(role: String) {
        when (role) {
            "user" -> {
                saveLoginStatus(true)
                val intent = Intent(requireContext(), UserActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            "admin" -> {
                val intent = Intent(requireContext(), MonitorActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            else -> {
                Toast.makeText(requireContext(), "Invalid user role.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }
}