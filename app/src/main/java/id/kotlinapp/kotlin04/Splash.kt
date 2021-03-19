package id.kotlinapp.kotlin04

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth

class Splash : AppCompatActivity() {
    // Time out before switching activity
    private val timeOut = 2000

    // Initialize firebase
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Add firebase
        auth = FirebaseAuth.getInstance()
    }

    // Check user status
    // If logged in, proceed to Main Activity
    // Else, proceed to Login Activity
    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            Handler().postDelayed({
                Log.d("Splash", "onStart: Login current user successful.")
                val i = Intent(this@Splash, MainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }, timeOut.toLong())
        } else {
            Handler().postDelayed({
                Log.d("Splash", "onStart: No logged in account. Head to Login Activity.")
                val i = Intent(this@Splash, Login::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }, timeOut.toLong())
        }
    }
}