package ru.dvfu.appliances.compose.home.booking_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.Arguments
import ru.dvfu.appliances.compose.MainDestinations
import ru.dvfu.appliances.compose.navigate
import ru.dvfu.appliances.compose.ui.theme.customColors
import ru.dvfu.appliances.compose.viewmodels.BookingListViewModel
import ru.dvfu.appliances.compose.viewmodels.CalendarEventDateAndTime
import ru.dvfu.appliances.compose.views.*
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.utils.toMillis
import java.time.LocalDate
import java.time.LocalDateTime


sealed class BookingTabItem(var titleRes: Int, var screen: @Composable () -> Unit) {

    class PendingBookingsTabItem(
        bookings: List<CalendarEvent>,
        viewModel: BookingListViewModel,
        navController: NavController
    ) :
        BookingTabItem(
            titleRes = R.string.pending,
            screen = { PendingBookingsList(bookings, viewModel, navController) }
        )

    class BookingRequestsTabItem(bookings: List<CalendarEvent>, viewModel: BookingListViewModel) :
        BookingTabItem(
            titleRes = R.string.my_requests,
            screen = { MyBookingRequestsList(bookings, viewModel) }
        )

    class ApprovedBookingsTabItem(bookings: List<CalendarEvent>, viewModel: BookingListViewModel) :
        BookingTabItem(
            titleRes = R.string.approved,
            screen = { ApprovedBookingsList(bookings, viewModel) }
        )

    class DeclinedBookingsTabItem(bookings: List<CalendarEvent>, viewModel: BookingListViewModel) :
        BookingTabItem(
            titleRes = R.string.declined,
            screen = { DeclinedBookingsList(bookings, viewModel) }
        )

    class PastBookingsTabItem(bookings: List<CalendarEvent>, viewModel: BookingListViewModel) :
        BookingTabItem(
            titleRes = R.string.past_bookings,
            screen = { PastBookingsList(bookings, viewModel) }
        )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BookingListTabsView(
    modifier: Modifier = Modifier,
    tabsList: List<BookingTabItem>,
    pagerState: PagerState
) {
    val scope = rememberCoroutineScope()

    ScrollableTabRow(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.background,
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }
    ) {
        tabsList.forEachIndexed { index, tab ->
            Tab(selected = pagerState.currentPage == index,
                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                text = { PrimaryText(text = stringResource(id = tab.titleRes)) })
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BookingTabsContent(
    modifier: Modifier = Modifier,
    tabsList: List<BookingTabItem>,
    pagerState: PagerState
) {
    HorizontalPager(
        modifier = modifier.fillMaxSize(),
        state = pagerState,
        verticalAlignment = Alignment.Top,
        count = tabsList.size
    ) { page ->
        tabsList[page].screen()
    }
}

@Composable
fun PendingBookingItemView(
    modifier: Modifier = Modifier,
    booking: CalendarEvent,
    navController: NavController,
    onApproveClick: (String) -> Unit,
    onDeclineClick: () -> Unit,
    onSetDateAndTime: (CalendarEventDateAndTime) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        val dialogState = remember{ mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderText(
                modifier = Modifier.padding(8.dp),
                text = stringResource(R.string.booking_request)
            )
            BookingTime(
                editable = true,
                timeStart = booking.timeStart,
                timeEnd = booking.timeEnd,
                onSetNewDateAndTime = onSetDateAndTime
            )
            Spacer(modifier = Modifier.size(8.dp))
            booking.appliance?.let {
                BookingAppliance(booking.appliance!!, onApplianceClick = {
                    navController.navigate(
                        MainDestinations.APPLIANCE_ROUTE,
                        Arguments.APPLIANCE to it
                    )
                })
            }
            booking.user?.let {
                BookingUser(booking.user, onUserClick = {
                    navController.navigate(
                        MainDestinations.USER_DETAILS_ROUTE,
                        Arguments.USER to it
                    )
                })
            }

            BookingCommentary(commentary = booking.commentary)

            BookingButtons(
                onApproveClick = { dialogState.value = true },
                onDeclineClick = onDeclineClick
            )

            Spacer(modifier = Modifier.size(16.dp))

            BookingCommentaryDialog(
                dialogState = dialogState.value,
                onApplyCommentary = onApproveClick,
                onCancel = { dialogState.value = false }
            )
        }

    }
}

@Composable
fun MyBookingRequestItemView(
    modifier: Modifier = Modifier,
    booking: CalendarEvent,
    onDeclineClick: () -> Unit,
    onSetDateAndTime: (CalendarEventDateAndTime) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderText(
                modifier = Modifier.padding(8.dp),
                text = stringResource(R.string.booking_request)
            )
            BookingTime(
                editable = true,
                timeStart = booking.timeStart,
                timeEnd = booking.timeEnd,
                onSetNewDateAndTime = onSetDateAndTime
            )
            Spacer(modifier = Modifier.size(8.dp))
            booking.appliance?.let {
                BookingAppliance(booking.appliance!!, onApplianceClick = {
//                    navController.navigate(
//                        MainDestinations.APPLIANCE_ROUTE,
//                        Arguments.APPLIANCE to book.appliance!!
//                    )
                })
            }

            BookingCommentary(commentary = booking.commentary)

            DeclineBookingButton(onDeclineClick = onDeclineClick)

            Spacer(modifier = Modifier.size(16.dp))
        }

    }
}

