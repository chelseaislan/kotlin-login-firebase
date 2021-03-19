package id.kotlinapp.kotlin04

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlin.math.log

class Signup : AppCompatActivity() {
    // Initialize firebase
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Add Firebase
        auth = FirebaseAuth.getInstance()

        // Create account to firebase
        buttonSignup.setOnClickListener {
            val email = editEmailSignup.text.toString().trim()
            val password = editPasswordSignup.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Authenticating user
            registerUser(email, password)
        }

        // To Login Activity
        txtToLogin.setOnClickListener {
            val i = Intent(this, Login::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
    }

    // If successful, go to main activity, data saved to firebase
    // Else, toast error
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Log.d("Signup", "Success registerUser: $email:$password uid: ${it.result?.user?.uid}")
                    Toast.makeText(this, "Sign up successful.", Toast.LENGTH_SHORT).show()
                    val i = Intent(this, MainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(i)
                } else {
                    Log.d("Signup", "Fail registerUser: ${it.exception?.message}")
                    Toast.makeText(this, "Failed to register: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}