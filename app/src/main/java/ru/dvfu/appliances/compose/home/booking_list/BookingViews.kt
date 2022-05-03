package ru.dvfu.appliances.compose.home.booking_list

import androidx.compose.foundation.layout.*
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
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.Arguments
import ru.dvfu.appliances.compose.MainDestinations
import ru.dvfu.appliances.compose.navigate
import ru.dvfu.appliances.compose.ui.theme.customColors
import ru.dvfu.appliances.compose.viewmodels.BookingListViewModel
import ru.dvfu.appliances.compose.viewmodels.EventDateAndTime
import ru.dvfu.appliances.compose.views.*
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.User


sealed class BookingTabItem(var titleRes: Int, var screen: @Composable () -> Unit) {

    class PendingBookingsTabItem(
        bookings: List<CalendarEvent>,
        viewModel: BookingListViewModel,
        navController: NavController
    ) :
        BookingTabItem(
            titleRes = R.string.booking_requests,
            screen = { PendingBookingsList(bookings, viewModel, navController) }
        )

    class MyBookingsTabItem(
        bookings: List<CalendarEvent>,
        viewModel: BookingListViewModel,
        navController: NavController
    ) :
        BookingTabItem(
            titleRes = R.string.my_bookings,
            screen = { MyBookingsList(bookings, viewModel, navController) }
        )

