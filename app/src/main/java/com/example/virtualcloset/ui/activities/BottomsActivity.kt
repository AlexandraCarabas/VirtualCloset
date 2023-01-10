package com.example.virtualcloset.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.ActivityBottomsBinding
import com.example.virtualcloset.databinding.ActivityTopsBinding
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.ui.RecyclerViewAdapter
import com.example.virtualcloset.utils.Constants
import com.google.firebase.firestore.*

class BottomsActivity : AppCompatActivity() {

    private lateinit var recyclerViewBottoms : RecyclerView
    private lateinit var itemArrayListBottoms: ArrayList<Item>
    private lateinit var myAdapterBottoms: RecyclerViewAdapter
    private lateinit var dbBottoms : FirebaseFirestore
    private lateinit var binding: ActivityBottomsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerViewBottoms = findViewById(R.id.recyclerView_bottoms)
        recyclerViewBottoms.layoutManager = LinearLayoutManager(this)
        recyclerViewBottoms.setHasFixedSize(true)

        itemArrayListBottoms = arrayListOf()

        myAdapterBottoms = RecyclerViewAdapter(itemArrayListBottoms)

        recyclerViewBottoms.adapter = myAdapterBottoms

        EventChangeListener()

        binding.ivAddItemBottoms.setOnClickListener{
            startActivity(Intent(this@BottomsActivity,AddItemActivity::class.java))
        }
        binding.ivArrowBackBottoms.setOnClickListener {
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

        dbBottoms = FirebaseFirestore.getInstance()
        dbBottoms.collection(items)
            .whereEqualTo("category", "Bottoms")
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
                            itemArrayListBottoms.add(dc.document.toObject(Item::class.java))
                        }
                    }
                    myAdapterBottoms.notifyDataSetChanged()
                }
            })
    }
}