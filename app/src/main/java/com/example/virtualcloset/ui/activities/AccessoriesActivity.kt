package com.example.virtualcloset.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.ActivityAccessoriesBinding
import com.example.virtualcloset.databinding.ActivityTopsBinding
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.ui.RecyclerViewAdapter
import com.example.virtualcloset.utils.Constants
import com.google.firebase.firestore.*

class AccessoriesActivity : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var itemArrayList: ArrayList<Item>
    private lateinit var myAdapter: RecyclerViewAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var binding: ActivityAccessoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        itemArrayList = arrayListOf()

        myAdapter = RecyclerViewAdapter(itemArrayList)

        recyclerView.adapter = myAdapter

        EventChangeListener()

        binding.ivAddItem.setOnClickListener{
            startActivity(Intent(this@AccessoriesActivity,AddItemActivity::class.java))
        }
        binding.ivArrowBack.setOnClickListener {
            onBackPressed()
        }
    }
    private fun EventChangeListener() {
        val sharedPreferences = getSharedPreferences(Constants.VIRTUALCLOSET_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences?.getString(Constants.SIGNED_IN_USERNAME, "")!!
        val userUid = sharedPreferences?.getString(Constants.SIGNED_IN_UID,"")!!

//        //var itemList : ArrayList<Item> =
//
        val items : String = Constants.USERS+"/"+ userUid + "/" + Constants.ITEMS

        db = FirebaseFirestore.getInstance()
        db.collection(items)
            .whereEqualTo("category", "Accessories")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ){
                    if(error != null){
                        Log.e("FirestoreError", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED){
                            itemArrayList.add(dc.document.toObject(Item::class.java))
                        }
                    }
                    myAdapter.notifyDataSetChanged()
                }
            })
    }
}