package com.example.receptomat.fragments

import ShoppingListAdapter
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.LoginActivity
import com.example.receptomat.R

import com.google.android.material.floatingactionbutton.FloatingActionButton
import database.ApiService
import database.RetrofitClient
import database.ShoppingListWithItems

import database.ShoppingListsWithItemsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var addButton: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewShoppingLists)
        addButton = view.findViewById(R.id.fabAddList)

        adapter = ShoppingListAdapter(mutableListOf()) { selectedList ->
            openEditFragment(selectedList)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        fetchShoppingLists()

        addButton.setOnClickListener {
            val container = view?.findViewById<FrameLayout>(R.id.fragment_container)
            container?.visibility = View.VISIBLE


            val fragment = EditShoppingListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("is_new", true)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }


    }

    override fun onResume() {
        super.onResume()
        fetchShoppingLists()
        view?.findViewById<FrameLayout>(R.id.fragment_container)?.visibility = View.GONE
    }



    private fun fetchShoppingLists() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)
        apiService.getShoppingListsWithItems(userId = userId).enqueue(object : Callback<ShoppingListsWithItemsResponse> {
            override fun onResponse(call: Call<ShoppingListsWithItemsResponse>, response: Response<ShoppingListsWithItemsResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val shoppingLists = response.body()?.shopping_lists ?: emptyList()
                    adapter.updateList(shoppingLists)
                } else {
                    Toast.makeText(requireContext(), "Error fetching lists: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ShoppingListsWithItemsResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun openEditFragment(selectedList: ShoppingListWithItems) {
        val fragment = EditShoppingListFragment().apply {
            arguments = Bundle().apply {
                putBoolean("is_new", false)
                putInt("list_id", selectedList.list_id)
                putString("list_name", selectedList.list_name)
                putStringArrayList("items", ArrayList(selectedList.items))
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()



    view?.findViewById<FrameLayout>(R.id.fragment_container)?.visibility = View.VISIBLE
    }

}