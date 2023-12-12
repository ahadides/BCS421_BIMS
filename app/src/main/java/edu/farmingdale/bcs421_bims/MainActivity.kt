package edu.farmingdale.bcs421_bims

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import edu.farmingdale.bcs421_bims.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(DashFragment())

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        // Load the ad.
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        
        binding.navView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.navigation_dashboard -> replaceFragment(DashFragment())
                R.id.navigation_Settings -> replaceFragment(SearchFragment())
                R.id.navigation_Search -> replaceFragment(SearchFragment())
                else ->{
                }
            }
            true
        }
    }
    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fram_layout,fragment,)
        fragmentTransaction.commit()
    }


}