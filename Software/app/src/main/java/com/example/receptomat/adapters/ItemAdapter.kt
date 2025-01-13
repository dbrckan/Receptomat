package com.example.receptomat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R

data class Item(val id: Int, val name: String, val description: String)

class ItemAdapter(
    private val items: List<Item>,
    private val onItemChanged: (Int, String) -> Unit,
    private val onItemDeleted: (Int, Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: EditText = itemView.findViewById(R.id.textViewItemName)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.setText(item.name)

        holder.itemName.addTextChangedListener {
            onItemChanged(position, it.toString())
        }

        holder.deleteButton.setOnClickListener {
            onItemDeleted(position, item)
        }
    }


    override fun getItemCount(): Int = items.size
}
