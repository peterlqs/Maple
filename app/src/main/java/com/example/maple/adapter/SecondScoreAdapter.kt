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
    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scoreHere: TextView = itemView.findViewById(R.id.scoreHere)
        val monthHere: TextView = itemView.findViewById(R.id.dayText)
        val remove: ImageButton = itemView.findViewById(R.id.imageButton_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_second_score, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
//        val currentScore = scores[position]
//        val currentMonth = months[position]
//        holder.scoreHere.text = currentScore.toString()
//        holder.monthHere.text = currentMonth.toString()
        val currentScore = mulList[position].diem
        val currentMonth = mulList[position].thang
        holder.scoreHere.text = currentScore.toString()
        holder.monthHere.text = currentMonth.toInt().toString()
        holder.remove.setOnClickListener {
            remove(user, position)
        }
//        val currentScore = theScore[position]
//        val currentSubject = theSubject[position]
//        holder.subject.text = "$currentSubject : $currentScore"
//        holder.subject.setOnClickListener {
//            Log.d("adapter", currentSubject)
//            listener.subjectName(currentSubject, currentScore)
//            //for some reason it gets error when firestore listener run
//            fireStoreListener?.remove()
//            holder.itemView.findNavController().navigate(R.id.action_monhocResult_to_monhocDiem)
//        }
    }

    override fun getItemCount(): Int {
        return mulList.size
    }

    private fun remove(user: FirebaseUser?, position: Int) {
        Log.d("SDJLKA", mulList[position].toString())
        Log.d("SDJLKA", mulList[position].id.toString())
        Log.d("SDJLKA", user?.email.toString())

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