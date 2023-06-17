package com.example.virtualcloset.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.codingstuff.imageslider.ImageAdapter
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.FragmentTwoPieceBinding
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.utils.Constants
import com.google.firebase.firestore.*
import kotlin.math.abs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TwoPieceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TwoPieceFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentTwoPieceBinding
    private lateinit var viewPager2Tops: ViewPager2
    private lateinit var viewPager2Bottoms: ViewPager2
    private lateinit var viewPager2Shoes: ViewPager2
    private lateinit var viewPager2Extra: ViewPager2
    private lateinit var viewPager2Bags: ViewPager2
    private lateinit var viewPager2Accessories: ViewPager2
    private lateinit var itemArrayList: ArrayList<Item>
    private lateinit var itemArrayListBottoms: ArrayList<Item>
    private lateinit var itemArrayListShoes: ArrayList<Item>
    private lateinit var itemArrayListBags: ArrayList<Item>
    private lateinit var itemArrayListAccessories: ArrayList<Item>
    private lateinit var adapter: ImageAdapter
    private lateinit var adapterBottoms: ImageAdapter
    private lateinit var adapterShoes: ImageAdapter
    private lateinit var adapterBags: ImageAdapter
    private lateinit var adapterAccessories: ImageAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var dbBottoms : FirebaseFirestore
    private lateinit var dbShoes : FirebaseFirestore
    private lateinit var dbBags : FirebaseFirestore
    private lateinit var dbAccessories : FirebaseFirestore
    private lateinit var userUid: String
    private var extra_visible = false


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
        val view = inflater.inflate(R.layout.fragment_two_piece, container, false)
        binding = FragmentTwoPieceBinding.inflate(layoutInflater)

        viewPager2Tops = view.findViewById(R.id.vp2_top)
        viewPager2Bottoms = view.findViewById(R.id.vp2_bottom)
        viewPager2Shoes = view.findViewById(R.id.vp2_shoes)
        viewPager2Extra = view.findViewById(R.id.vp2_extra)
        viewPager2Bags = view.findViewById(R.id.vp2_bags)
        viewPager2Accessories = view.findViewById(R.id.vp2_acessories)
        itemArrayList = arrayListOf()
        itemArrayListBottoms = arrayListOf()
        itemArrayListShoes = arrayListOf()
        itemArrayListBags = arrayListOf()
        itemArrayListAccessories = arrayListOf()

        adapter = ImageAdapter(itemArrayList, viewPager2Tops)
        adapterBottoms = ImageAdapter(itemArrayListBottoms,viewPager2Bottoms)
        adapterShoes = ImageAdapter(itemArrayListShoes,viewPager2Shoes)
        adapterBags = ImageAdapter(itemArrayListBags,viewPager2Bags)
        adapterAccessories = ImageAdapter(itemArrayListAccessories,viewPager2Accessories)

        viewPager2Tops.adapter = adapter
        viewPager2Tops.offscreenPageLimit = 3
        viewPager2Tops.clipToPadding = false
        viewPager2Tops.clipChildren = false
        viewPager2Tops.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS

        viewPager2Bottoms.adapter = adapterBottoms
        viewPager2Bottoms.offscreenPageLimit = 3
        viewPager2Bottoms.clipToPadding = false
        viewPager2Bottoms.clipChildren = false
        viewPager2Bottoms.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS

        viewPager2Shoes.adapter = adapterShoes
        viewPager2Shoes.offscreenPageLimit = 3
        viewPager2Shoes.clipToPadding = false
        viewPager2Shoes.clipChildren = false
        viewPager2Shoes.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS

        viewPager2Extra.adapter = adapter
        viewPager2Extra.offscreenPageLimit = 3
        viewPager2Extra.clipToPadding = false
        viewPager2Extra.clipChildren = false
        viewPager2Extra.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS

        viewPager2Bags.adapter = adapterBags
        viewPager2Bags.offscreenPageLimit = 3
        viewPager2Bags.clipToPadding = false
        viewPager2Bags.clipChildren = false
        viewPager2Bags.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS

        viewPager2Accessories.adapter = adapterAccessories
        viewPager2Accessories.offscreenPageLimit = 3
        viewPager2Accessories.clipToPadding = false
        viewPager2Accessories.clipChildren = false
        viewPager2Accessories.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS
        setUpTransformer()

        val sharedPreferences = this.getActivity()?.getSharedPreferences(Constants.VIRTUALCLOSET_PREFERENCES, Context.MODE_PRIVATE)
        userUid = sharedPreferences?.getString(Constants.SIGNED_IN_UID,"")!!

        EventChangeListener()
        EventChangeListenerBottoms()
        EventChangeListenerShoes()
        EventChangeListenerBags()
        EventChangeListenerAccessories()

        viewPager2Tops.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val it = itemArrayList[position].name
                Toast.makeText(
                    this@TwoPieceFragment.requireContext(),
                    "Item $it, [$position]",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        viewPager2Bottoms.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
//                handlerBottoms.removeCallbacks(runnable)
//                handlerBottoms.postDelayed(runnable, 2000)
            }
        })
        viewPager2Shoes.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
