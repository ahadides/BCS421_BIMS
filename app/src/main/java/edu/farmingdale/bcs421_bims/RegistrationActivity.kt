package edu.farmingdale.bcs421_bims


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegistrationActivity : AppCompatActivity() {
    private lateinit var firstN: EditText
    private lateinit var lastN: EditText
    private lateinit var DOB: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passW: EditText
    private lateinit var registerButton : Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        firstN = findViewById(R.id.firstNameEditText)
        lastN  = findViewById(R.id.familyNameEditText)
        DOB = findViewById(R.id.dateOfBirthEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passW = findViewById(R.id.editTextTextPassword2)
        registerButton = findViewById(R.id.registerButton)
        registerButton.setOnClickListener {
            val firstName = firstN.text.toString()
            val lastName = lastN.text.toString()
            val dateOfBirth = DOB.text.toString()
            val email = emailEditText.text.toString()
            val password = passW.text.toString()
            if(isInputEmpty(firstName, lastName, dateOfBirth, email, password)) {
                if (isValidInput(firstName, lastName, dateOfBirth, email, password)) {
                    // Data is valid, you can proceed with registration
                    val newUser = Users(firstName, lastName, dateOfBirth, email, password)
                    newUser.firstName = firstName
                    newUser.lastName = lastName
                    newUser.dateOfBirth = dateOfBirth
                    newUser.email = email
                    newUser.password = password
                    val intent = Intent(this, Login_Activity::class.java)
                    // Add your registration logic here
                    Log.d("NewUser", newUser.email)
                    intent.putExtra("newUser", newUser)
                    setResult(Activity.RESULT_OK, intent)
                    Toast.makeText(this, "User Registered", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error: Check Information Format ", Toast.LENGTH_SHORT).show()

                    // Data is not valid, display an error message
                }
            }else{
                Toast.makeText(this, "Error: Empty Fields", Toast.LENGTH_SHORT).show()
            }

        }

    }



    private fun isValidInput(firstName: String, familyName: String, dateOfBirth: String, email: String, password: String): Boolean {
        // Perform validation here
        return (isValidFirstName(firstName) && isValidFamilyName(familyName) && isValidDateOfBirth(dateOfBirth) && isValidEmail(email) && isValidPassword(password))
    }

    private fun isValidFirstName(firstName: String): Boolean {
        return firstName.length in 3..30
    }

    private fun isValidFamilyName(familyName: String): Boolean {
        return familyName.length in 3..30
    }

    private fun isValidDateOfBirth(dateOfBirth: String): Boolean {
        //checking the format or the date's validity
        return dateOfBirth.matches("\\d{4}\\d{2}\\d{2}".toRegex())
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        // password validation rules
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$".toRegex()
        return passwordPattern.matches(password)
    }
    private fun isInputEmpty(firstName: String, lastName: String, dateOfBirth: String, email: String, password: String): Boolean {
        // Check if any of the fields is empty
        if (firstName.isEmpty() || lastName.isEmpty() || dateOfBirth.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return false
        }

        return true
    }
}


