package id.kotlinapp.kotlin04

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // Initialize firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add firebase
        auth = FirebaseAuth.getInstance()

        // Step no. 1 to add toolbar
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.nav_host_fragment_container)
        val appBarConfig = AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_profile).build()
        setupActionBarWithNavController(navController, appBarConfig)
        nav_bottom.setupWithNavController(navController)
    }
}