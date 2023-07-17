package com.example.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.example.firebase.databinding.ActivityUpdateNoteBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class UpdateNoteActivity : AppCompatActivity() {

    lateinit var updateNoteActivity: ActivityUpdateNoteBinding

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myReference: DatabaseReference = database.reference.child("Notes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateNoteActivity = ActivityUpdateNoteBinding.inflate(layoutInflater)
        val view = updateNoteActivity.root
        setContentView(view)

        supportActionBar?.title = "Update Note"

        getAndSetData()

        updateNoteActivity.buttonUpdate.setOnClickListener {
            updateNote()
        }

        updateNoteActivity.buttonCancel.setOnClickListener {
            finish()
        }
    }

    fun getAndSetData() {
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")

        updateNoteActivity.editTextUpdateTitle.setText(title)
        updateNoteActivity.editTextUpdateDescription.setText(description)

    }

    fun updateNote() {
        val titleUpdate = updateNoteActivity.editTextUpdateTitle.text.toString()
        val desUpdate = updateNoteActivity.editTextUpdateDescription.text.toString()
        val noteId = intent.getStringExtra("id").toString()

        val noteMap = mutableMapOf<String, Any>()

        noteMap["noteId"] = noteId
        noteMap["title"] = titleUpdate
        noteMap["description"] = desUpdate

        myReference.child(noteId).updateChildren(noteMap).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Toast.makeText(applicationContext,"List has been updated", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(applicationContext,task.exception.toString(), Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }
}