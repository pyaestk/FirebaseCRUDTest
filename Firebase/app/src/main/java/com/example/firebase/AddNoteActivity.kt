package com.example.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.firebase.Model.Notes
import com.example.firebase.databinding.ActivityAddNoteBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddNoteActivity : AppCompatActivity() {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myReference: DatabaseReference = database.reference.child("Notes")

    lateinit var addNoteBinding: ActivityAddNoteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addNoteBinding = ActivityAddNoteBinding.inflate(layoutInflater)
        val view = addNoteBinding.root
        setContentView(view)

        supportActionBar?.title = "Add Your Note"

        addNoteBinding.buttonAdd.setOnClickListener {

            if (addNoteBinding.editTextTitle.text.isEmpty()){
                Toast.makeText(applicationContext, "Title can't be null", Toast.LENGTH_SHORT).show()
            } else {
                addNoteToDatabase()
            }
            
        }
        addNoteBinding.buttonCancel.setOnClickListener {
            finish() //back to main activity
        }
    }

    fun addNoteToDatabase() {
        val title: String = addNoteBinding.editTextTitle.text.toString()
        val description: String = addNoteBinding.editTextDescription.text.toString()

        val id: String = myReference.push().key.toString()

        val note = Notes(id, title, description)

        myReference.child(id).setValue(note).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(applicationContext, "list has been added to database", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}