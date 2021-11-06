package ru.dvfu.appliances.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsMenuLink
import ru.dvfu.appliances.R

@Composable
fun Settings(navController: NavController, upPress: () -> Unit) {
    Scaffold(topBar = { ScheduleAppBar(stringResource(R.string.settings), backClick = upPress) }) {
        Column {
            SettingsCheckbox(
                icon = { Icon(imageVector = Icons.Default.Wifi, contentDescription = "Wifi") },
                title = { Text(text = "Dark theme") },
                subtitle = { Text(text = "") },
                onCheckedChange = { newValue ->  },
            )
        }
    }
}