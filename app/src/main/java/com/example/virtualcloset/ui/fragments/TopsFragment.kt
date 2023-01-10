package com.example.virtualcloset.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualcloset.R
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.ui.RecyclerViewAdapter
import com.example.virtualcloset.utils.Constants
import com.google.firebase.firestore.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var recyclerView : RecyclerView
private lateinit var itemArrayList: ArrayList<Item>
private lateinit var myAdapter: RecyclerViewAdapter
private lateinit var db : FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [TopsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TopsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tops, container, false)

        recyclerView = view.findViewById(R.id.recyclerView_l)
        recyclerView.layoutManager = LinearLayoutManager(this.activity)
        recyclerView.setHasFixedSize(true)

        itemArrayList = arrayListOf()

        myAdapter = RecyclerViewAdapter(itemArrayList)

        recyclerView.adapter = myAdapter

        EventChangeListener()

        return view
    }

    private fun EventChangeListener() {
        val sharedPreferences = this.getActivity()?.getSharedPreferences(Constants.VIRTUALCLOSET_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences?.getString(Constants.SIGNED_IN_USERNAME, "")!!
        val userUid = sharedPreferences?.getString(Constants.SIGNED_IN_UID,"")!!

//        //var itemList : ArrayList<Item> =
//
        val items : String = Constants.USERS+"/"+ userUid + "/" + Constants.ITEMS

        db = FirebaseFirestore.getInstance()
        db.collection(items)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TopsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TopsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}