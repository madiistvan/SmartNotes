package hu.bme.aut.android.smartnotes.list

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.smartnotes.R
import hu.bme.aut.android.smartnotes.list.adapter.NotesAdapter
import hu.bme.aut.android.smartnotes.databinding.ActivityNotesBinding
import hu.bme.aut.android.smartnotes.list.details.NoteDetailsActivity
import hu.bme.aut.android.smartnotes.model.NoteItem
import hu.bme.aut.android.smartnotes.activity.BaseActivity
import hu.bme.aut.android.smartnotes.activity.StartPageActivity
import java.lang.Exception


class NotesActivity : BaseActivity(), NotesAdapter.NoteItemClickListener {
    private lateinit var binding: ActivityNotesBinding
    private lateinit var adapter: NotesAdapter
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.fab.setOnClickListener { onAdd(NoteItem("", "", "")) }
        showProgressDialog()
        initActionBar()
    }

    override fun onStart() {
        super.onStart()
        initNotes()
    }

    override fun onSupportNavigateUp(): Boolean {
        Firebase.auth.signOut()
        startActivity(Intent(this, StartPageActivity::class.java))
        return true
    }

    private fun initNotes() {
        adapter = NotesAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        fetchNotes()
    }

    private fun fetchNotes(): ArrayList<NoteItem> {
        val notesList = ArrayList<NoteItem>()
        val userID = Firebase.auth.currentUser?.uid
        try {
            db.collection(userID!!).get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        notesList.add(
                            NoteItem(
                                doc.getString(getString(R.string.document_title_field))!!,
                                doc.getString(
                                    getString(
                                        R.string.document_note_field
                                    )
                                )!!,
                                doc.id
                            )
                        )
                        Log.w(
                            TAG,
                            "Success getting documents: " + doc.getString("title") + notesList.size
                        )
                    }
                    adapter.update(notesList)
                    Log.w(TAG, "noteList size:" + notesList.size)
                    hideProgressDialog()
                }
                .addOnFailureListener { exception ->
                    snack(binding.root, getString(R.string.note_fetch_error_msg))
                    Log.w(TAG, "Error getting documents: ", exception)
                }

        } catch (e: Exception) {
            snack(binding.root, getString(R.string.note_fetch_error_msg))
        }

        Log.w(TAG, "noteList size:" + notesList.size)
        return notesList
    }


    override fun onDelete(item: NoteItem) {
        val userID = Firebase.auth.currentUser?.uid
        db.collection(userID!!).document(item.id).delete()
            .addOnSuccessListener { snack(binding.root, getString(R.string.delete_msg)) }
            .addOnFailureListener { snack(binding.root, getString(R.string.failure)) }
        Log.w(TAG, "${item.title} deleted")
    }

    override fun onAdd(item: NoteItem) {
        startActivity(Intent(this, NoteDetailsActivity::class.java))

    }

    override fun edit(item: NoteItem) {
        val intent = Intent(this, NoteDetailsActivity::class.java)
        intent.putExtra("title", item.title)
        intent.putExtra("note", item.note)
        intent.putExtra("id", item.id)
        startActivity(intent)

    }
}