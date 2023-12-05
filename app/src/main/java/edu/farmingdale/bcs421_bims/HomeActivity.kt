package edu.farmingdale.bcs421_bims

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        //Set up the logout button
        val googleLogoutButton = findViewById<Button>(R.id.bnGoogleLogout)
        googleLogoutButton.setOnClickListener {
            signOut()
        }

        //Set up Search Activity button
        val searchActivityButton = findViewById<Button>(R.id.bnSearchActivity)
        searchActivityButton.setOnClickListener {
            //Redirect to the registration activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        //Set up Camera Activity button
        val cameraActivityButton = findViewById<Button>(R.id.button)
        cameraActivityButton.setOnClickListener {
            //Redirect to the registration activity
            val intent = Intent(this, Inventory::class.java)
            startActivity(intent)
        }

    }

    private fun signOut() {
        //Firebase sign out
        auth.signOut()

        //Google sign out
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //Update UI after sign out
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            //Close HomeActivity
            finish()
        }
    }
}