package hu.bme.aut.android.smartnotes.list.details

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.smartnotes.R
import hu.bme.aut.android.smartnotes.activity.BaseActivity
import hu.bme.aut.android.smartnotes.activity.MainActivity
import hu.bme.aut.android.smartnotes.databinding.ActivityNoteDetailsBinding
import hu.bme.aut.android.smartnotes.model.NoteItem

class NoteDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityNoteDetailsBinding
    private val db = Firebase.firestore
    private var note : NoteItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (intent.hasExtra("title")){
            note = NoteItem(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("note")!!,
                intent.getStringExtra("id")!!,
                )
            binding.title.setText(note!!.title)
            binding.note.setText(note!!.note)
        }
        binding.photo.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))

        }
        binding.btnSave.setOnClickListener {
            val userID = Firebase.auth.currentUser?.uid

            if(binding.title.text.toString().trim().isNotEmpty()) {


                val noteMap = hashMapOf(
                        "title" to binding.title.text.toString(),
                        "note" to binding.note.text.toString()
                    )

                if (note == null) {
                    db.collection(userID!!).add(noteMap)
                        .addOnSuccessListener { snack(binding.root,getString(R.string.note_created_msg)) }
                        .addOnFailureListener { snack(binding.root,getString(R.string.note_not_created_msg)) }
                }
                else{
                    val doc = db.collection(userID!!).document(note!!.id)
                    doc.update(noteMap as Map<String, Any>)
                        .addOnSuccessListener { snack(binding.root,getString(R.string.note_edited_msg)) }
                        .addOnFailureListener { snack(binding.root,getString(R.string.note_not_edited_msg)) }
                }
                finish()
            }
            else{
                snack(binding.root,getString(R.string.note_error_msg))
            }

        }
        initActionBar()
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}