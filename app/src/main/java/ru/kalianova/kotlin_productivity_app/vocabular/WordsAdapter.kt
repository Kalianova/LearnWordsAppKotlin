package ru.kalianova.kotlin_productivity_app.vocabular

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.kalianova.kotlin_productivity_app.ListItemWord
import ru.kalianova.kotlin_productivity_app.R

class WordsAdapter(private val itemWords: List<ListItemWord>) :
    RecyclerView.Adapter<WordsAdapter.ItemViewHolder>(){
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var largeTextView: TextView? = null
        var smallTextView: TextView? = null
        var image: ImageView? = null

        init {
            largeTextView = itemView.findViewById(R.id.textViewLarge)
            smallTextView = itemView.findViewById(R.id.textViewSmall)
            image = itemView.findViewById(R.id.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_word_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.largeTextView?.text = itemWords[position].word
        holder.smallTextView?.text = itemWords[position].translation
        if (itemWords[position].count >= 6)
            holder.image?.visibility = ImageView.VISIBLE
    }

    override fun getItemCount() = itemWords.size

}