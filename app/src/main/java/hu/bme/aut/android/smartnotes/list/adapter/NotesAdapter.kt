package hu.bme.aut.android.smartnotes.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.smartnotes.databinding.NoteCardBinding
import hu.bme.aut.android.smartnotes.model.NoteItem

class NotesAdapter(private val listener: NoteItemClickListener) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    private var notes =  ArrayList<NoteItem>()

    interface NoteItemClickListener{
        fun onDelete(item: NoteItem)
        fun onAdd(item: NoteItem)
        fun edit(item: NoteItem)
    }


    inner class ViewHolder(val binding: NoteCardBinding) : RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.title
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = ViewHolder(
        NoteCardBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)
    )


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val note = notes[position]

        viewHolder.binding.title.text = notes[position].title
        viewHolder.binding.ibRemove.setOnClickListener {
            delete(position)
            listener.onDelete(note)
        }
        viewHolder.binding.noteCard.setOnClickListener {
            edit(position)
            listener.edit(note)
        }

    }

    override fun getItemCount() = notes.size

    fun update(data: ArrayList<NoteItem>){
        notes.clear()
        notes.addAll(data)
        notifyDataSetChanged()
    }
    fun add(note: NoteItem){
        notes.add(note)
        notifyItemInserted(notes.size - 1)
    }
    private fun delete(position: Int){
        notes.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,itemCount)
    }
    private fun edit(position: Int){
        notifyItemChanged(position)
    }

}
