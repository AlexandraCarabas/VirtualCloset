package com.example.virtualcloset.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.ActivityAddItemBinding
import com.example.virtualcloset.utils.Constants
import java.io.IOException
import java.util.jar.Manifest

class AddItemActivity : BaseActivity() {

    private lateinit var binding: ActivityAddItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add_item)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent( MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(intent)


            }else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    Constants.CAMERA_PERMISSIONS_CODE
                )
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

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val thumbNail: Bitmap = result.data!!.extras!!.get("data") as Bitmap
            binding.ivItemPhoto.setImageBitmap(thumbNail)
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
                    val selectedImageFileUri = data.data!!

                    binding.ivItemPhoto.setImageURI(selectedImageFileUri)
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
}