@Composable
fun BookingApprovedItemView(
    modifier: Modifier = Modifier,
    booking: CalendarEvent,
    onDeclineClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderText(
                modifier = Modifier.padding(8.dp),
                text = stringResource(R.string.booking_approved),
                textColor = MaterialTheme.colors.primaryVariant
            )
            BookingTime(
                editable = true,
                timeStart = booking.timeStart,
                timeEnd = booking.timeEnd,
                onSetNewDateAndTime = {

                }
            )
            Spacer(modifier = Modifier.size(8.dp))
            booking.appliance?.let {
                BookingAppliance(booking.appliance!!, onApplianceClick = {
//                    navController.navigate(
//                        MainDestinations.APPLIANCE_ROUTE,
//                        Arguments.APPLIANCE to book.appliance!!
//                    )
                })
            }
            booking.managedUser?.let {
                BookingUser(
                    user = booking.managedUser,
                    header = stringResource(id = R.string.managed_by),
                    onUserClick = {
//                    navController.navigate(
//                        MainDestinations.USER_DETAILS_ROUTE,
//                        Arguments.USER to book.user
//                    )
                    })
            }

            BookingCommentary(
                header = stringResource(R.string.manager_commentary),
                commentary = booking.managerCommentary
            )

            DeclineBookingButton(onDeclineClick = onDeclineClick)
        }

    }
}

@Composable
fun BookingDeclinedOrPastItemView(
    modifier: Modifier = Modifier,
    booking: CalendarEvent,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderText(
                modifier = Modifier.padding(8.dp),
                text = if (booking.status == BookingStatus.DECLINED) {
                    stringResource(R.string.declined_booking)
                } else {
                    stringResource(id = R.string.book_approved)
                },
                textColor = if (booking.timeEnd.toMillis > LocalDateTime.now().toMillis) {
                    Color.Red
                } else {
                    MaterialTheme.customColors.secondaryTextColor
                }
            )
            BookingTime(
                timeStart = booking.timeStart,
                timeEnd = booking.timeEnd
            )
            Spacer(modifier = Modifier.size(8.dp))
            booking.appliance?.let {
                BookingAppliance(booking.appliance!!, onApplianceClick = {
//                    navController.navigate(
//                        MainDestinations.APPLIANCE_ROUTE,
//                        Arguments.APPLIANCE to book.appliance!!
//                    )
                })
            }
            booking.managedUser?.let {
                BookingUser(
                    user = booking.managedUser,
                    header = stringResource(R.string.managed_by),
                    onUserClick = {
//                    navController.navigate(
//                        MainDestinations.USER_DETAILS_ROUTE,
//                        Arguments.USER to book.user
//                    )
                    })
            }

            BookingCommentary(
                header = stringResource(id = R.string.manager_commentary),
                commentary = booking.managerCommentary
            )

            Spacer(modifier = Modifier.size(32.dp))
        }

    }
}


