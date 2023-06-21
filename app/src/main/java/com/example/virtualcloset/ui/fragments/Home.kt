package com.example.virtualcloset.ui.fragments


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.virtualcloset.databinding.FragmentHomeBinding
import com.example.virtualcloset.R
import com.example.virtualcloset.models.News
import com.example.virtualcloset.ui.RecyclerViewAdapterNews
import com.example.virtualcloset.ui.activities.SignInActivity
import com.example.virtualcloset.utils.Constants
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment(), RecyclerViewAdapterNews.OnNewsClick{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var binding: FragmentHomeBinding
    private lateinit var myTextView: TextView
    private lateinit var myAdaptor: RecyclerViewAdapterNews
    private lateinit var newsArrayList: ArrayList<News>

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
        val mName : TextView = view.findViewById<TextView>(R.id.name_textView)
        mName.text = username

        val logout_btn = view.findViewById<ImageView>(R.id.btn_logout)
        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.startActivity(Intent(this.activity,SignInActivity::class.java))
            activity?.finish()
        }

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerViewNews)
        recyclerView.layoutManager = LinearLayoutManager(this@Home.requireContext())

        myAdaptor = RecyclerViewAdapterNews(this)
        recyclerView.adapter = myAdaptor

        fetchNews()

        return view
    }



    fun fetchNews() {
        val queue = Volley.newRequestQueue(this@Home.requireContext())
        val url = "https://newsapi.ai/api/v1/article/getArticles?query=%7B%22%24query%22%3A%7B%22%24and%22%3A%5B%7B%22conceptUri%22%3A%22http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FFashion%22%7D%2C%7B%22categoryUri%22%3A%22dmoz%2FShopping%2FClothing%22%7D%5D%7D%2C%22%24filter%22%3A%7B%22forceMaxDataTimeWindow%22%3A%2231%22%7D%7D&resultType=articles&articlesSortBy=date&articlesCount=10&articleBodyLen=-1&apiKey=d03f661e-a943-4fe1-bdb1-d0cc65a7a848"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener {
                val newsJsonArray = it.getJSONObject("articles").getJSONArray("results")
                val newsArray = ArrayList<News>()
                for(i in 0 until newsJsonArray.length()){
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News (
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("image")
                    )
                    newsArray.add(news)
                }
                myAdaptor.updateData(newsArray)
            },
            Response.ErrorListener {

            }
        )
        queue.add(jsonObjectRequest)
    }

    override fun onClicked(news: News) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this@Home.requireContext(), Uri.parse(news.url))
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