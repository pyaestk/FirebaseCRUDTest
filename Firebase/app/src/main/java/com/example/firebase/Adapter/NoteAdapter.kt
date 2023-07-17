package com.example.firebase.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.Model.Notes
import com.example.firebase.UpdateNoteActivity
import com.example.firebase.databinding.NoteItemsBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NoteAdapter(
    var context: Context,
    var noteList: ArrayList<Notes>
): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myReference: DatabaseReference = database.reference.child("Notes")

    inner class NoteViewHolder(val adapterBinding: NoteItemsBinding)
        : RecyclerView.ViewHolder(adapterBinding.root) {

        init {
            adapterBinding.removeList.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    
                    removeNote(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {

        val binding = NoteItemsBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return NoteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.adapterBinding.textViewTitle.text = noteList[position].title
        holder.adapterBinding.textViewDescription.text = noteList[position].description

        if(holder.adapterBinding.textViewDescription.text.isEmpty()){
            holder.adapterBinding.textViewDescription.visibility = View.GONE
        } else {
            holder.adapterBinding.textViewDescription.visibility = View.VISIBLE
        }

        holder.adapterBinding.cardView.setOnClickListener{
            val intent = Intent(context, UpdateNoteActivity::class.java)
            intent.putExtra("id", noteList[position].noteId)
            intent.putExtra("title", noteList[position].title)
            intent.putExtra("description", noteList[position].description)
            context.startActivity(intent)
        }




    }

    fun getNoteId(position: Int): String {
        return noteList[position].noteId
    }

    fun removeNote(position: Int) {
        val note = noteList[position]
        val noteId = note.noteId

        // Remove the note from the list
        noteList.removeAt(position)
        // Notify the adapter about the removed item
        notifyItemRemoved(position)

        // Delete the note from the database
        myReference.child(noteId).removeValue()

        Toast.makeText(context, "Note has been deleted", Toast.LENGTH_SHORT).show()
    }


}