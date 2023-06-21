package com.example.virtualcloset.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.FragmentOutfitsBinding
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.models.Outfit
import com.example.virtualcloset.ui.RecyclerViewAdapter
import com.example.virtualcloset.ui.RecyclerViewAdapterOutfit
import com.example.virtualcloset.ui.activities.CreateOutfitActivity
import com.example.virtualcloset.ui.activities.DisplayItemActivity
import com.example.virtualcloset.ui.activities.OutfitDetailsActivity
import com.example.virtualcloset.utils.Constants
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Outfits.newInstance] factory method to
 * create an instance of this fragment.
 */
class Outfits : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentOutfitsBinding

    private lateinit var recyclerView : RecyclerView
    private lateinit var outfitsArrayList: ArrayList<Outfit>
    private lateinit var myAdapter: RecyclerViewAdapterOutfit
    private lateinit var db : FirebaseFirestore
    private lateinit var userUid: String

    private lateinit var manager: RecyclerView.LayoutManager


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
        val view = inflater.inflate(R.layout.fragment_outfits, container, false)

        binding = FragmentOutfitsBinding.inflate(layoutInflater)

        val sharedPreferences = this.getActivity()?.getSharedPreferences(Constants.VIRTUALCLOSET_PREFERENCES, Context.MODE_PRIVATE)
        userUid = sharedPreferences?.getString(Constants.SIGNED_IN_UID,"")!!

        recyclerView = view.findViewById(R.id.recyclerViewOutfits)
        //recyclerView.layoutManager = LinearLayoutManager(this@Outfits.requireContext())
        //recyclerView.setHasFixedSize(true)

        outfitsArrayList = arrayListOf()

        myAdapter = RecyclerViewAdapterOutfit(outfitsArrayList)
        recyclerView.adapter = myAdapter
        myAdapter.setOnItemClickListener(object : RecyclerViewAdapterOutfit.onItemClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(
                    this@Outfits.requireContext(),
                    "You clicked on item nr. $position",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@Outfits.requireContext(), OutfitDetailsActivity::class.java)
                intent.putExtra("outfit", outfitsArrayList[position])
                startActivity(intent)
            }
        })

        EventChangeListener()

        val btnCreateOutfit = view.findViewById<Button>(R.id.btn_create_outfit)
        btnCreateOutfit.setOnClickListener {
            activity?.startActivity(Intent(this.activity,CreateOutfitActivity::class.java))
        }

        return view
    }

    private fun EventChangeListener() {

//        //var itemList : ArrayList<Item> =
//
        val outfits : String = Constants.USERS+"/"+ userUid + "/" + Constants.OUTFITS

        db = FirebaseFirestore.getInstance()
        db.collection(outfits)
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
                            outfitsArrayList.add(dc.document.toObject(Outfit::class.java))
                        }
                    }
                    myAdapter.notifyDataSetChanged()
                }
            })
    }

    override fun onResume() {
        super.onResume()

        val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
        fragmentManager?.let {
            val currentFragment = fragmentManager.findFragmentById(R.id.cl_outfits_container)
            currentFragment?.let {
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.detach(it)
                fragmentTransaction.attach(it)
                fragmentTransaction.commit()
            }
        }
        myAdapter.notifyDataSetChanged()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Outfits.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Outfits().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}