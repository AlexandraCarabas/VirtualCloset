package com.example.virtualcloset.ui.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
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
import androidx.core.content.FileProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.virtualcloset.BuildConfig
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import kotlin.jvm.Throws

typealias LumaListener = (luma: Double) -> Unit

class AddItemActivity : BaseActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private lateinit var cameraExecutor: ExecutorService

    lateinit var selectedColor: ColorObject
    internal var imagePath:String? = ""
    private var mSelectedImageFileUri: Uri? = null
    private var storageImagePath:String = ""
    private var itemCategory: Int = 0

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

        binding.ivArrowBack.setOnClickListener {
            onBackPressed()
        }

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

//    companion object {
//        private const val TAG = "CameraXApp"
//        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
//        private val REQUIRED_PERMISSIONS =
//            mutableListOf (
//                Manifest.permission.CAMERA,
//            ).apply {
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                }
//            }.toTypedArray()
//    }


//    @Throws(IOException::class)
//    private fun createImageFile(): File{
//        // Create an image file name
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "JPEG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        ).apply {
//            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = absolutePath
//        }
//    }

    fun takePhoto() {
        //val cameraIntent =
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            takePictureIntent.resolveActivity(packageManager)?.also {
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//                    showErrorSnackBar("Error occurred while creating the Tile", true)
//                    null
//                }
//                photoFile?.also {
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        this,
//                        "com.example.virtualcloset",
//                        it
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    resultLauncher.launch(takePictureIntent)
//                }
//            }
//
//        }
        lifecycleScope.launchWhenStarted {
            getTempFileUri().let { uri->
                latestTmpUri = uri
                resultLauncher.launch(uri)
            }
        }
        //resultLauncher.launch(cameraIntent)
    }

    private fun getTempFileUri(): Uri{
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

//    private fun galleryAddPic() {
////        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
////            val f = File(currentPhotoPath)
////            mediaScanIntent.data = Uri.fromFile(f)
////            sendBroadcast(mediaScanIntent)
////        }
//
//        val file = File(currentPhotoPath)
//        MediaScannerConnection.scanFile(this, arrayOf(file.toString()),null,null)
//    }

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
//                val intent = Intent( MediaStore.ACTION_IMAGE_CAPTURE)
//                //startActivityForResult(intent,Constants.CAMERA_REQUEST_CODE)
//                resultLauncher.launch(intent)
                takePhoto()
                //startCamera()
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

//    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if(result.resultCode == Activity.RESULT_OK) {
//            val data: Intent? = result.data
//            val thumbNail: Bitmap = result.data!!.extras!!.get("data") as Bitmap
//            val phUri: Uri = result.data!!.extras!!.get("photoURI") as Uri
//            saveImage(thumbNail)
//            galleryAddPic()
//            Toast.makeText(
//                this,
//                "saved: "+imagePath,
//                Toast.LENGTH_LONG
//            ).show()
//            binding.ivItemPhoto.setImageBitmap(thumbNail)
//        }
//    }

    private var latestTmpUri: Uri? = null

    var resultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                binding.ivItemPhoto.setImageURI(uri)
                mSelectedImageFileUri = uri
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
            var position : Int = 0
            for(pos: String in Constants.category_options ){
                if(category == pos) {
                    itemCategory = position
                }
                position += 1
            }

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
                FirestoreClass().addItemToDatabase(this@AddItemActivity,item,mSelectedImageFileUri!!)
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

                FirestoreClass().addItemToDatabase(this@AddItemActivity,item,mSelectedImageFileUri!!)
            }
        }
    }

    fun itemAddedSuccessfully() {
        Toast.makeText(
            this@AddItemActivity,
            resources.getString(R.string.item_added_successfully),
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(this@AddItemActivity,CategoryItemsActivity::class.java)
        intent.putExtra(Constants.CATEGORY, itemCategory)
        startActivity(intent)
        //onBackPressed()
        //finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}