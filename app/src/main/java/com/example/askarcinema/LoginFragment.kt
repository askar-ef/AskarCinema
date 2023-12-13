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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

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

        with(binding) {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    if ("$email@gmail.com" == "admin@gmail.com" && password == "adminadmin"){
                        Toast.makeText(requireContext(), "bisa harusnya", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), AdminActivity::class.java)
                        startActivity(intent)

                    } else {
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = firebaseAuth.currentUser?.uid

                                    // Save login status to SharedPreferences
                                    saveLoginStatus(true)

                                    // Check user type and navigate accordingly
                                    checkUserTypeAndNavigate(uid)
                                    val intent = Intent(requireContext(), UserActivity::class.java)
                                    startActivity(intent)
//                                Toast.makeText(requireContext(), "Pass Benar", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Login failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            }

//            btnToSignup.setOnClickListener {
//                val intent = Intent(requireContext(), SignUpActivity::class.java)
//                startActivity(intent)
//            }
        }
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()  // Pastikan untuk memanggil apply() setelah melakukan perubahan
    }


    private fun checkUserTypeAndNavigate(uid: String?) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        uid?.let {
            databaseReference.child(it).child("userType").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userType = dataSnapshot.value as? String

                    Toast.makeText(requireContext(), "aman", Toast.LENGTH_SHORT).show()

                    val intent = when (userType) {
                        "admin" -> Intent(requireContext(), HomeActivity::class.java)
                        else -> Intent(requireContext(), HomeActivity::class.java)
                    }

                    startActivity(intent)
                    requireActivity().finish()  // Finish the hosting activity to prevent going back to the login screen
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                }
            })
        }
    }
}
