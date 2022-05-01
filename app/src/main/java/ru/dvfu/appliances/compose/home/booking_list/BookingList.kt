package ru.dvfu.appliances.compose.home.booking_list

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.pager.rememberPagerState
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.*
import ru.dvfu.appliances.compose.appliance.LoadingItem
import ru.dvfu.appliances.compose.appliance.UserImage
import ru.dvfu.appliances.compose.home.DateAndTime
import ru.dvfu.appliances.compose.viewmodels.BookingListViewModel
import ru.dvfu.appliances.compose.viewmodels.CalendarEventDateAndTime
import ru.dvfu.appliances.compose.views.*
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.utils.*
import ru.dvfu.appliances.ui.ViewState
import java.time.Duration
import java.time.LocalDateTime
import java.util.*


@OptIn(
    ExperimentalCoilApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.animation.ExperimentalAnimationApi::class,
    androidx.compose.material.ExperimentalMaterialApi::class,
    com.google.accompanist.pager.ExperimentalPagerApi::class
)
@Composable
fun BookingList(navController: NavController) {
    val viewModel: BookingListViewModel = getViewModel()
    val currentUser = viewModel.currentUser.collectAsState()
    val uiState by viewModel.viewState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScheduleAppBar(
                title = stringResource(R.string.bookings),
                backClick = { navController.popBackStack() })
        }) {
        Crossfade(targetState = uiState) { state ->
            when (state) {
                is ViewState.Error -> {
                    Text(text = "Error")
                }
                is ViewState.Loading -> {
                    LoadingItem(Modifier.fillMaxSize())
                }
                is ViewState.Success -> {

                    if (state.data.isEmpty()) {
                        NoBookingsView(Modifier.fillMaxSize())
                    }

                    val bookingTabs = remember { mutableStateListOf<BookingTabItem>() }

                    LaunchedEffect(key1 = state.data) {
                        bookingTabs.clear()
                        bookingTabs.addAll(
                            initTabs(
                                bookings = state.data,
                                currentUser = currentUser.value,
                                viewModel = viewModel,
                                navController = navController
                            )
                        )
                    }

                    val pagerState = rememberPagerState()

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        BookingListTabsView(
                            tabsList = bookingTabs,
                            pagerState = pagerState
                        )

                        BookingTabsContent(
                            tabsList = bookingTabs,
                            pagerState = pagerState
                        )

                    }
                }
            }
        }
    }
}

private fun initTabs(
    bookings: List<CalendarEvent>,
    currentUser: User,
    viewModel: BookingListViewModel,
    navController: NavController,
): List<BookingTabItem> {

    val result = mutableListOf<BookingTabItem>()

    if (currentUser.isAdmin() || bookings.find {
            it.appliance?.isUserSuperuserOrAdmin(currentUser) == true
        } != null) {
        result.add(
            BookingTabItem.PendingBookingsTabItem(
                bookings = if (currentUser.isAdmin()) {
                    bookings
                } else {
                    bookings.filter {
                        it.appliance?.isUserSuperuserOrAdmin(currentUser) == true
                    }
                },
                viewModel = viewModel,
                navController = navController
            )
        )
    }

    val myBookings = bookings.filter { it.user.userId == currentUser.userId }

    result.add(
        BookingTabItem.BookingRequestsTabItem(
            bookings = myBookings.filter { it.status == BookingStatus.NONE },
            viewModel = viewModel
        )
    )

    result.add(
        BookingTabItem.PastBookingsTabItem(
            bookings = myBookings
                .filter {
                    (it.status == BookingStatus.APPROVED || it.status == BookingStatus.DECLINED)
                            && it.timeEnd.toMillis < LocalDateTime.now().toMillis
                },
            viewModel = viewModel
        )
    )
    return result
}

@Composable
fun BookingListHeader(stringResource: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

        Card(border = BorderStroke(2.dp, Color.Black), shape = RoundedCornerShape(10.dp)) {
            Text(
                stringResource, modifier = Modifier
                    .padding(4.dp)
                    .padding(horizontal = 10.dp), style = MaterialTheme.typography.h6
            )
        }
    }
}

@Composable
fun NoBookingsView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 64.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryText(text = stringResource(id = R.string.no_books))
    }
}

