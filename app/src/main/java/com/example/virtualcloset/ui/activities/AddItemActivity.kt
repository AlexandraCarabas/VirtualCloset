package com.example.virtualcloset.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.ActivityAddItemBinding
import com.example.virtualcloset.firestore.FirestoreClass
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.ui.ColorSpinnerAdapter
import com.example.virtualcloset.utils.ColorList
import com.example.virtualcloset.utils.ColorObject
import com.example.virtualcloset.utils.Constants
import com.example.virtualcloset.utils.GlideLoader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.jar.Manifest
import kotlin.random.Random

class AddItemActivity : BaseActivity() {

    private lateinit var binding: ActivityAddItemBinding
    lateinit var selectedColor: ColorObject
    internal var imagePath:String? = ""
    private var mSelectedImageFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add_item)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadColorSpinner()

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
                showImageChooser(this@AddItemActivity)
            } else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.ivArrowBack.setOnClickListener {  }

        binding.llItemCategory.setOnClickListener {
            categoryDialog()
        }

        binding.llItemStyle.setOnClickListener {
            styleDialog()
        }

        binding.btnAddItem.setOnClickListener {
            addItem()
        }
    }

    fun takePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(cameraIntent)
    }

    private fun loadColorSpinner() {
        selectedColor = ColorList().defaultColor
        binding.colorSpinner.apply {
            adapter = ColorSpinnerAdapter(applicationContext, ColorList().basicColors())
            setSelection(ColorList().colorPosition(selectedColor),false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?,p1: View?, position: Int, p3: Long){
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
                showImageChooser(this@AddItemActivity)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permision_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun categoryDialog() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this);
        with(builder){
            setTitle("Item Category")
            setItems(Constants.category_options) { dialog, which ->
                Toast.makeText(
                    this@AddItemActivity,
                    Constants.category_options[which] + " is clicked",
                    Toast.LENGTH_LONG
                ).show()
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
                Toast.makeText(
                    this@AddItemActivity,
                    Constants.style_options[which] + " is clicked",
                    Toast.LENGTH_LONG
                ).show()
                binding.tvItemStyle.text =Constants.style_options[which]
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

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val thumbNail: Bitmap = result.data!!.extras!!.get("data") as Bitmap
            saveImage(thumbNail)
            Toast.makeText(
                this,
                "saved: "+imagePath,
                Toast.LENGTH_LONG
            ).show()
            binding.ivItemPhoto.setImageBitmap(thumbNail)
        }
    }

    private fun saveImage(bitmap: Bitmap){
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root+"/capture_photo")
        Toast.makeText(
            this,
            root,
            Toast.LENGTH_LONG
        ).show()
        myDir.mkdirs()
        val generator = java.util.Random()
        var n = 10000
        n = generator.nextInt(n)
        val OutletFname = "Image-$n.jpg"
        val file = File(myDir, OutletFname)
        if(file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,out)
            imagePath = file.absolutePath
            out.flush()
            out.close()
        }catch (e: Exception) {
            e.printStackTrace()
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
                        this@AddItemActivity,
                        resources.getString(R.string.image_selection_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun addItem(){
        if(validateData()) {
            val itemName = binding.etItemName.text.toString().trim{ it <= ' '}
            val color = selectedColor.name
            val itemPattern = binding.etItemPattern.text.toString().trim{ it <= ' '}
            val category = binding.tvItemCategory.text.toString()
            val itemSize = binding.etItemSize.text.toString()
            val itemStyle = binding.tvItemStyle.text.toString()

            if(mSelectedImageFileUri==null){
                val item = Item(
                    System.currentTimeMillis().toString(),
                    itemName,
                    color,
                    itemPattern,
                    category,
                    itemSize,
                    itemStyle
                )

                FirestoreClass().addItemToDatabase(this@AddItemActivity,item)
            }
            else{
                val item = Item(
                    System.currentTimeMillis().toString(),
                    itemName,
                    color,
                    itemPattern,
                    category,
                    itemSize,
                    itemStyle,
                    mSelectedImageFileUri.toString()
                )

                FirestoreClass().addItemToDatabase(this@AddItemActivity,item)
            }
        }
    }

    fun itemAddedSuccessfully() {
        Toast.makeText(
            this@AddItemActivity,
            resources.getString(R.string.item_added_successfully),
            Toast.LENGTH_LONG
        ).show()
        startActivity(Intent(this@AddItemActivity,NavigationActivity::class.java))
        finish()
    }
}