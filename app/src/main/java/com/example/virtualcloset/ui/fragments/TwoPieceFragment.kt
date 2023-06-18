package com.example.virtualcloset.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.codingstuff.imageslider.ImageAdapter
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.FragmentTwoPieceBinding
import com.example.virtualcloset.firestore.FirestoreClass
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.models.Outfit
import com.example.virtualcloset.ui.activities.NavigationActivity
import com.example.virtualcloset.utils.Constants
import com.google.firebase.firestore.*
import org.checkerframework.common.subtyping.qual.Bottom
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
class TwoPieceFragment : BaseFragment() {
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
    private lateinit var itemTop: Item
    private lateinit var itemBottom: Item
    private lateinit var itemShoes: Item
    private lateinit var itemBag: Item
    private lateinit var itemAccessory: Item
    private lateinit var itemExtra: Item
    private lateinit var outfit: Outfit
    private var isSelectedExtra = false
    private var isSelectedBag = false
    private var isSelectedAcc = false


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
        outfit = Outfit()

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

        val btnAddExtra = view.findViewById<Button>(R.id.btn_add_extra)
        val ivAddBag = view.findViewById<ImageView>(R.id.iv_add_bag)
        val ivAddAccessories = view.findViewById<ImageView>(R.id.iv_add_accessories)
        val btnSaveOutfit = view.findViewById<Button>(R.id.btn_save_outfit)

        btnAddExtra.setOnClickListener {
            isSelectedExtra = !isSelectedExtra
            Toast.makeText(this@TwoPieceFragment.requireContext(),"extra selected, $isSelectedExtra",Toast.LENGTH_LONG)
        }

        ivAddBag.setOnClickListener {
            isSelectedBag = !isSelectedBag
            Toast.makeText(this@TwoPieceFragment.requireContext(),"bag selected, $isSelectedBag",Toast.LENGTH_LONG)
        }

        ivAddAccessories.setOnClickListener {
            isSelectedAcc = !isSelectedAcc
            Toast.makeText(this@TwoPieceFragment.requireContext(),"accesories selected, $isSelectedAcc",Toast.LENGTH_LONG)
        }

        btnSaveOutfit.setOnClickListener {
            outfit.images.add(itemTop.image)
            outfit.items.add(itemTop)
            outfit.images.add(itemBottom.image)
            outfit.items.add(itemBottom)
            outfit.images.add(itemShoes.image)
            outfit.items.add(itemShoes)
            if(isSelectedExtra||viewPager2Extra.isVisible){
                Toast.makeText(this@TwoPieceFragment.requireContext(),"extra selected, $isSelectedExtra",Toast.LENGTH_LONG)
                outfit.images.add(itemExtra.image)
                outfit.items.add(itemExtra)
            }
            if(isSelectedBag||viewPager2Bags.isVisible){
                outfit.images.add(itemBag.image)
                outfit.items.add(itemBag)
            }
            if(isSelectedAcc||viewPager2Accessories.isVisible){
                outfit.images.add(itemAccessory.image)
                outfit.items.add(itemAccessory)
            }
            saveDialog()
            Toast.makeText(this.requireContext(),"Save clicked",Toast.LENGTH_SHORT).show()
        }

        viewPager2Tops.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                itemTop = itemArrayList[position]
//                Toast.makeText(
//                    this@TwoPieceFragment.requireContext(),
//                    "Item $it.name, [$position]",
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        })
        viewPager2Bottoms.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                itemBottom = itemArrayListBottoms[position]
            }
        })
        viewPager2Shoes.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                itemShoes = itemArrayListShoes[position]
            }
        })
        viewPager2Extra.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
