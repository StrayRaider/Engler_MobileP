package com.example.engler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class WordListAdapter(
    context: Context,
    private val words: MutableList<String>,
    private val onEditClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : ArrayAdapter<String>(context, R.layout.list_item, words) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val wordTextView: TextView = view.findViewById(R.id.tvWord)
        val editButton: ImageButton = view.findViewById(R.id.btnEdit)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDelete)

        val word = getItem(position)

        wordTextView.text = word

        // Set up Edit button click listener
        editButton.setOnClickListener {
            word?.let { onEditClick(it) }
        }

        // Set up Delete button click listener
        deleteButton.setOnClickListener {
            word?.let { onDeleteClick(it) }
        }

        return view
    }
}