    class PastBookingsTabItem(
        bookings: List<CalendarEvent>,
        viewModel: BookingListViewModel,
        navController: NavController
    ) :
        BookingTabItem(
            titleRes = R.string.past_bookings,
            screen = { PastBookingsList(bookings, viewModel, navController) }
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
    currentUser: User,
    booking: CalendarEvent,
    navController: NavController,
    onApproveClick: (CalendarEvent, String) -> Unit,
    onDeclineClick: (CalendarEvent, String) -> Unit,
    onSetDateAndTime: (EventDateAndTime) -> Unit,
    onApplyCommentary: (CalendarEvent, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BookingItem {
        EventInfo(
            currentUser = currentUser,
            event = booking,
            navController = navController,
            onApproveClick = onApproveClick,
            onDeclineClick = onDeclineClick,
            onSetDateAndTime = {_,_ ->
                //onSetDateAndTime(booking, it)
                // TODO:
            },
            onCommentarySave = onApplyCommentary
        )
    }
}

@Composable
fun MyBookingRequestItemView(
    modifier: Modifier = Modifier,
    booking: CalendarEvent,
    onDeclineClick: () -> Unit,
    onSetDateAndTime: (EventDateAndTime) -> Unit,
    onApplyCommentary: (CalendarEvent, String) -> Unit,
) {
    BookingItem {
        EventInfo(
            // TODO: currentUser handle
            currentUser = User(),
            event = booking,
            navController = rememberNavController(),
            // TODO: give navController for real
            onApproveClick = { _, _ ->

            },
            onDeclineClick = { _, _ ->

            },
            onSetDateAndTime = {_,_ ->
                //onSetDateAndTime(booking, it)
                // TODO:
            },
            onCommentarySave = onApplyCommentary
        )
    }
}

@Composable
fun BookingApprovedItemView(
    modifier: Modifier = Modifier,
    booking: CalendarEvent,
    onDeclineClick: () -> Unit
) {
    /*Card(
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

    }*/
}

@Composable
fun BookingDeclinedOrPastItemView(
    modifier: Modifier = Modifier,
    booking: CalendarEvent,
) {
    /*Card(
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

    }*/
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
    onApproveClick: (String) -> Unit,
    onDeclineClick: (String) -> Unit
) {
    var approveDialogState by remember { mutableStateOf(false) }
    var declineDialogState by remember { mutableStateOf(false) }

    if (approveDialogState) {
        BookingCommentaryDialog(
            commentArg = "",
            onCancel = { approveDialogState = false },
            onApplyCommentary = {
                approveDialogState = false
                onApproveClick(it)
            },
            newStatus = BookingStatus.APPROVED
        )
    }

    if (declineDialogState) {
        BookingCommentaryDialog(
            commentArg = "",
            onCancel = { declineDialogState = false },
            onApplyCommentary = {
                approveDialogState = false
                onDeclineClick(it)
            },
            newStatus = BookingStatus.DECLINED
        )
    }

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
            onClick = { declineDialogState = true }
        )
        DefaultButton(
            text = stringResource(id = R.string.approve),
            onClick = { approveDialogState = true }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BookingCommentaryDialog(
    commentArg: String,
    newStatus: BookingStatus? = null,
    onCancel: () -> Unit,
    onApplyCommentary: (String) -> Unit
) {
    val maxSymbols = remember { 256 }
    var commentary by remember { mutableStateOf(commentArg) }
    val symbolsCount = remember(commentary) { mutableStateOf(commentary.length) }
    val isError = remember(symbolsCount.value) { mutableStateOf(symbolsCount.value >= maxSymbols) }

    DefaultDialog(
        primaryText = stringResource(R.string.leave_a_commentary),
        secondaryText = stringResource(R.string.not_necessary),
        positiveButtonText = when (newStatus) {
            BookingStatus.DECLINED -> stringResource(id = R.string.decline)
            BookingStatus.APPROVED -> stringResource(id = R.string.approve)
            else -> stringResource(id = R.string.apply)
        },
        positiveButtonColor = when (newStatus) {
            BookingStatus.DECLINED -> ButtonDefaults.buttonColors(
                Color.Red, contentColorFor(
                    backgroundColor = Color.Red
                )
            )
            BookingStatus.APPROVED -> ButtonDefaults.buttonColors(
                Color.Green, contentColorFor(
                    backgroundColor = Color.Green
                )
            )
            else -> ButtonDefaults.buttonColors()
        },
        onPositiveClick = {
            if (!isError.value) {
                onApplyCommentary(commentary)
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
                value = commentary,
                onValueChange = {
                    commentary = it
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

@Composable
fun EventInfo(
    currentUser: User,
    modifier: Modifier = Modifier,
    event: CalendarEvent,
    navController: NavController,
    onApproveClick: (CalendarEvent, String) -> Unit,
    onDeclineClick: (CalendarEvent, String) -> Unit,
    onSetDateAndTime: (CalendarEvent, EventDateAndTime) -> Unit,
    onCommentarySave: (CalendarEvent, String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextDivider(text = "Дата и время")
        /*HeaderText(
            modifier = Modifier.padding(8.dp),
            text = when (event.status) {
                BookingStatus.NONE -> {
                    if (event.timeEnd.isBefore(LocalDateTime.now())) stringResource(R.string.event_finished)
                    else stringResource(R.string.booking_request)
                }
                BookingStatus.APPROVED -> stringResource(id = R.string.booking_approved)
                BookingStatus.DECLINED -> stringResource(id = R.string.book_declined)
            }
        )*/
        BookingTime(
            editable = currentUser.canManageEvent(event),
            timeStart = event.timeStart,
            timeEnd = event.timeEnd,
            onSetNewDateAndTime = {
                onSetDateAndTime(event, it)
            }
        )
        Spacer(modifier = Modifier.size(8.dp))

        BookingAppliance(event.appliance, onApplianceClick = {
            navController.navigate(
                MainDestinations.APPLIANCE_ROUTE,
                Arguments.APPLIANCE to event.appliance
            )
        })

        BookingUser(event.user, onUserClick = {
            navController.navigate(
                MainDestinations.USER_DETAILS_ROUTE,
                Arguments.USER to event.user
            )
        })

        BookingCommentary(
            commentary = event.commentary,
            editable = event.user.userId == currentUser.userId && event.status == BookingStatus.NONE,
            onCommentarySave = { comment ->
                onCommentarySave(event, comment)
            }
        )

        Spacer(modifier = Modifier.size(8.dp))

        BookingStatus(
            book = event,
            currentUser = currentUser,
            onUserClick = {
                navController.navigate(
                    MainDestinations.USER_DETAILS_ROUTE,
                    Arguments.USER to it
                )
            },
            onApprove = onApproveClick,
            onDecline = onDeclineClick,
        )
        Spacer(modifier = Modifier.size(8.dp))
    }

}

@Composable
fun BookingItem(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        content()
    }
}