//                if(viewPager2Extra.isVisible){
//                    isSelectedExtra=true
//                }
                itemExtra = itemArrayList[position]
                Toast.makeText(this@TwoPieceFragment.requireContext(),"extra selected, $isSelectedExtra",Toast.LENGTH_LONG)
            }
        })
        viewPager2Bags.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                itemBag = itemArrayListBags[position]
            }
        })
        viewPager2Accessories.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                itemAccessory = itemArrayListAccessories[position]
            }
        })
        return view
    }

    private fun saveDialog() {
        val dialog = Dialog(this@TwoPieceFragment.requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.save_outfit_custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnSave: Button = dialog.findViewById(R.id.btn_save_outfit_final)
        val btnCancel: Button = dialog.findViewById(R.id.btn_cancel)
        val llOutfitCategory: LinearLayout = dialog.findViewById(R.id.ll_outfit_category)
        val tvOutfitCategory: TextView = dialog.findViewById(R.id.tv_outfit_category)
        val llOutfitStyle: LinearLayout = dialog.findViewById(R.id.ll_outfit_style)
        val tvOutfitStyle: TextView = dialog.findViewById(R.id.tv_outfit_style)
        val etOutfitName: EditText = dialog.findViewById(R.id.et_outfit_name)

        llOutfitCategory.setOnClickListener {
            var builder: AlertDialog.Builder = AlertDialog.Builder(this@TwoPieceFragment.requireContext())
            with(builder){
                setTitle("Outfit Category")
                setItems(Constants.outfit_category_options) { dialog, which ->
                    Toast.makeText(
                        this@TwoPieceFragment.requireContext(),
                        Constants.outfit_category_options[which] + " is clicked",
                        Toast.LENGTH_LONG
                    ).show()
                    tvOutfitCategory.text = Constants.outfit_category_options[which]
                }
                show()
            }
        }

        llOutfitStyle.setOnClickListener {
            var builder: AlertDialog.Builder = AlertDialog.Builder(this@TwoPieceFragment.requireContext());
            with(builder){
                setTitle("Item Style")
                setItems(Constants.style_options) { dialog, which ->
                    Toast.makeText(
                        this@TwoPieceFragment.requireContext(),
                        Constants.style_options[which] + " is clicked",
                        Toast.LENGTH_LONG
                    ).show()
                    tvOutfitStyle.text =Constants.style_options[which]
                }
                show()
            }
        }

        fun validate() : Boolean{
            return when{
                TextUtils.isEmpty(etOutfitName.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(this@TwoPieceFragment.requireContext(), resources.getString(R.string.err_msg_name_empty), Toast.LENGTH_LONG)
                    false
                }
                TextUtils.isEmpty(tvOutfitCategory.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(this@TwoPieceFragment.requireContext(), resources.getString(R.string.err_msg_category_empty), Toast.LENGTH_LONG)
                    false
                }
                TextUtils.isEmpty(tvOutfitStyle.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(this@TwoPieceFragment.requireContext(), resources.getString(R.string.err_msg_category_empty), Toast.LENGTH_LONG)
                    false
                }
                else -> {
                    true
                }
            }
        }

        btnSave.setOnClickListener {
            Toast.makeText(this@TwoPieceFragment.requireContext(), "Saved", Toast.LENGTH_LONG).show()
            if (validate()){
                val outfitName = etOutfitName.text.toString().trim{ it <= ' '}
                val category = tvOutfitCategory.text.toString()
                val style = tvOutfitStyle.text.toString()

                val outfitToBeAdded = Outfit(
                    System.currentTimeMillis().toString(),
                    outfitName,
                    category,
                    style,
                    outfit.images,
                    outfit.items
                    )
                FirestoreClass().addOutfitToDatabase(this, outfitToBeAdded)
            }

        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    override fun outfitAddedSuccessfully() {
        Toast.makeText(
            this@TwoPieceFragment.requireContext(),
            resources.getString(R.string.item_added_successfully),
            Toast.LENGTH_LONG
        ).show()
        startActivity(Intent(this@TwoPieceFragment.requireContext(), NavigationActivity::class.java))
        //onBackPressed()
        //this.activity?.finish()
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