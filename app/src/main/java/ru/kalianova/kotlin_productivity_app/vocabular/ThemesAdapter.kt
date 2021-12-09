package ru.kalianova.kotlin_productivity_app.vocabular

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.kalianova.kotlin_productivity_app.ListItemTheme
import ru.kalianova.kotlin_productivity_app.R

class ThemesAdapter(private val itemThemes: List<ListItemTheme>, val clickListener: (ListItemTheme) -> Unit) :
        RecyclerView.Adapter<ThemesAdapter.ItemViewHolder>(){
        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var largeTextView: TextView? = null
            var smallTextView: TextView? = null

            init {
                largeTextView = itemView.findViewById(R.id.textViewLarge)
                smallTextView = itemView.findViewById(R.id.textViewSmall)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val itemView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.activity_word_item, parent, false)
            return ItemViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.largeTextView?.text = itemThemes[position].name
            holder.smallTextView?.text = itemThemes[position].key
            holder.itemView.setOnClickListener{clickListener(itemThemes[position])}
        }


        override fun getItemCount() = itemThemes.size

}