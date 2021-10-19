package ru.dvfu.appliances.compose

import UsersScreen
import Drawer
import HomeScreen
import AppliancesScreen
import MusicScreen
import ProfileScreen
import SettingsScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.ui.components.NavDrawerItem

/**
 * Main activity for the app.
 */

class MainActivity : ComponentActivity() {
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    @InternalCoroutinesApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}


@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
@Composable
fun MainScreen() {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    // If you want the drawer from the right side, uncomment the following
    // CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(scope, scaffoldState, navController) },
        drawerBackgroundColor = colorResource(id = R.color.design_default_color_primary),
        // scrimColor = Color.Red,  // Color for the fade background when you open/close the drawer
        drawerContent = {
            Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
        },
    ) {
        Navigation(navController = navController, scaffoldState = scaffoldState)
    }
    // }
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@InternalCoroutinesApi
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@InternalCoroutinesApi
@ExperimentalMaterialApi
@Composable
fun Navigation(navController: NavHostController, scaffoldState: ScaffoldState) {
    NavHost(navController, startDestination = NavDrawerItem.Home.route) {
        composable(NavDrawerItem.Home.route) {
            HomeScreen()
        }
        composable(NavDrawerItem.Music.route) {
            MusicScreen()
        }
        composable(NavDrawerItem.Movies.route) {
            AppliancesScreen(navController, Modifier)
        }
        composable(NavDrawerItem.Users.route) {
            UsersScreen(navController, Modifier)
        }
        composable(NavDrawerItem.Profile.route) {
            ProfileScreen(navController, Modifier)
        }
        composable(NavDrawerItem.Settings.route) {
            SettingsScreen()
        }
    }
}

@Composable
fun TopBar(scope: CoroutineScope, scaffoldState: ScaffoldState, navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    /*when(navBackStackEntry?.destination?.route) {
        NavDrawerItem.Home.title -> "",
        NavDrawerItem.Music.title,
        NavDrawerItem.Movies.title,
        NavDrawerItem.Books.title,
        NavDrawerItem.Profile.title,
        NavDrawerItem.Settings.title
    }*/
    TopAppBar(
        title = { navBackStackEntry?.destination?.route?.let { Text(text = it, fontSize = 18.sp) } },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(Icons.Filled.Menu, "")
            }
        },
        backgroundColor = colorResource(id = R.color.design_default_color_primary),
        contentColor = Color.White
    )
}

@Preview(showBackground = false)
@Composable
fun TopBarPreview() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val navController = rememberNavController()
    TopBar(scope = scope, scaffoldState = scaffoldState, navController = navController, )
}


/*
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

}*/
