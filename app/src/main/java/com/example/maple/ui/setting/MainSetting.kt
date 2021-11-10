package com.example.maple.ui.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.maple.Login
import com.example.maple.R
import com.example.maple.databinding.MainSettingFragmentBinding
import com.example.maple.ui.score.MainScore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainSetting : Fragment() {

    //Binding
    private lateinit var _binding: MainSettingFragmentBinding
    private val binding get() = _binding

    //db
    private var db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val email = auth.currentUser?.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = MainSettingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set name
        db.collection("users").document(auth.uid!!).addSnapshotListener { value, error ->
            //Error handling
            if (error != null) {
                Log.w(MainScore.TAG, "listen:error", error)
                return@addSnapshotListener
            }
            binding.userNameSetting.text = value?.get("name").toString()
        }

        //
        binding.userInformationSetting.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_mainSetting_to_userInformationSetting2)
        }
        //
        binding.setting.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainSetting_to_settingSetting)
        }
        //
        binding.applicationLoggingOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(context, Login::class.java)
            startActivity(intent)
        }

    }
}