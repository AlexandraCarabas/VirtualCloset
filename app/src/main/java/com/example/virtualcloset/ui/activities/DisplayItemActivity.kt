package com.example.virtualcloset.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.ActivityDisplayItemBinding
import com.example.virtualcloset.firestore.FirestoreClass
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.ui.ColorSpinnerAdapter
import com.example.virtualcloset.utils.ColorList
import com.example.virtualcloset.utils.ColorObject
import com.example.virtualcloset.utils.Constants
import com.example.virtualcloset.utils.GlideLoader
import java.io.IOException


class DisplayItemActivity : BaseActivity() {

    private lateinit var binding: ActivityDisplayItemBinding
    private var isEditable = false
    lateinit var selectedColor: ColorObject
    private var mSelectedImageFileUri: Uri? = null
    var dColor: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_display_item)

        val myItem = intent.getParcelableExtra<Item>("item")

        binding.tvTitle.text = myItem?.name
        dColor = myItem?.color!!
        loadColorSpinner()

        isEditable = false
        binding.ivTakePhoto.visibility = View.INVISIBLE
        binding.ivUploadPhoto.visibility = View.INVISIBLE
        binding.etItemName.isEnabled = false
        binding.colorSpinner.isEnabled = false
        binding.etItemPattern.isEnabled = false
        binding.etItemSize.isEnabled = false
        binding.tvItemCategory.isEnabled = false
        binding.tvItemStyle.isEnabled = false
        binding.btnSaveItem.visibility = View.INVISIBLE

        binding.ivItemPhoto.setImageURI(myItem?.image?.toUri())
        binding.etItemName.setText(myItem?.name)
        binding.etItemPattern.setText(myItem?.pattern)
        binding.etItemSize.setText(myItem?.size)
        binding.tvItemStyle.setText(myItem?.style)
        binding.tvItemCategory.setText(myItem?.category)


        binding.swEditable.setOnCheckedChangeListener{ _ , isChecked ->
            if(isChecked){
                isEditable = true
                binding.ivTakePhoto.visibility = View.VISIBLE
                binding.ivUploadPhoto.visibility = View.VISIBLE
                binding.etItemName.isEnabled = true
                binding.colorSpinner.isEnabled = true
                binding.etItemPattern.isEnabled = true
                binding.etItemSize.isEnabled = true
                binding.tvItemCategory.isEnabled = true
                binding.tvItemStyle.isEnabled = true
                binding.btnSaveItem.visibility = View.VISIBLE

            }else{
                isEditable = false
                binding.ivTakePhoto.visibility = View.INVISIBLE
                binding.ivUploadPhoto.visibility = View.INVISIBLE
                binding.etItemName.isEnabled = false
                binding.colorSpinner.isEnabled = false
                binding.etItemPattern.isEnabled = false
                binding.etItemSize.isEnabled = false
                binding.tvItemCategory.isEnabled = false
                binding.tvItemStyle.isEnabled = false
                binding.btnSaveItem.visibility = View.INVISIBLE
            }
        }

        binding.ivTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    Constants.CAMERA_PERMISSIONS_CODE
                )

            }
            if(ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )!= PackageManager.PERMISSION_GRANTED
            ){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.WRITE_STORAGE_PERMISSION_CODE
                )
            }else{
                takePhoto()
            }
        }

        binding.ivUploadPhoto.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