@Composable
fun BookingStatus(
    book: CalendarEvent,
    currentUser: User,
    onUserClick: (User) -> Unit,
    onDecline: ((CalendarEvent, String) -> Unit),
    onApprove: ((CalendarEvent, String) -> Unit),
) {
    Divider()
    Spacer(modifier = Modifier.size(12.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when (book.status) {
            BookingStatus.APPROVED -> {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {}, enabled = false
                    ) {
                        Text(text = stringResource(id = R.string.approved), color = Color.Green)
                    }
                }
                Text(text = book.managedTime?.toDateAndTime ?: "")
                book.managedUser?.let {
                    BookingUser(
                        user = book.managedUser,
                        shouldShowHeader = false
                    ) { onUserClick(it) }
                }

            }
            BookingStatus.DECLINED -> {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {}, enabled = false
                    ) {
                        Text(text = stringResource(id = R.string.declined), color = Color.Red)
                    }
                }
                Text(text = book.managedTime?.toDateAndTime ?: "")
                book.managedUser?.let {
                    BookingUser(
                        user = book.managedUser,
                        shouldShowHeader = false
                    ) { onUserClick(it) }
                }
            }

            BookingStatus.NONE -> {
                if (currentUser.canManageEvent(book)) {
                    BookingButtons(
                        onDeclineClick = { onDecline(book, it) },
                        onApproveClick = { onApprove(book, it) })
                } else {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = {}, enabled = false
                        ) {
                            Text(text = "В рассмотрении", color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCommentary(
    modifier: Modifier = Modifier,
    header: String = stringResource(id = R.string.commentary),
    commentary: String,
    onCommentarySave: ((String) -> Unit)? = null,
) {

    var commentaryDialog by remember {
        mutableStateOf(false)
    }

    if (commentaryDialog) {
        BookingCommentaryDialog(
            commentArg = commentary,
            onApplyCommentary = onCommentarySave ?: {},
            onCancel = { commentaryDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TextDivider(text = header)
        Row {
            if (commentary.isBlank()) {
                SecondaryText(text = stringResource(R.string.no_commentary))
            } else {
                PrimaryText(
                    text = commentary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }
            if (onCommentarySave != null) {
                IconButton(onClick = { commentaryDialog = true }) {
                    Icon(Icons.Default.Edit, "")
                }
            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BookingTime(
    modifier: Modifier = Modifier,
    editable: Boolean = false,
    timeStart: LocalDateTime,
    timeEnd: LocalDateTime,
    onSetNewDateAndTime: ((CalendarEventDateAndTime) -> Unit)? = null
) {

    var dialogState by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(
            modifier = Modifier
                .padding(8.dp)
                .size(24.dp)
        )

        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PrimaryText(text = timeStart.toLocalDate().format(TimeConstants.FULL_DATE_FORMAT))
            PrimaryText(
                text = formattedTime(timeStart, timeEnd)
            )
        }

        if (editable) {
            IconButton(onClick = { dialogState = !dialogState }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                )
            }
        } else {
            Spacer(
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
            )
        }

    }

    if (dialogState) {
        var dialogDate by remember { mutableStateOf(timeStart.toLocalDate()) }
        var dialogTimeStart by remember { mutableStateOf(timeStart.toLocalTime()) }
        var dialogTimeEnd by remember { mutableStateOf(timeEnd.toLocalTime()) }
        val isError by remember(dialogTimeStart, dialogTimeEnd) {
            mutableStateOf(
                dialogTimeEnd.isBefore(dialogTimeStart) || Duration.between(
                    dialogTimeStart,
                    dialogTimeEnd
                ) < Duration.ofMinutes(30)
            )
        }
        val duration by remember(dialogTimeStart, dialogTimeEnd) {
            val dur = Duration.between(dialogTimeStart, dialogTimeEnd)
            val period = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                dur.toHours(),
                dur.minusHours(dur.toHours()).toMinutes(),
            )
            mutableStateOf(period)
        }

        DefaultDialog(
            positiveButtonText = stringResource(id = R.string.apply),
            neutralButtonText = stringResource(id = R.string.cancel),
            onDismiss = { dialogState = false },
            onPositiveClick = {
                if (isError) {
                    SnackbarManager.showMessage(R.string.time_end_is_before_start)
                } else {
                    dialogState = false
                    if (onSetNewDateAndTime != null) {
                        onSetNewDateAndTime(
                            CalendarEventDateAndTime(
                                date = dialogDate,
                                timeStart = dialogTimeStart,
                                timeEnd = dialogTimeEnd
                            )
                        )
                    }
                }
            },
            onNeutralClick = { dialogState = false }

        ) {
            Column(
                modifier = Modifier.padding(vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                DateAndTime(
                    date = dialogDate,
                    timeStart = dialogTimeStart,
                    timeEnd = dialogTimeEnd,
                    duration = duration,
                    onDateSet = if (dialogTimeStart.atDate(dialogDate)
                            .isBefore(LocalDateTime.now())
                    ) {
                        null
                    } else {
                        { dialogDate = it }
                    },
                    onTimeStartSet = if (dialogTimeStart.atDate(dialogDate)
                            .isBefore(LocalDateTime.now())
                    ) {
                        null
                    } else {
                        { dialogTimeStart = it }
                    },
                    onTimeEndSet = { dialogTimeEnd = it },
                    isDurationError = isError,
                )
            }
        }
    }
}

@Composable
fun BookingAppliance(
    appliance: Appliance,
    shouldShowHeader: Boolean = true,
    onApplianceClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (shouldShowHeader) {
            TextDivider(text = stringResource(id = R.string.appliance))
        }

        InvisibleCardClickable(onClick = onApplianceClick) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(10.dp)
                    .fillMaxWidth()
                //.padding(horizontal = 10.dp)
            ) {
                ApplianceImage(
                    appliance,
                    modifier = Modifier
                        .height(48.dp)
                        .aspectRatio(1f)
                        .fillMaxWidth(0.20f),
                )
                ApplianceName(
                    appliance = appliance, modifier = Modifier.fillMaxWidth(0.80f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InvisibleCardClickable(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    function: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        elevation = 0.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        function()
    }
}

@Composable
fun BookingUser(
    user: User,
    shouldShowHeader: Boolean = true,
    header: String = stringResource(id = R.string.user),
    onUserClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (shouldShowHeader) {
            TextDivider(text = header)
        }
        InvisibleCardClickable(onClick = onUserClick) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                UserImage(
                    modifier = Modifier
                        .height(48.dp)
                        .aspectRatio(1f)
                        .fillMaxWidth(0.20f),
                    user = user
                )
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth(0.80f)
                ) {
                    Text(user.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(user.email)
                }
            }
        }
    }
}
