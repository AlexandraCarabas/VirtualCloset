package com.example.virtualcloset.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.codingstuff.imageslider.ImageAdapter
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.FragmentOnePieceBinding
import com.example.virtualcloset.firestore.FirestoreClass
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.models.Outfit
import com.example.virtualcloset.ui.activities.NavigationActivity
import com.example.virtualcloset.ui.activities.OutfitDetailsActivity
import com.example.virtualcloset.utils.Constants
import com.google.firebase.firestore.*
import kotlin.math.abs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OnePieceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnePieceFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentOnePieceBinding
    private lateinit var viewPager2Dress: ViewPager2
    private lateinit var viewPager2Shoes: ViewPager2
    private lateinit var viewPager2Extra: ViewPager2
    private lateinit var viewPager2Bags: ViewPager2
    private lateinit var viewPager2Accessories: ViewPager2
    private lateinit var itemArrayListDresses: ArrayList<Item>
    private lateinit var itemArrayListShoes: ArrayList<Item>
    private lateinit var itemArrayListBags: ArrayList<Item>
    private lateinit var itemArrayListAccessories: ArrayList<Item>
    private lateinit var itemArrayListTops: ArrayList<Item>
    private lateinit var adapterDress: ImageAdapter
    private lateinit var adapterShoes: ImageAdapter
    private lateinit var adapterTops: ImageAdapter
    private lateinit var adapterBags: ImageAdapter
    private lateinit var adapterAccessories: ImageAdapter
    private lateinit var dbD : FirebaseFirestore
    private lateinit var dbS : FirebaseFirestore
    private lateinit var dbT : FirebaseFirestore
    private lateinit var dbB : FirebaseFirestore
    private lateinit var dbA : FirebaseFirestore
    private lateinit var userUid: String
    private lateinit var itemDress: Item
    private lateinit var itemShoes: Item
    private lateinit var itemBag: Item
    private lateinit var itemAccessory: Item
    private lateinit var itemExtra: Item
    private lateinit var outfit: Outfit

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
        val view = inflater.inflate(R.layout.fragment_one_piece, container, false)
        binding = FragmentOnePieceBinding.inflate(layoutInflater)
        outfit = Outfit()

        viewPager2Dress = view.findViewById(R.id.vp2_dress)
        viewPager2Shoes = view.findViewById(R.id.vp2_shoes)
        viewPager2Extra = view.findViewById(R.id.vp2_extra)
        viewPager2Bags = view.findViewById(R.id.vp2_bags)
        viewPager2Accessories = view.findViewById(R.id.vp2_acessories)

        itemArrayListDresses = arrayListOf()
        itemArrayListShoes = arrayListOf()
        itemArrayListTops = arrayListOf()
        itemArrayListBags = arrayListOf()
        itemArrayListAccessories = arrayListOf()

        adapterDress = ImageAdapter(itemArrayListDresses,viewPager2Dress)
        adapterShoes = ImageAdapter(itemArrayListShoes,viewPager2Shoes)
        adapterTops = ImageAdapter(itemArrayListTops, viewPager2Extra)
        adapterBags = ImageAdapter(itemArrayListBags,viewPager2Bags)
        adapterAccessories = ImageAdapter(itemArrayListAccessories,viewPager2Accessories)

        viewPager2Dress.adapter = adapterDress
        viewPager2Dress.offscreenPageLimit = 3
        viewPager2Dress.clipToPadding = false
        viewPager2Dress.clipChildren = false
        viewPager2Dress.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS

        viewPager2Shoes.adapter = adapterShoes
        viewPager2Shoes.offscreenPageLimit = 3
        viewPager2Shoes.clipToPadding = false
        viewPager2Shoes.clipChildren = false
        viewPager2Shoes.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS

        viewPager2Extra.adapter = adapterTops
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

        EventChangeListenerDress()
        EventChangeListenerShoes()
        EventChangeListenerExtra()
        EventChangeListenerBags()
        EventChangeListenerAccessories()

        val btnSaveOutfit = view.findViewById<Button>(R.id.btn_save_outfit)

        btnSaveOutfit.setOnClickListener {
            outfit.images.add(itemDress.image)
            outfit.items.add(itemDress)
            outfit.images.add(itemShoes.image)
            outfit.items.add(itemShoes)
            if(viewPager2Extra.isVisible){
                Toast.makeText(this@OnePieceFragment.requireContext(),"extra selected",
                    Toast.LENGTH_LONG)
                outfit.images.add(itemExtra.image)
                outfit.items.add(itemExtra)
            }
            if(viewPager2Bags.isVisible){
                outfit.images.add(itemBag.image)
                outfit.items.add(itemBag)
            }
            if(viewPager2Accessories.isVisible){
                outfit.images.add(itemAccessory.image)
                outfit.items.add(itemAccessory)
            }
            saveDialog()
        }
        val btnShuffle = view.findViewById<ImageView>(R.id.iv_shuffle)

        btnShuffle.setOnClickListener {
            shuffle()
        }

        viewPager2Dress.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                itemDress = itemArrayListDresses[position]
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
                itemExtra = itemArrayListTops[position]
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

    private fun shuffle() {
        val randomDress = (0..itemArrayListDresses.size).random()
        viewPager2Dress.currentItem = randomDress

        val randomShoes = (0..itemArrayListShoes.size).random()
        viewPager2Shoes.currentItem = randomShoes

        if(viewPager2Extra.isVisible){
            val randomTop = (0..itemArrayListTops.size).random()
            viewPager2Extra.currentItem = randomTop
        }
        if(viewPager2Bags.isVisible){
            val randomBag = (0..itemArrayListBags.size).random()
            viewPager2Bags.currentItem = randomBag
        }
        if (viewPager2Accessories.isVisible){
            val randomAcc = (0..itemArrayListAccessories.size).random()
            viewPager2Accessories.currentItem = randomAcc
        }
    }

    private fun setUpTransformer(){
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }

        viewPager2Dress.setPageTransformer(transformer)
        viewPager2Shoes.setPageTransformer(transformer)
        viewPager2Extra.setPageTransformer(transformer)
    }

    private fun saveDialog() {
        val dialog = Dialog(this@OnePieceFragment.requireContext())
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
            var builder: AlertDialog.Builder = AlertDialog.Builder(this@OnePieceFragment.requireContext())
            with(builder){
                setTitle("Outfit Category")
                setItems(Constants.outfit_category_options) { dialog, which ->
                    Toast.makeText(
                        this@OnePieceFragment.requireContext(),
                        Constants.outfit_category_options[which] + " is clicked",
                        Toast.LENGTH_LONG
                    ).show()
                    tvOutfitCategory.text = Constants.outfit_category_options[which]
                }
                show()
            }
        }

        llOutfitStyle.setOnClickListener {
            var builder: AlertDialog.Builder = AlertDialog.Builder(this@OnePieceFragment.requireContext());
            with(builder){
                setTitle("Item Style")
                setItems(Constants.style_options) { dialog, which ->
                    Toast.makeText(
                        this@OnePieceFragment.requireContext(),
                        Constants.style_options[which] + " is clicked",
                        Toast.LENGTH_LONG
                    ).show()
                    tvOutfitStyle.text = Constants.style_options[which]
                }
                show()
            }
        }

        fun validate() : Boolean{
            return when{
                TextUtils.isEmpty(etOutfitName.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(this@OnePieceFragment.requireContext(), resources.getString(R.string.err_msg_name_empty), Toast.LENGTH_LONG)
                    false
                }
                TextUtils.isEmpty(tvOutfitCategory.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(this@OnePieceFragment.requireContext(), resources.getString(R.string.err_msg_category_empty), Toast.LENGTH_LONG)
                    false
                }
                TextUtils.isEmpty(tvOutfitStyle.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(this@OnePieceFragment.requireContext(), resources.getString(R.string.err_msg_category_empty), Toast.LENGTH_LONG)
                    false
                }
                else -> {
                    true
                }
            }
        }

        btnSave.setOnClickListener {
            Toast.makeText(this@OnePieceFragment.requireContext(), "Saved", Toast.LENGTH_LONG).show()
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
                outfit = outfitToBeAdded
                FirestoreClass().addOutfitToDatabase(this, outfitToBeAdded)
            }

        }


        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    @Override
    public fun onBackPressed() {
    }

    override fun outfitAddedSuccessfully() {
        Toast.makeText(
            this@OnePieceFragment.requireContext(),
            resources.getString(R.string.item_added_successfully),
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(this@OnePieceFragment.requireContext(), OutfitDetailsActivity::class.java)
        intent.putExtra("outfit", outfit)
        startActivity(intent)
        //startActivity(Intent(this@OnePieceFragment.requireContext(), Outfits()::class.java))
        //onBackPressed()
        //this.activity?.finish()
//        val navController = findNavController()
//        navController.navigateUp() // to clear previous navigation history
//        navController.navigate(R.id.cl_outfits_container)
    }

    private fun EventChangeListenerDress() {
        val items : String = Constants.USERS+"/"+ userUid + "/" + Constants.ITEMS
        dbD = FirebaseFirestore.getInstance()
        dbD.collection(items)
            .whereEqualTo(Constants.CATEGORY, Constants.category_options[2])
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
                            itemArrayListDresses.add(dc.document.toObject(Item::class.java))
                        }
                    }
                    adapterDress.notifyDataSetChanged()
                }
            })
    }

    private fun EventChangeListenerShoes() {
        val items : String = Constants.USERS+"/"+ userUid + "/" + Constants.ITEMS
        dbS = FirebaseFirestore.getInstance()
        dbS.collection(items)
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
    private fun EventChangeListenerExtra() {
        val items: String = Constants.USERS + "/" + userUid + "/" + Constants.ITEMS
        dbT = FirebaseFirestore.getInstance()
        dbT.collection(items)
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
                            itemArrayListTops.add(dc.document.toObject(Item::class.java))
                        }
                    }
                    adapterTops.notifyDataSetChanged()
                }
            })
    }

    private fun EventChangeListenerBags() {
        val items: String = Constants.USERS + "/" + userUid + "/" + Constants.ITEMS
        dbB = FirebaseFirestore.getInstance()
        dbB.collection(items)
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
        dbA = FirebaseFirestore.getInstance()
        dbA.collection(items)
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
         * @return A new instance of fragment OnePieceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OnePieceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}