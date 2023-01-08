package com.example.virtualcloset

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.virtualcloset.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class NavigationActivity : AppCompatActivity() {

    private val homeFragment = Home()
    private val closetFragment = Closet()
    private val outfitFragment = Home()

    private lateinit var currentFragment : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        val sharedPreferences = getSharedPreferences(Constants.VIRTUALCLOSET_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.SIGNED_IN_USERNAME, "")!!

        Toast.makeText(
            this@NavigationActivity,
            "Hello $username",
            Toast.LENGTH_LONG
        ).show()
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, homeFragment).commit()
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

    private fun replaceFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.commit()
    }
}