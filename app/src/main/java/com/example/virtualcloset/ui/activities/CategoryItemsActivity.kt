package com.example.virtualcloset.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.ActivityCategoryItemsBinding
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.ui.RecyclerViewAdapter
import com.example.virtualcloset.utils.Constants
import com.google.firebase.firestore.*

class CategoryItemsActivity : BaseActivity() {

    private lateinit var recyclerView : RecyclerView
    private lateinit var itemArrayList: ArrayList<Item>
    private lateinit var myAdapter: RecyclerViewAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var binding: ActivityCategoryItemsBinding
    var category_position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        category_position = intent.getIntExtra(Constants.CATEGORY,0)
        binding.tvTitle.text = Constants.category_options[category_position]

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        itemArrayList = arrayListOf()

        myAdapter = RecyclerViewAdapter(itemArrayList)
        recyclerView.adapter = myAdapter
        myAdapter.setOnItemClickListener(object: RecyclerViewAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(this@CategoryItemsActivity, DisplayItemActivity::class.java)
                intent.putExtra("item", itemArrayList[position])
                startActivity(intent)
            }

        })


        EventChangeListener()

        binding.ivAddItem.setOnClickListener{
            startActivity(Intent(this,AddItemActivity::class.java))
        }
        binding.ivArrowBack.setOnClickListener {
            onBackPressed()
        }

        refreshActivity()
    }

    private fun refreshActivity() {
        binding.swipeToRefresh.setOnRefreshListener {
            myAdapter.notifyDataSetChanged()
            binding.swipeToRefresh.isRefreshing = false
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, NavigationActivity::class.java)
        intent.putExtra(Constants.CLOSET, 1)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        myAdapter.notifyDataSetChanged()
    }

    private fun EventChangeListener() {
        val sharedPreferences = getSharedPreferences(Constants.VIRTUALCLOSET_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences?.getString(Constants.SIGNED_IN_USERNAME, "")!!
        val userUid = sharedPreferences?.getString(Constants.SIGNED_IN_UID,"")!!
        val items : String = Constants.USERS+"/"+ userUid + "/" + Constants.ITEMS

        db = FirebaseFirestore.getInstance()
        db.collection(items)
            .whereEqualTo(Constants.CATEGORY, Constants.category_options[category_position])
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