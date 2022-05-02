package ru.dvfu.appliances.compose

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import ru.dvfu.appliances.compose.ui.theme.ScheduleTheme
import kotlinx.coroutines.InternalCoroutinesApi
import ru.dvfu.appliances.R

/**
 * Main activity for the app.
 */


class MainActivity : ComponentActivity() {

    @OptIn(
        ExperimentalMaterialApi::class,
        InternalCoroutinesApi::class,
        ExperimentalAnimationApi::class,
        ExperimentalFoundationApi::class,
        ExperimentalComposeUiApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getFirebaseMessagingToken()

        setContent {
            ScheduleTheme {
                ScheduleApp()
            }
        }
    }

    private fun getFirebaseMessagingToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(this.localClassName, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(this.localClassName, msg)
            //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }
}

@Preview(showBackground = false)
@Composable
fun TopBarPreview() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val navController = rememberNavController()
    ScheduleAppBar(title = "ScheduleAppBar")
}

/**
 * A composable function that returns the [Resources]. It will be recomposed when `Configuration`
 * gets updated.
 */
@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}
