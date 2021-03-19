package id.kotlinapp.kotlin04

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_reset_password.*

class Login : AppCompatActivity() {
    // Initialize firebase
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Add firebase
        auth = FirebaseAuth.getInstance()

        // Login with email password saved in firebase
        buttonLogin.setOnClickListener {
            val email = editEmailLogin.text.toString().trim()
            val password = editPasswordLogin.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Authenticating user
            loginUser(email, password)
        }

        // To signup activity
        txtToSignup.setOnClickListener {
            val i = Intent(this, Signup::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }

        // To reset password activity
        txtResetPass.setOnClickListener {
            val i = Intent(this, ResetPassword::class.java)
            startActivity(i)
        }
    }

    // If successful, go to main activity
    // Else, toast error
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Log.d("Login", "Success loginUser: $email:$password, uid: ${it.result?.user?.uid}")
                    Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show()
                    val i = Intent(this, MainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(i)
                } else {
                    Log.d("Login", "Fail loginUser: ${it.exception?.message}")
                    Toast.makeText(this, "Failed to log in: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}