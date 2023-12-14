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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var databaseReference: DatabaseReference

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
        databaseReference = FirebaseDatabase.getInstance("https://askarcinema-cd517-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("accounts")

        with(binding) {
            btnSignup.setOnClickListener {
                val email = etEmail.text.toString()
                val fullname = etFullname.text.toString()
                val username = etUsername.text.toString()
                val password = etPassword.text.toString()
                val role = "user"
                val newAccount = Account("", email, fullname, username, password, role)

                if (email.isNotEmpty() && fullname.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val accountId = databaseReference.push().key
                                if (accountId != null) {
                                    newAccount.id = accountId
                                    addAccount(newAccount)

                                    // Save login status to SharedPreferences
                                    saveLoginStatus(true)

                                    // Navigate to HomeActivity or AdminActivity based on userType
                                    navigateToHomeOrAdmin(role)
                                } else {
                                    Log.d("MainActivity", "Error generating account ID")
                                }
                            } else {
                                Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }

    private fun addAccount(account: Account) {
        val accountReference = databaseReference.child(account.id)
        accountReference.setValue(account)
            .addOnSuccessListener {
                Log.d("MainActivity", "Account added successfully: $account")
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Error adding account: ", it)
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
