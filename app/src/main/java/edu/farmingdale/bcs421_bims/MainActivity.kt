package edu.farmingdale.bcs421_bims

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBtn = findViewById<Button>(R.id.searchBtn)

        searchBtn.setOnClickListener{
            val i = Intent(this, SearchActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}