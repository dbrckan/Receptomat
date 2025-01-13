import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import database.ShoppingListWithItems

class ShoppingListAdapter(
    private val shoppingLists: MutableList<ShoppingListWithItems>,
    private val onListClick: (ShoppingListWithItems) -> Unit
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    inner class ShoppingListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.textViewListName)
        val itemsTextView: TextView = view.findViewById(R.id.textViewItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping_list, parent, false)
        return ShoppingListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val shoppingList = shoppingLists[position]
        holder.nameTextView.text = shoppingList.list_name
        holder.itemsTextView.text = shoppingList.items.joinToString(", ")

        holder.itemView.setOnClickListener {
            onListClick(shoppingList)
        }

    }

    override fun getItemCount(): Int = shoppingLists.size

    fun updateList(newLists: List<ShoppingListWithItems>) {
        shoppingLists.clear()
        shoppingLists.addAll(newLists)
        notifyDataSetChanged()
    }
}
