package com.example.virtualcloset.ui.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.virtualcloset.databinding.FragmentHomeBinding
import com.example.virtualcloset.R
import com.example.virtualcloset.models.User
import com.example.virtualcloset.utils.Constants

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var binding: FragmentHomeBinding
    private lateinit var myTextView: TextView

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
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        binding = FragmentHomeBinding.inflate(layoutInflater)

        val sharedPreferences = this.getActivity()?.getSharedPreferences(Constants.VIRTUALCLOSET_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences?.getString(Constants.SIGNED_IN_USERNAME, "")!!
//        myTextView = binding.nameTextView
//
//        val bundle = arguments
//        val message = bundle!!.getString("mText")
//
//        myTextView.text = message
        //binding.nameTextView.text =
        val mName : TextView = view.findViewById<TextView>(R.id.name_textView)
        mName.text = username
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}