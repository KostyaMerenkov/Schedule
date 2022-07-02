package ru.dvfu.appliances.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.MainActivity

class SplashScreen : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    /** Duration of wait  */
    private val SPLASH_DISPLAY_LENGTH: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        /**If user is not authenticated, send him to SignInActivity to authenticate first.
         * Else send him to DashboardActivity*/
        Handler().postDelayed({
            if (user != null) {
                val dashboardIntent = Intent(this, MainActivity::class.java)
                startActivity(dashboardIntent)
                // remove this activity from the stack
                finish()
            } else {
                val signInIntent = Intent(this, LoginActivity::class.java)
                startActivity(signInIntent)
                // remove this activity from the stack
                finish()
            }
        }, SPLASH_DISPLAY_LENGTH)
    }
}