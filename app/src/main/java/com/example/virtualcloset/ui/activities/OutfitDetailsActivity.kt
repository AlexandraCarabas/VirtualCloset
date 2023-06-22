package com.example.virtualcloset.ui.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.ActivityOutfitDetailsBinding
import com.example.virtualcloset.firestore.FirestoreClass
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.models.Outfit
import com.example.virtualcloset.ui.RecyclerViewAdapter
import com.example.virtualcloset.ui.fragments.Outfits
import com.example.virtualcloset.utils.Constants
import com.google.firebase.firestore.*

class OutfitDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityOutfitDetailsBinding

    private lateinit var recyclerView : RecyclerView
    private lateinit var itemArrayList: ArrayList<Item>
    private lateinit var myAdapter: RecyclerViewAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var outfitID : String

    private var isEditable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutfitDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myOutfit = intent.getParcelableExtra<Outfit>("outfit")
        outfitID = myOutfit!!.id

        recyclerView = findViewById(R.id.rv_items)
        itemArrayList = myOutfit.items

        myAdapter = RecyclerViewAdapter(itemArrayList)
        recyclerView.adapter = myAdapter
        myAdapter.setOnItemClickListener(object: RecyclerViewAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
//                Toast.makeText(
//                    this@CategoryItemsActivity,
//                    "You clicked on item nr. $position",
//                    Toast.LENGTH_SHORT
//                ).show()
//                val intent = Intent(this@CategoryItemsActivity, DisplayItemActivity::class.java)
//                intent.putExtra("item", itemArrayList[position])
//                startActivity(intent)
            }

        })

        //EventChangeListener()

        binding.tvTitle.text = myOutfit?.name

        isEditable = false
        binding.etOutfitName.isEnabled = false
        binding.tvOutfitCategory.isEnabled = false
        binding.tvOutfitStyle.isEnabled = false
        binding.btnSaveOutfit.visibility = View.INVISIBLE

        binding.etOutfitName.setText(myOutfit.name)
        binding.tvOutfitCategory.setText(myOutfit.style)
        binding.tvOutfitStyle.setText(myOutfit.category)

        binding.ivDeleteOutfit.setOnClickListener {
            delete_outfit(outfitID)
        }

        binding.swEditable.setOnCheckedChangeListener{ _ , isChecked ->
            if(isChecked){
                isEditable = true
                binding.etOutfitName.isEnabled = true
                binding.tvOutfitCategory.isEnabled = true
                binding.tvOutfitStyle.isEnabled = true
                binding.btnSaveOutfit.visibility = View.VISIBLE

            }else{
                isEditable = false
                binding.etOutfitName.isEnabled = false
                binding.tvOutfitCategory.isEnabled = false
                binding.tvOutfitStyle.isEnabled = false
                binding.btnSaveOutfit.visibility = View.INVISIBLE
            }
        }

        binding.ivArrowBack.setOnClickListener {
            onBackPressed()
        }

        binding.llOutfitCategory.setOnClickListener {
            categoryDialog()
        }

        binding.llOutfitStyle.setOnClickListener {
            styleDialog()
        }

        binding.btnSaveOutfit.setOnClickListener {
            val itemHashMap = HashMap<String, Any>()
            if(validateData()){
                itemHashMap[Constants.OUTFIT_NAME] = binding.etOutfitName.text.toString()
                itemHashMap[Constants.OUTFIT_CATEGORY] = binding.tvOutfitCategory.text.toString()
                itemHashMap[Constants.OUTFIT_STYLE] = binding.tvOutfitStyle.text.toString()
                itemHashMap[Constants.OUTFIT_IMAGES] = myOutfit.images
                itemHashMap[Constants.OUTFIT_ITEMS] = myOutfit.items

                FirestoreClass().updateOutfitToDatabase(this,outfitID, itemHashMap)
            }
        }

    }

    private fun delete_outfit(deleteID: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.delete_custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnYes : Button = dialog.findViewById(R.id.btn_yes)
        val btnNo : Button = dialog.findViewById(R.id.btn_no)

        btnYes.setOnClickListener {
            FirestoreClass().deleteOutfitFromDatabase(this, deleteID)
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun EventChangeListener() {
        val sharedPreferences = getSharedPreferences(Constants.VIRTUALCLOSET_PREFERENCES, Context.MODE_PRIVATE)
        val userUid = sharedPreferences?.getString(Constants.SIGNED_IN_UID,"")!!

//        //var itemList : ArrayList<Item> =
//
        val outfits : String = Constants.USERS+"/"+ userUid + "/" + Constants.OUTFITS

        db = FirebaseFirestore.getInstance()
        db.collection(outfits)
            .whereEqualTo(Constants.OUTFIT_ID, outfitID)
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

    private fun categoryDialog() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this);
        with(builder){
            setTitle("Outfit Category")
            setItems(Constants.outfit_category_options) { dialog, which ->
                binding.tvOutfitCategory.text = Constants.outfit_category_options[which]
            }
            show()
        }
    }

    private fun styleDialog() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this);
        with(builder){
            setTitle("Outfit Style")
            setItems(Constants.style_options) { dialog, which ->
                binding.tvOutfitStyle.text = Constants.style_options[which]
            }
            show()
        }
    }

    private fun validateData() : Boolean{
        return when{
            TextUtils.isEmpty(binding.etOutfitName.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_name_empty), true)
                false
            }
            TextUtils.isEmpty(binding.tvOutfitCategory.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_category_empty), true)
                false
            }
            TextUtils.isEmpty(binding.tvOutfitStyle.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_style_empty), true)
                false
            }
            else -> {
                showErrorSnackBar(resources.getString(R.string.outfit_added_successfully), false)
                true
            }
        }
    }

    fun outfitUpdatedSuccessfully(){
        showErrorSnackBar("Outfit updated successfully!",false)
        binding.swEditable.isChecked = false
    }

    fun outfitDeletedSuccessfully(){
        showErrorSnackBar("Outfit deleted successfully!",false)
//        val intent = Intent(this, Outfits::class.java)
//        startActivity(intent)
        //supportFragmentManager.beginTransaction().replace(,Outfits()).commit()
        onBackPressed()
        //supportFragmentManager.beginTransaction().replace(R.id.cl_outfits_container, Outfits()).commit()
    }

    override fun onBackPressed() {
        val intent = Intent(this, NavigationActivity::class.java)
        intent.putExtra(Constants.OUTFITS, 1)
        startActivity(intent)
    }

}