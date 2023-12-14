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

                                // Navigate to HomeActivity or AdminActivity based on userType
                                navigateToHomeOrAdmin(role)
                            } else {
                                Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }

    private fun saveUserDataToFirestore(account: Account) {
        firestore.collection("accounts")
            .add(account)
            .addOnSuccessListener { documentReference ->
                Log.d("MainActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("MainActivity", "Error adding document", e)
                Toast.makeText(
                    requireContext(),
                    "Error adding document to Firestore: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    private fun navigateToHomeOrAdmin(userType: String) {
        val intent = if (userType == "admin") {
            Intent(requireContext(), AdminActivity::class.java)
        } else {
            Intent(requireContext(), HomeActivity::class.java)
        }

        startActivity(intent)
        requireActivity().finish()  // Finish the hosting activity to prevent going back to the login/signup screen
    }
}
