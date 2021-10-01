package com.example.maple.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maple.R
import com.example.maple.data.Diem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SecondScoreAdapter(
    val scores: MutableList<Double>,
    val months: MutableList<Int>,
    private val mulList: MutableList<Diem>
) :
    RecyclerView.Adapter<SecondScoreAdapter.ViewHolder>() {
    // Db and user
    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Get all the text view and button id
        val scoreHere: TextView = itemView.findViewById(R.id.scoreHere)
        val monthHere: TextView = itemView.findViewById(R.id.dayText)
        val remove: ImageButton = itemView.findViewById(R.id.imageButton_delete)
    }

    // This part is like a requirement, dunno how to explain
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_second_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get current user
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        // Set the item's score and month
        val currentScore = mulList[position].diem
        val currentMonth = mulList[position].thang
        holder.scoreHere.text = currentScore.toString()
        holder.monthHere.text = currentMonth.toInt().toString()
        // Set remove button
        holder.remove.setOnClickListener {
            remove(user, position)
        }
    }

    //Get the size of list so the recycler view knows how much space it needs to make
    override fun getItemCount(): Int {
        return mulList.size
    }

    private fun remove(user: FirebaseUser?, position: Int) {
        // Remove the score
        db.collection("users")
            .document(user?.email.toString())
            .collection("subjectScores")
            .document(mulList[position].id)
            .delete()
            .addOnCompleteListener {
                mulList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemChanged(position, itemCount)
                Log.d("SecondAdapter", "Success")
            }

    }
}