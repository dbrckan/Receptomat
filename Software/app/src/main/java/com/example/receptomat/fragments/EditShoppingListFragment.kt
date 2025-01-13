package com.example.receptomat.fragments

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
import database.RetrofitClient
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveButton = view.findViewById(R.id.buttonSave)
        cancelButton = view.findViewById(R.id.buttonCancel)
        addItemButton = view.findViewById(R.id.buttonAddItem)
        listNameEditText = view.findViewById(R.id.editTextListName)
        itemsRecyclerView = view.findViewById(R.id.recyclerViewItems)

        itemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        itemsRecyclerView.adapter = ItemAdapter(items) { position, newName ->
            items[position] = Item(position, newName, "")
        }


        addItemButton.setOnClickListener {
            items.add(Item(items.size, "", ""))
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

            saveShoppingList(listName, itemNames)
        }

        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun saveShoppingList(listName: String, itemNames: List<String>) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val request = AddShoppingListRequest(list_name = listName, user_id = 1, items = itemNames)
        apiService.createShoppingListWithItems(request).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(requireContext(), "List saved successfully", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    val error = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(requireContext(), "Error saving list: $error", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}