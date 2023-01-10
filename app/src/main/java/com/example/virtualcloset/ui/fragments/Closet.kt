package com.example.virtualcloset.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_closet, container, false)

        binding = FragmentClosetBinding.inflate(layoutInflater)

//        binding.topsIcon.setOnClickListener {
//            val intent = Intent(activity,AddItemActivity::class.java)
//            getActivity()?.startActivity(Intent(intent))
//        }
        val topsView = view.findViewById<ImageView>(R.id.tops_icon)
        val bottomsView = view.findViewById<ImageView>(R.id.bottoms_icon)
        val dressesView = view.findViewById<ImageView>(R.id.dresses_icon)
        val accessoriesView = view.findViewById<ImageView>(R.id.accessories_icon)
        val bagsView = view.findViewById<ImageView>(R.id.bags_icon)
        val shoesView = view.findViewById<ImageView>(R.id.shoesIcon)


        topsView.setOnClickListener {
            val intent = Intent(this.activity,CategoryItemsActivity::class.java)
            intent.putExtra(Constants.CATEGORY, 0)
            getActivity()?.startActivity(Intent(intent))
        }

        bottomsView.setOnClickListener {
            val intent = Intent(this.activity,CategoryItemsActivity::class.java)
            intent.putExtra(Constants.CATEGORY, 1)
            getActivity()?.startActivity(Intent(intent))
        }

        dressesView.setOnClickListener {
            val intent = Intent(this.activity,CategoryItemsActivity::class.java)
            intent.putExtra(Constants.CATEGORY, 2)
            getActivity()?.startActivity(Intent(intent))
        }

        accessoriesView.setOnClickListener {
            val intent = Intent(this.activity,CategoryItemsActivity::class.java)
            intent.putExtra(Constants.CATEGORY, 3)
            getActivity()?.startActivity(Intent(intent))
        }

        bagsView.setOnClickListener {
            val intent = Intent(this.activity,CategoryItemsActivity::class.java)
            intent.putExtra(Constants.CATEGORY, 4)
            getActivity()?.startActivity(Intent(intent))
        }

        shoesView.setOnClickListener {
            val intent = Intent(this.activity,CategoryItemsActivity::class.java)
            intent.putExtra(Constants.CATEGORY, 5)
            getActivity()?.startActivity(Intent(intent))
        }


        return view
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