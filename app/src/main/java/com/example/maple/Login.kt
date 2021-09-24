package com.example.maple

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.maple.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.util.*


class Login : Activity() {
    private var _binding: ActivityLoginBinding? = null
    val binding get() = _binding!!

    //Stuff for sign in with google.
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    // Initialize cloud firestore...
    val db:FirebaseFirestore= FirebaseFirestore.getInstance()

    //Constants
    private companion object {
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = Firebase.auth
        val currentUser = auth.currentUser
        updateUI(currentUser)
        //sign in before ?
        if (auth.currentUser != null) {
            //Go to main activity
            startActivity(Intent(this@Login, MainActivity::class.java))
            finish()
        }
        //Set sign in button ( google sign in )
        binding.signInBtn.setOnClickListener {
            Log.d(TAG, "OnCreate, ggl sign in")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }

        //Anonymous sign in
        binding.signInBtn2.setOnClickListener {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success")
                        val user = auth.currentUser
                        updateUI(user)
                        startActivity(Intent(this@Login, MainActivity::class.java))
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }


    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    // [END on_start_check_user]
    // [START onactivityresult]

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)



                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }



                try {
                    //set time in mili
                    Thread.sleep(5000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                //Go to main activity
                startActivity(Intent(this@Login, MainActivity::class.java))
                finish()
            }
    }
    // [END auth_with_google]

    // [START signin] What is this for?
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    private fun updateUI(user: FirebaseUser?) {

    }

    private fun createNewUser(user: FirebaseUser?){

        //Initialize data
        val time:Calendar= Calendar.getInstance()
        val currentDate:String=DateFormat.getDateInstance().format(time.time)
        db.collection("users")
            .document(user?.email.toString())
            .set(hashMapOf(
                "status" to "created"
            ))
        //Initialize EXP and LVL
        db.collection("users")
            .document(user?.email.toString())
            .collection("PlayerEXP")
            .document("userScore")
            .set(hashMapOf(
                "yourEXP" to 0,
                "yourLVL" to 1
            ))

        //Initialize Achievement
        db.collection("users")
            .document(user?.email.toString())
            .collection("Achievement")
            .document("myAchievement")



    }

    private fun CheckAvailable(user:FirebaseUser?){
        db.collection("users").get()
            .addOnSuccessListener{
                var available:Boolean=false
                for (document in it) {
                    Log.e("user",document.id)
                    if (document.id==user?.email.toString()){
                        Log.e("available",document.id)

                        available=true
                        break
                    }
                }
                if (!available){
                    createNewUser(user)
                }
            }
            .addOnCanceledListener {
                Log.e("APP","Failed to log in")
            }
    }






}