package com.example.virtualcloset.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.FragmentClosetBinding
import com.example.virtualcloset.databinding.FragmentHomeBinding
import com.example.virtualcloset.ui.activities.*
import com.example.virtualcloset.utils.Constants

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Closet.newInstance] factory method to
 * create an instance of this fragment.
 */
class Closet : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentClosetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    var permissionsOk : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_closet, container, false)

        binding = FragmentClosetBinding.inflate(layoutInflater)

        val topsView = view.findViewById<ImageView>(R.id.tops_icon)
        val bottomsView = view.findViewById<ImageView>(R.id.bottoms_icon)
        val dressesView = view.findViewById<ImageView>(R.id.dresses_icon)
        val accessoriesView = view.findViewById<ImageView>(R.id.accessories_icon)
        val bagsView = view.findViewById<ImageView>(R.id.bags_icon)
        val shoesView = view.findViewById<ImageView>(R.id.shoesIcon)
        val addItemBtn = view.findViewById<ImageView>(R.id.ivAddItem)


        topsView.setOnClickListener {
            checkReadStoragePermission()
            if(permissionsOk) {
                val intent = Intent(this.activity, CategoryItemsActivity::class.java)
                intent.putExtra(Constants.CATEGORY, 0)
                getActivity()?.startActivity(Intent(intent))
            }else{
                Toast.makeText(
                    this.requireContext(),
                    "Storage Permission required for this action! Go to settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        bottomsView.setOnClickListener {
            checkReadStoragePermission()
            if(permissionsOk) {
                val intent = Intent(this.activity, CategoryItemsActivity::class.java)
                intent.putExtra(Constants.CATEGORY, 1)
                getActivity()?.startActivity(Intent(intent))
            }else{
                Toast.makeText(
                    this.requireContext(),
                    "Storage Permission required for this action! Go to settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        dressesView.setOnClickListener {
            checkReadStoragePermission()
            if(permissionsOk) {
                val intent = Intent(this.activity, CategoryItemsActivity::class.java)
                intent.putExtra(Constants.CATEGORY, 2)
                getActivity()?.startActivity(Intent(intent))
            }else{
                Toast.makeText(
                    this.requireContext(),
                    "Storage Permission required for this action! Go to settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        accessoriesView.setOnClickListener {
            checkReadStoragePermission()
            if(permissionsOk) {
                val intent = Intent(this.activity, CategoryItemsActivity::class.java)
                intent.putExtra(Constants.CATEGORY, 3)
                getActivity()?.startActivity(Intent(intent))
            }else{
                Toast.makeText(
                    this.requireContext(),
                    "Storage Permission required for this action! Go to settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        bagsView.setOnClickListener {
            checkReadStoragePermission()
            if(permissionsOk) {
                val intent = Intent(this.activity, CategoryItemsActivity::class.java)
                intent.putExtra(Constants.CATEGORY, 4)
                getActivity()?.startActivity(Intent(intent))
            }else{
                Toast.makeText(
                    this.requireContext(),
                    "Storage Permission required for this action! Go to settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        shoesView.setOnClickListener {
            checkReadStoragePermission()
            if(permissionsOk) {
                val intent = Intent(this.activity, CategoryItemsActivity::class.java)
                intent.putExtra(Constants.CATEGORY, 5)
                getActivity()?.startActivity(Intent(intent))
            }else{
                Toast.makeText(
                    this.requireContext(),
                    "Storage Permission required for this action! Go to settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        addItemBtn.setOnClickListener {
            val intent = Intent(this.activity, AddItemActivity::class.java)
            getActivity()?.startActivity(intent)
        }

        return view
    }

    private fun checkReadStoragePermission(){
        if(ActivityCompat.checkSelfPermission(
                this.requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionsOk = true
        } else{
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.READ_STORAGE_PERMISSION_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    permissionsOk = true
                }else{
                    Toast.makeText(
                        this.activity,
                        "You denied permissions for local storage read. The command will not work",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Closet.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Closet().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}