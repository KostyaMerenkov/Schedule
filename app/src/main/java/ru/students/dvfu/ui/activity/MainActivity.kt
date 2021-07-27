package ru.students.dvfu.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ru.students.dvfu.R
import ru.students.dvfu.databinding.ActivityMainBinding
import ru.students.dvfu.databinding.NavHeaderMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var vb: ActivityMainBinding
    private lateinit var hvb: NavHeaderMainBinding

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)
        val hv: View = vb.navView.getHeaderView(0)
        hvb = NavHeaderMainBinding.bind(hv)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()
        if (!mAuth.currentUser!!.isAnonymous) putUserInfoToNavBar(mAuth.currentUser!!)
        else hvb.userEmail.setOnClickListener {
            signOut()
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        vb.navView.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            signOut()
            return@setOnMenuItemClickListener true
        }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_week, R.id.nav_month, R.id.rv_users, R.id.rv_appliances
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun putUserInfoToNavBar(currentUser: FirebaseUser) {
        currentUser.photoUrl?.let {
            Glide.with(this).load(it).circleCrop().into(hvb.userAvatar)
        }
        currentUser.displayName?.let {
            hvb.userName.text = it
        }
        currentUser.email?.let {
            hvb.userEmail.text = currentUser.email
        }
    }

    private fun signOut() {
        if (mAuth.currentUser?.isAnonymous == true) {
            mAuth.currentUser?.delete()
        }
        mAuth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}