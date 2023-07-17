package com.example.firebase

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.Adapter.NoteAdapter
import com.example.firebase.Model.Notes
import com.example.firebase.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityMainBinding

    val noteList = ArrayList<Notes>()
    lateinit var noteAdapter: NoteAdapter

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myReference: DatabaseReference = database.reference.child("Notes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        mainBinding.fab.setOnClickListener{
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        //abstract class
        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val id = noteAdapter.getNoteId(viewHolder.adapterPosition)
                myReference.child(id).removeValue()
                Toast.makeText(applicationContext, "Note has been deleted", Toast.LENGTH_SHORT).show()
            }

        }).attachToRecyclerView(mainBinding.recyclerView)

        noteAdapter = NoteAdapter(this, noteList)
        mainBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mainBinding.recyclerView.adapter = noteAdapter

        retrieveDataFromDatabase()
    }

    fun retrieveDataFromDatabase() {
        myReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                noteList.clear() // Clear the list before adding new data

                for (eachNote in snapshot.children) {
                    val note = eachNote.getValue(Notes::class.java)

                    if(note!=null) {
                        println("noteId: ${note.noteId}")
                        println("noteTitle: ${note.title}")
                        println("noteDescription: ${note.description}")
                        println("-----------------------------")

                        noteList.add(note)
                    }
                }

                // Notify the adapter that the data has changed (1st way)
                noteAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete, menu)
        return true 
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.deleteAll) {
            showDialogMessage()
        }

        return super.onOptionsItemSelected(item)
    }

    fun showDialogMessage() {
        val dialogMessage = AlertDialog.Builder(this)
        dialogMessage.setTitle("Delete all lists")
        dialogMessage.setMessage("If you click \"Yes\", all of your lists will be deleted forever")
        dialogMessage.setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialogInterface, i ->
            dialogInterface.cancel()
        })
        dialogMessage.setPositiveButton("Yes", DialogInterface.OnClickListener{ dialogInterface, i ->
            myReference.removeValue().addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    noteAdapter.notifyDataSetChanged()
                    Toast.makeText(applicationContext, "All lists have been deleted", Toast.LENGTH_LONG).show()
                }
            }
        })
        dialogMessage.create().show()
    }
}





