package com.example.receptomat.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.adapters.Item
import com.example.receptomat.adapters.ItemAdapter
import database.AddShoppingListRequest
import database.ApiService
import database.BasicResponse
import database.DeleteItemRequest
import database.RetrofitClient
import database.UpdateShoppingListRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditShoppingListFragment : Fragment(R.layout.fragment_edit_shopping_list) {

    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var addItemButton: Button
    private lateinit var listNameEditText: EditText
    private lateinit var itemsRecyclerView: RecyclerView
    private val items = mutableListOf<Item>()
    private var listId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveButton = view.findViewById(R.id.buttonSave)
        cancelButton = view.findViewById(R.id.buttonCancel)
        addItemButton = view.findViewById(R.id.buttonAddItem)
        listNameEditText = view.findViewById(R.id.editTextListName)
        itemsRecyclerView = view.findViewById(R.id.recyclerViewItems)

        // Postavljanje RecyclerView adaptera
        itemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        itemsRecyclerView.adapter = ItemAdapter(items, { position, newName ->
            items[position] = Item(items[position].id, newName, "")
        }, { position, item ->
            deleteItemFromList(item, position)
        })

        arguments?.let { bundle ->
            val isNew = bundle.getBoolean("is_new", true)
            if (!isNew) {

                listId = bundle.getInt("list_id", -1)
                listNameEditText.setText(bundle.getString("list_name", ""))
                val receivedItems = bundle.getStringArrayList("items") ?: arrayListOf()
                items.clear()
                items.addAll(receivedItems.mapIndexed { index, name -> Item(index + 1, name, "") })
                itemsRecyclerView.adapter?.notifyDataSetChanged()
            } else {

                listId = -1
            }
        }

        addItemButton.setOnClickListener {
            items.add(Item(-1, "", ""))
            itemsRecyclerView.adapter?.notifyItemInserted(items.size - 1)
        }

        saveButton.setOnClickListener {
            val listName = listNameEditText.text.toString().trim()
            if (listName.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a list name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val itemNames = items.map { it.name }.filter { it.isNotBlank() }
            if (itemNames.isEmpty()) {
                Toast.makeText(requireContext(), "Add at least one item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (listId == -1) {
                createShoppingList(listName, itemNames)
            } else {
                updateShoppingList(listName)
            }
        }

        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun createShoppingList(listName: String, itemNames: List<String>) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        val request = AddShoppingListRequest(list_name = listName, user_id = userId, items = itemNames)

        apiService.createShoppingListWithItems(request).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(requireContext(), "List created successfully", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Error creating list: ${response.body()?.error}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun updateShoppingList(listName: String) {
        if (listId == -1) {
            Toast.makeText(requireContext(), "Invalid list ID", Toast.LENGTH_SHORT).show()
            return
        }

        val itemNames = items.map { it.name }.filter { it.isNotBlank() }
        if (itemNames.isEmpty()) {
            Toast.makeText(requireContext(), "Add at least one item", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "List updated successfully", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Error updating list: ${response.body()?.error}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun deleteItemFromList(item: Item, position: Int) {
        if (item.id == -1 || listId == -1) {
            Toast.makeText(requireContext(), "Invalid item or list ID", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val request = DeleteItemRequest(item_id = item.id, list_id = listId)

        apiService.deleteItemFromList(request).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    items.removeAt(position)
                    itemsRecyclerView.adapter?.notifyItemRemoved(position)
                    Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error deleting item: ${response.body()?.error}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
