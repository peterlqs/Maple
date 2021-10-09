package com.example.maple.ui.mic

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maple.R
import com.example.maple.data.Message
import com.example.maple.databinding.MainMicFragmentBinding
import com.example.maple.hideKeyboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class MainMic : Fragment() {
    // Db and user
    private val ref = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser?.uid


    // Binding
    private lateinit var _binding: MainMicFragmentBinding
    private val binding get() = _binding

    // Set TAG to easily recognize which fragment when debug
    companion object {
        const val TAG = "MainMic"
        private const val REQUEST_CODE_STT = 1

    }

    // Inflate
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainMicFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // A list to contain all Message object ( the messeage, user ( the user or bot ) )
        val messageList = ArrayList<Message>()
        // Adapter for recycler view .let is like MessageAdapter(context, messageList) with null proof
        val messageAdapter = context?.let { MessageAdapter(it, messageList) }

        // Set up recycler view
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = messageAdapter

        // Scroll to bottom of recycler view by scroll to element of list's size -1 ( last element )
        messageAdapter?.itemCount?.minus(1)?.let { binding.recyclerView.scrollToPosition(it) }

        // Firebase listener
        ref.child("chats").child(user!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    // Clear messageList otherwise it would get duplicated
                    messageList.clear()

                    // For each message, add to messageList
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }

                    // Scroll to bottom like Messenger ( explanation above )
                    binding.recyclerView.scrollToPosition(messageList.size - 1)

                    // Tell recycler view to change, can you remove the warning pls
                    messageAdapter?.notifyDataSetChanged()
                }

                // Catch error
                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Failed to get data from Firebase")
                }
            })

        // Recycler view scroll to bottom even when layout change ( keyboard show up when input )
        // i8 is old bottom position, i4 is current bottom position
        binding.recyclerView.addOnLayoutChangeListener { _, _, _, _, i4, _, _, _, i8 ->
            if (i4 < i8) {
                binding.recyclerView.scrollBy(0, i8 - i4)
            }
        }

        // When hit enter button, setOnKeyListener is like onClickListener but for key
        binding.editTextChat.setOnKeyListener { _, keyCode, event ->
            // If the key is enter AND user press on the enter key
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                // When enter it create an extra \n so remove that
                val message = binding.editTextChat.text.toString().replace("\n", "")
                // If message not blank then send to Firebase
                if (message != "") sendText(message)
            }
            // If not enter then do nothing
            false
        }

        // Submit button
        binding.imageButton.setOnClickListener {
            // Remove \n just in case
            val message = binding.editTextChat.text.toString().replace("\n", "")
            // If message not blank then send to Firebase
            // TODO UNCOMMENT BELOW WHEN DONE
//            if (message != "") sendText(message)
            Navigation.findNavController(view).navigate(R.id.action_mainMic_to_mainExperiment)
        }

        //Mic ( CURRENTLY NOT WORKING )
        binding.btnMic.setOnClickListener {
            getSpeechInput()
            // For some reason after speech recognition it shows keyboard, so hide it
            hideKeyboard(view)
        }


    }

    // Send message to Firebase
    private fun sendText(text: String) {
        // Turn message into an object ( asign user(uid) to know if it's bot or user )
        val messageObject = Message(text, user)

        // This is realtime db not Firestore cuz realtime doesn't charge on read but on the amount of storage ( kb,mb,... )
        ref.child("chats").child(user!!).child("messages").push()
            .setValue(messageObject).addOnSuccessListener {
                // Clear text input
                binding.editTextChat.setText("")
                Log.d(TAG, "Success add to realtime")
            }

        //Demo bot, current it just say hi im a bot
        val botObject = Message("Hi im a bot", "bot")
        ref.child("chats").child(user).child("messages").push()
            .setValue(botObject).addOnSuccessListener {
                binding.editTextChat.setText("")
                Log.d(TAG, "Success add to realtime BOT")
            }
    }

    //------------------------
    // IGNORE ALL CODES BELOW
    //------------------------

    private fun getSpeechInput() {
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        // Language model defines the purpose, there are special models for other use cases, like search.
        sttIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        // Adding an extra language, you can use any language from the Locale class.
        sttIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
//            Locale.getDefault()
            "vi"
        )
        // Text that shows up on the Speech input prompt.
        sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Bạn hãy nói đi")
        try {
            // Start the intent for a result, and pass in our request code.
            startActivityForResult(sttIntent, REQUEST_CODE_STT)
        } catch (e: ActivityNotFoundException) {
            // Handling error when the service is not available.
            e.printStackTrace()
            Toast.makeText(context, "Your device does not support STT.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // Handle the result for our request code.
            REQUEST_CODE_STT -> {
                // Safety checks to ensure data is available.
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // Retrieve the result array.
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    // Ensure result array is not null or empty to avoid errors.
                    if (!result.isNullOrEmpty()) {
                        // Recognized text is in the first position.
                        val recognizedText = result[0]
                        // Do what you want with the recognized text.
//                        binding.editTextChat.setText(recognizedText)
                        sendText(recognizedText)

                    }
                }
            }
        }
    }

    private val textToSpeechEngine: TextToSpeech by lazy {
        // Pass in context and the listener.
        TextToSpeech(context,
            TextToSpeech.OnInitListener { status ->
                // set our locale only if init was success.
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale.ENGLISH
                }
            })
    }

    override fun onPause() {
        textToSpeechEngine.stop()
        super.onPause()
    }

    override fun onDestroy() {
        textToSpeechEngine.shutdown()
        super.onDestroy()
    }
}