//                showErrorSnackBar("You already have the storage permission.",false)
                showImageChooser(this@DisplayItemActivity)
            } else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.ivArrowBack.setOnClickListener {
            onBackPressed()
        }

        binding.llItemCategory.setOnClickListener {
            categoryDialog()
        }

        binding.llItemStyle.setOnClickListener {
            styleDialog()
        }

        binding.btnSaveItem.setOnClickListener {
            val itemHashMap = HashMap<String, Any>()
            if(validateData()){
                val itemID = myItem?.id!!
                itemHashMap[Constants.ITEM_NAME] = binding.etItemName.text.toString()
                itemHashMap[Constants.ITEM_PATTERN] = binding.etItemPattern.text.toString()
                itemHashMap[Constants.ITEM_CATEGORY] = binding.tvItemCategory.text.toString()
                itemHashMap[Constants.ITEM_SIZE] = binding.etItemSize.text.toString()
                itemHashMap[Constants.ITEM_STYLE] = binding.tvItemStyle.text.toString()
                itemHashMap[Constants.ITEM_IMAGE] = mSelectedImageFileUri.toString()

                FirestoreClass().updateItemToDatabase(this,itemID, itemHashMap)
            }
        }



    }

    fun itemUpdatedSuccessfully(){
        showErrorSnackBar("Item updated successfully!",false)
        binding.swEditable.isChecked = false
    }

    fun takePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(cameraIntent)
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val thumbNail: Bitmap = result.data!!.extras!!.get("data") as Bitmap
            binding.ivItemPhoto.setImageBitmap(thumbNail)
        }
    }

    private fun loadColorSpinner() {
        selectedColor = ColorList().defaultColor
        var listColor = ColorList().basicColors()
        for(c in listColor){
            if(c.name == dColor){
                selectedColor = c
            }
        }
        binding.colorSpinner.apply {
            adapter = ColorSpinnerAdapter(applicationContext, ColorList().basicColors())
            setSelection(ColorList().colorPosition(selectedColor),false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long){
                    selectedColor = ColorList().basicColors()[position]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.CAMERA_PERMISSIONS_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent( MediaStore.ACTION_IMAGE_CAPTURE)
                //startActivityForResult(intent,Constants.CAMERA_REQUEST_CODE)
                resultLauncher.launch(intent)
            }else{
                Toast.makeText(
                    this,
                    "You denied the permission for camera. You can allow it in the settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                showErrorSnackBar("The storage permission is granted", false)
                showImageChooser(this@DisplayItemActivity)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permision_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        resultLauncher2.launch(galleryIntent)
    }

    var resultLauncher2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
//            val thumbNail: Bitmap = result.data!!.extras!!.get("data") as Bitmap
//            binding.ivItemPhoto.setImageBitmap(thumbNail)
            if(data != null) {
                try {
                    mSelectedImageFileUri = data.data!!

                    //binding.ivItemPhoto.setImageURI(selectedImageFileUri)
                    GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, binding.ivItemPhoto)
                }catch ( e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@DisplayItemActivity,
                        resources.getString(R.string.image_selection_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun categoryDialog() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this);
        with(builder){
            setTitle("Item Category")
            setItems(Constants.category_options) { dialog, which ->
//                Toast.makeText(
//                    this@DisplayItemActivity,
//                    Constants.category_options[which] + " is clicked",
//                    Toast.LENGTH_LONG
//                ).show()
                binding.tvItemCategory.text = Constants.category_options[which]
            }
            show()
        }
    }

    private fun styleDialog() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this);
        with(builder){
            setTitle("Item Style")
            setItems(Constants.style_options) { dialog, which ->
//                Toast.makeText(
//                    this@DisplayItemActivity,
//                    Constants.style_options[which] + " is clicked",
//                    Toast.LENGTH_LONG
//                ).show()
                binding.tvItemStyle.text = Constants.style_options[which]
            }
            show()
        }
    }

    private fun validateData() : Boolean{
        return when{
            TextUtils.isEmpty(binding.etItemName.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_name_empty), true)
                false
            }
            TextUtils.isEmpty(binding.tvItemCategory.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_category_empty), true)
                false
            }
            TextUtils.isEmpty(binding.tvItemStyle.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_category_empty), true)
                false
            }
            TextUtils.isEmpty(binding.etItemPattern.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_pattern_empty), true)
                false
            }
            TextUtils.isEmpty(binding.etItemSize.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_size_empty), true)
                false
            }
            else -> {
                showErrorSnackBar(resources.getString(R.string.item_added_successfully), false)
                true
            }
        }
    }

}