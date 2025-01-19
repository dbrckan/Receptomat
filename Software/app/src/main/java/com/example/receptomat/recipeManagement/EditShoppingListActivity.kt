package com.example.receptomat.recipeManagement

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.adapters.Item
import com.example.receptomat.adapters.ItemAdapter
import database.AddShoppingListRequest
import database.ApiService
import database.BasicResponse
import database.DeleteItemRequest
import database.RecipeItem
import database.RetrofitClient
import database.UpdateShoppingListRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditShoppingListActivity : AppCompatActivity() {

    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var addItemButton: Button
    private lateinit var listNameEditText: EditText
    private lateinit var itemsRecyclerView: RecyclerView
    private val items = mutableListOf<Item>()
    private var listId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_shopping_list)

        saveButton = findViewById(R.id.buttonSave)
        cancelButton = findViewById(R.id.buttonCancel)
        addItemButton = findViewById(R.id.buttonAddItem)
        listNameEditText = findViewById(R.id.editTextListName)
        itemsRecyclerView = findViewById(R.id.recyclerViewItems)

        itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        itemsRecyclerView.adapter = ItemAdapter(items, { position, newName ->
            items[position] = Item(items[position].id, newName, "")
        }, { position, item ->
            deleteItemFromList(item, position)
        })

        val isNew = intent.getBooleanExtra("is_new", true)
        if (isNew) {
            val receivedItems = intent.getStringArrayListExtra("items")
            if (receivedItems != null) {
                items.clear()
                items.addAll(receivedItems.mapIndexed { index, name ->
                    Item(index + 1, name, "")
                })
                itemsRecyclerView.adapter?.notifyDataSetChanged()
            }
            listNameEditText.setText("New Shopping List")
        } else {
            listId = intent.getIntExtra("list_id", -1)
            val listName = intent.getStringExtra("list_name") ?: ""
            listNameEditText.setText(listName)

            val receivedItems = intent.getStringArrayListExtra("items")
            if (receivedItems != null) {
                items.clear()
                items.addAll(receivedItems.mapIndexed { index, name ->
                    Item(index + 1, name, "")
                })
                itemsRecyclerView.adapter?.notifyDataSetChanged()
            }
        }



        addItemButton.setOnClickListener {
            items.add(Item(-1, "", ""))
            itemsRecyclerView.adapter?.notifyItemInserted(items.size - 1)
        }

        saveButton.setOnClickListener {
            val listName = listNameEditText.text.toString().trim()
            if (listName.isEmpty()) {
                Toast.makeText(this, "Enter a list name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val itemNames = items.map { it.name }.filter { it.isNotBlank() }
            if (itemNames.isEmpty()) {
                Toast.makeText(this, "Add at least one item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (listId == -1) {
                createShoppingList(listName, itemNames)
            } else {
                updateShoppingList(listName)
            }
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun createShoppingList(listName: String, itemNames: List<String>) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        val request = AddShoppingListRequest(list_name = listName, user_id = userId, items = itemNames)

        apiService.createShoppingListWithItems(request).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@EditShoppingListActivity, "List created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditShoppingListActivity, "Error creating list: ${response.body()?.error}",
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Toast.makeText(this@EditShoppingListActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateShoppingList(listName: String) {
        if (listId == -1) {
            Toast.makeText(this, "Invalid list ID", Toast.LENGTH_SHORT).show()
            return
        }

        val itemNames = items.map { it.name }.filter { it.isNotBlank() }
        if (itemNames.isEmpty()) {
            Toast.makeText(this, "Add at least one item", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val request = UpdateShoppingListRequest(
            list_id = listId,
            list_name = listName,
            items = itemNames
        )

        apiService.updateShoppingList(request).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@EditShoppingListActivity, "List updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditShoppingListActivity, "Error updating list: ${response.body()?.error}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Toast.makeText(this@EditShoppingListActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteItemFromList(item: Item, position: Int) {
        if (item.id == -1 || listId == -1) {
            Toast.makeText(this, "Invalid item or list ID", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val request = DeleteItemRequest(item_id = item.id, list_id = listId)

        apiService.deleteItemFromList(request).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    items.removeAt(position)
                    itemsRecyclerView.adapter?.notifyItemRemoved(position)
                    Toast.makeText(this@EditShoppingListActivity, "Item deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@EditShoppingListActivity, "Error deleting item: ${response.body()?.error}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Toast.makeText(this@EditShoppingListActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
