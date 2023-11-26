package edu.farmingdale.bcs421_bims

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var dateOfBirthEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        //Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.familyNameEditText)
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.editTextTextPassword2)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val dateOfBirth = dateOfBirthEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && dateOfBirth.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                //Perform registration using Firebase Auth
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            //On successful authentication, save additional user info in the database
                            val user = auth.currentUser
                            val databaseRef = FirebaseDatabase.getInstance().getReference("users")
                            val newUser = Users(firstName, lastName, dateOfBirth, email) // Assuming a User class with necessary fields
                            user?.let {
                                databaseRef.child(it.uid).setValue(newUser).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                        // Redirect to login or main activity
                                        val homeIntent = Intent(this, LoginActivity::class.java)
                                        startActivity(homeIntent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Failed to save user info: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


