package com.example.virtualcloset.ui.activities

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.virtualcloset.ui.fragments.Closet
import com.example.virtualcloset.ui.fragments.Home
import com.example.virtualcloset.R
import com.example.virtualcloset.models.User
import com.example.virtualcloset.ui.fragments.Outfits
import com.example.virtualcloset.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class NavigationActivity : BaseActivity() {

    private val homeFragment = Home()
    private val closetFragment = Closet()
    private val outfitFragment = Outfits()

    private lateinit var currentFragment : Fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        val sharedPreferences = getSharedPreferences(Constants.VIRTUALCLOSET_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.SIGNED_IN_USERNAME, "")!!
        val userUid = sharedPreferences.getString(Constants.SIGNED_IN_UID,"")!!

        var userDetails: User = User ()
        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
        if(intent.hasExtra((Constants.CLOSET))){
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, closetFragment).commit()
            //Toast.makeText(this,"Trebuie sa intru in closet",Toast.LENGTH_LONG).show()
        }
        else {
            if (intent.hasExtra(Constants.OUTFITS)) {
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout, outfitFragment)
                    .commit()
                //Toast.makeText(this,"Trebuie sa intru in outfits",Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this@NavigationActivity,
                    "Hello $username",
                    Toast.LENGTH_LONG
                ).show()
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout, homeFragment).commit()
            }
        }

//        Toast.makeText(
//            this@NavigationActivity,
//            "Hello $username",
//            Toast.LENGTH_LONG
//        ).show()
//        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, homeFragment).commit()
        var bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener(bottomListener)
    }


    val bottomListener = NavigationBarView.OnItemSelectedListener {
        // switch between ids of menu
        when(it.itemId){
            R.id.item_1 -> {
                currentFragment = homeFragment
            }
            R.id.item_2 -> {
                currentFragment = closetFragment
            }
            R.id.item_3 -> {
                currentFragment = outfitFragment
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout,currentFragment).commit()
        true
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }

}