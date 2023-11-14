package edu.farmingdale.bcs421_bims

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Login_Activity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passWord: EditText
    private lateinit var bntLogin: Button
    private lateinit var bntSignup: Button
    val registrationRequestCode = 1
    private val usersStore = ArrayList<Users>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UI elements
        emailEditText = findViewById(R.id.editTextTextEmailAddress)
        passWord = findViewById(R.id.editTextTextPassword)
        bntLogin = findViewById(R.id.button)
        bntSignup = findViewById(R.id.button2)

        bntSignup.setOnClickListener{
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivityForResult(intent, registrationRequestCode)
        }

        bntLogin.setOnClickListener {
            val email = emailEditText.text.toString()
            val pass = passWord.text.toString()

            if (isValidEmail(email) && isValidPassword(pass)) {
                // Check if the email and password match a user in the list
                val user = usersStore.find { it.email == email && it.password == pass }
                Log.d("loginUser",email)
                Log.d("regUser",usersStore[0].email)
                if (user != null) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == registrationRequestCode && resultCode == Activity.RESULT_OK) {
            // Data is returned from the RegistrationActivity
            val newUser = data?.getSerializableExtra("newUser") as? Users

            if (newUser != null) {
                // Add the new user to the list of all users
                usersStore.add(newUser)


            }
        }
    }


    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        // Define your password validation rules here
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$".toRegex()
        return passwordPattern.matches(password)
    }
}