@Composable
fun DeclineBookingButton(
    modifier: Modifier = Modifier,
    onDeclineClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        DefaultButton(
            text = stringResource(R.string.refuse),
            tint = Color.Red,
            onClick = onDeclineClick
        )
    }
}

@Composable
fun BookingButtons(
    modifier: Modifier = Modifier,
    onApproveClick: () -> Unit,
    onDeclineClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DefaultButton(
            text = stringResource(id = R.string.decline),
            tint = Color.Red,
            onClick = onDeclineClick
        )
        DefaultButton(
            text = stringResource(id = R.string.approve),
            onClick = onApproveClick
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BookingCommentaryDialog(
    dialogState: Boolean,
    onApplyCommentary: (String) -> Unit,
    onCancel: () -> Unit
) {
    val maxSymbols = remember { 256 }
    val commentary = remember { mutableStateOf("") }
    val symbolsCount = remember(commentary.value) { mutableStateOf(commentary.value.length) }
    val isError = remember(symbolsCount.value) { mutableStateOf(symbolsCount.value >= maxSymbols) }

    if (dialogState) {
        DefaultDialog(
            primaryText = stringResource(R.string.leave_a_commentary),
            secondaryText = stringResource(R.string.not_necessary),
            positiveButtonText = stringResource(id = R.string.apply),
            onPositiveClick = {
                if (!isError.value) {
                    onApplyCommentary(commentary.value)
                } else {
                    SnackbarManager.showMessage(R.string.too_many_symbols)
                }
            },
            neutralButtonText = stringResource(id = R.string.cancel),
            onNeutralClick = onCancel,
            onDismiss = onCancel
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp),
                    value = commentary.value,
                    onValueChange = {
                        commentary.value = it
                    },
                    label = {
                        Text(text = stringResource(id = R.string.commentary))
                    },
                    isError = isError.value,
                    singleLine = false,
                    maxLines = 5
                )
                SecondaryText(
                    modifier = Modifier,
                    text = "${symbolsCount.value}/$maxSymbols",
                    textColor = if (isError.value) {
                        Color.Red
                    } else {
                        MaterialTheme.customColors.secondaryTextColor
                    }
                )
            }
        }
    }

}

@Composable
fun EventInfoScreen(
    currentUser: User,
    modifier: Modifier = Modifier,
    event: CalendarEvent,
    navController: NavController,
    onApproveClick: () -> Unit,
    onDeclineClick: () -> Unit,
    onSetDateAndTime: (CalendarEventDateAndTime) -> Unit
) {

    HeaderText(
        modifier = Modifier.padding(8.dp),
        text = when (event.status) {
            BookingStatus.NONE -> {
                if (event.timeEnd.isAfter(LocalDateTime.now())) stringResource(R.string.event_finished) else stringResource(R.string.booking_request)
            }
            BookingStatus.APPROVED -> stringResource(id = R.string.booking_approved)
            BookingStatus.DECLINED -> stringResource(id = R.string.book_declined)
        }
    )
    BookingTime(
        editable = true,
        timeStart = event.timeStart,
        timeEnd = event.timeEnd,
        onSetNewDateAndTime = onSetDateAndTime
    )
    Spacer(modifier = Modifier.size(8.dp))
    event.appliance.let {
        BookingAppliance(it, onApplianceClick = {
            navController.navigate(
                MainDestinations.APPLIANCE_ROUTE,
                Arguments.APPLIANCE to it
            )
        })
    }
    BookingUser(event.user, onUserClick = {
        navController.navigate(
            MainDestinations.USER_DETAILS_ROUTE,
            Arguments.USER to event.user
        )
    })

    BookingCommentary(commentary = event.commentary)

    Spacer(modifier = Modifier.size(16.dp))

    BookingStatus(
        book = event,
        currentUser = currentUser,
        onUserClick = {
            navController.navigate(
                MainDestinations.USER_DETAILS_ROUTE,
                Arguments.USER to it
            )
        },
        onApprove = { onApproveClick() },
        onDecline = { onDeclineClick() },
    )


}

