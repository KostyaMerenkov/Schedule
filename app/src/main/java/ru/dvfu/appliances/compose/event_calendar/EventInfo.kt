package ru.dvfu.appliances.compose.event_calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.dvfu.appliances.model.repository.entity.Event

@Composable
fun EventInfo() {

    val event = Event()
    Scaffold(
        //topBar = {  }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            EventUser()
        }
    }


}

@Composable
fun EventUser() {
    TODO("Not yet implemented")
}

@Preview
@Composable
fun EventInfoPreview() {
    EventInfo()
}