//                handlerShoes.removeCallbacks(runnable)
//                handlerShoes.postDelayed(runnable, 2000)
            }
        })
        return view
    }

//    override fun onPause() {
//        super.onPause()
//
//        handler.removeCallbacks(runnable)
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        handler.postDelayed(runnable , 2000)
//    }

    private val runnable = Runnable {
        viewPager2Tops.currentItem = viewPager2Tops.currentItem + 1
    }

    private fun setUpTransformer(){
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.1f
        }

        viewPager2Tops.setPageTransformer(transformer)
        viewPager2Bottoms.setPageTransformer(transformer)
        viewPager2Shoes.setPageTransformer(transformer)
        viewPager2Extra.setPageTransformer(transformer)
    }

    private fun EventChangeListener() {
        val items: String = Constants.USERS + "/" + userUid + "/" + Constants.ITEMS
        db = FirebaseFirestore.getInstance()
        db.collection(items)
            .whereEqualTo(Constants.CATEGORY, Constants.category_options[0])
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("FirestoreError", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            itemArrayList.add(dc.document.toObject(Item::class.java))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            })
    }

    private fun EventChangeListenerBottoms() {
        val items: String = Constants.USERS + "/" + userUid + "/" + Constants.ITEMS
        dbBottoms = FirebaseFirestore.getInstance()
        dbBottoms.collection(items)
            .whereEqualTo(Constants.CATEGORY, Constants.category_options[1])
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("FirestoreError", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            itemArrayListBottoms.add(dc.document.toObject(Item::class.java))
                        }
                    }
                    adapterBottoms.notifyDataSetChanged()
                }
            })
    }


    private fun EventChangeListenerShoes() {
        val items : String = Constants.USERS+"/"+ userUid + "/" + Constants.ITEMS
        dbShoes = FirebaseFirestore.getInstance()
        dbShoes.collection(items)
            .whereEqualTo(Constants.CATEGORY, Constants.category_options[5])
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
                            itemArrayListShoes.add(dc.document.toObject(Item::class.java))
                        }
                    }
                    adapterShoes.notifyDataSetChanged()
                }
            })
    }

    private fun EventChangeListenerBags() {
        val items: String = Constants.USERS + "/" + userUid + "/" + Constants.ITEMS
        dbBags = FirebaseFirestore.getInstance()
        dbBags.collection(items)
            .whereEqualTo(Constants.CATEGORY, Constants.category_options[4])
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("FirestoreError", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            itemArrayListBags.add(dc.document.toObject(Item::class.java))
                        }
                    }
                    adapterBags.notifyDataSetChanged()
                }
            })
    }

    private fun EventChangeListenerAccessories() {
        val items: String = Constants.USERS + "/" + userUid + "/" + Constants.ITEMS
        dbAccessories = FirebaseFirestore.getInstance()
        dbAccessories.collection(items)
            .whereEqualTo(Constants.CATEGORY, Constants.category_options[3])
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("FirestoreError", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            itemArrayListAccessories.add(dc.document.toObject(Item::class.java))
                        }
                    }
                    adapterAccessories.notifyDataSetChanged()
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
         * @return A new instance of fragment TwoPieceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TwoPieceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}