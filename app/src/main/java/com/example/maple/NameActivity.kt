package com.example.maple

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.maple.databinding.ActivityNameBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class NameActivity : Activity() {
    // Binding
    private var _binding: ActivityNameBinding? = null
    private val binding get() = _binding!!

    // Db
    private var db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val uid = auth.currentUser?.uid.toString()

    // For debug, TAG to know which fragment ur currently in
    private companion object {
        private const val TAG = "NameActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user input name
        val name = binding.editTextName
        // Check if name exists
        val btnSubmit = binding.btnConfirm
        // When click on button check if name exist otherwise move to main
        btnSubmit.setOnClickListener {
            db.collection("users").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get list of existing names
                    val names = mutableListOf<String>()
                    for (doc in task.result!!) {
                        names.add(doc.getString("name").toString())
                    }
                    // If name exists then clear
                    if (names.contains(name.text.toString())) {
                        name.text?.clear()
                        name.hint = "Tên đã có"
                    } else {
                        Log.d("name test", name.text.toString())
                        Log.d("email null?", uid)
                        db.collection("users").document(uid)
                            .set(hashMapOf("name" to name.text.toString()))
                            .addOnSuccessListener {
                                Log.d("Something", "DocumentSnapshot successfully written!")
                                startActivity(Intent(this@NameActivity, MainActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.w(
                                    "Something",
                                    "Error writing document",
                                    e
                                )
                            }
                    }
                }
            }
        }
    }
}