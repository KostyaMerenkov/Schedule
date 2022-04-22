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
import ru.dvfu.appliances.compose.*
import ru.dvfu.appliances.compose.appliance.LoadingItem
import ru.dvfu.appliances.compose.appliance.UserImage
import ru.dvfu.appliances.compose.home.DateAndTime
import ru.dvfu.appliances.compose.utils.TimeConstants
import ru.dvfu.appliances.compose.utils.toHoursAndMinutes
import ru.dvfu.appliances.compose.viewmodels.BookingListViewModel
import ru.dvfu.appliances.compose.views.*
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.ui.ViewState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofLocalizedDateTime
import java.time.format.FormatStyle

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

//    if (managingUiState is UiState.InProgress) ModalLoadingDialog()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScheduleAppBar(
                title = "Бронирования",
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

                    val list = state.data

                    if (list.isEmpty()) {
                        NoBookingsView(Modifier.fillMaxSize())
                    }

                    val bookingTabs = mutableListOf<BookingTabItem>()

                    if (currentUser.value.isAdmin()) {
                        bookingTabs.add(
                            BookingTabItem.PendingBookingsTabItem(
                                bookings = list,
                                viewModel = viewModel
                            )
                        )
                    } else {
                        val pendingBookings = list.filter {
                            it.appliance?.isUserSuperuserOrAdmin(currentUser.value) == true
                        }
                        bookingTabs.add(
                            BookingTabItem.PendingBookingsTabItem(
                                bookings = pendingBookings,
                                viewModel = viewModel
                            )
                        )
                    }

                    val bookings = list.filter { it.user?.userId == currentUser.value.userId }

                    bookingTabs.add(
                        BookingTabItem.ApprovedBookingsTabItem(
                            bookings = bookings,
                            viewModel = viewModel
                        )
                    )

                    bookingTabs.add(
                        BookingTabItem.DeclinedBookingsTabItem(
                            bookings = bookings,
                            viewModel = viewModel
                        )
                    )

                    val pagerState = rememberPagerState(pageCount = bookingTabs.size)

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

//                    LazyColumn(
//                        contentPadding = PaddingValues(8.dp)
//                    ) {
//                        list.groupBy { it.status }
//                            .apply { keys.sortedWith(compareBy { it.ordinal }) }
//                            .forEach { (status, books) ->
//                                stickyHeader { BookingListHeader(stringResource(status.stringRes)) }
//                                items(books) { book ->
//                                    Card(
//                                        modifier = Modifier.padding(8.dp),
//                                        elevation = 12.dp,
//                                        shape = RoundedCornerShape(12.dp)
//                                    ) {
//                                        Column(modifier = Modifier.padding(16.dp)) {
//                                            BookingTime(book.timeStart, book.timeEnd)
//                                            Spacer(modifier = Modifier.size(8.dp))
//                                            book.appliance?.let {
//                                                BookingAppliance(book.appliance!!, onApplianceClick = {
//                                                    navController.navigate(
//                                                        MainDestinations.APPLIANCE_ROUTE,
//                                                        Arguments.APPLIANCE to book.appliance!!
//                                                    )
//                                                })
//                                            }
//                                            book.user?.let {
//                                                BookingUser(book.user, onUserClick = {
//                                                    navController.navigate(
//                                                        MainDestinations.USER_DETAILS_ROUTE,
//                                                        Arguments.USER to book.user
//                                                    )
//                                                })
//                                            }
//                                            if (book.commentary.isNotBlank()) {
//                                                BookingCommentary(commentary = book.commentary)
//                                            }
//                                            BookingStatus(
//                                                book = book,
//                                                viewModel = viewModel,
//                                                currentUser = currentUser,
//                                                onApprove = viewModel::approveBook,
//                                                onDecline = viewModel::declineBook,
//                                                onUserClick = {
//                                                    navController.navigate(
//                                                        MainDestinations.USER_DETAILS_ROUTE,
//                                                        Arguments.USER to it
//                                                    )
//                                                }
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                    }
                }
            }
        }
    }
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
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.no_books))
    }
}

@Composable
fun BookingStatus(
    book: CalendarEvent,
    viewModel: BookingListViewModel,
    currentUser: State<User>,
    onDecline: (CalendarEvent) -> Unit,
    onApprove: (CalendarEvent) -> Unit,
    onUserClick: (User) -> Unit,

    ) {
    Divider()
    Spacer(modifier = Modifier.size(12.dp))
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = book.managedTime?.format(TimeConstants.FULL_DATE_FORMAT) ?: ""
                )
                book.managedUser?.let {
                    BookingUser(
                        user = book.managedUser,
                        shouldShowHeader = false
                    ) { onUserClick(it) }
                }
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = book.managedTime?.format(ofLocalizedDateTime(FormatStyle.MEDIUM)) ?: ""
                )
                book.managedUser?.let {
                    BookingUser(
                        user = book.managedUser,
                        shouldShowHeader = false
                    ) { onUserClick(it) }
                }

            }

        }
        BookingStatus.NONE -> {
            if (currentUser.value.isAdmin() || currentUser.value.let {
                    book.appliance?.superuserIds?.contains(it.userId) == true
                }) {
                BookingManagerButtons(
                    onDecline = { onDecline(book) },
                    onApprove = { onApprove(book) })
            }
        }
    }
}

@Composable
fun BookingManagerButtons(onDecline: () -> Unit, onApprove: () -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        TextButton(
            modifier = Modifier.weight(1f), onClick = onDecline,
        ) {
            Text(text = stringResource(id = R.string.decline), color = Color.Red)
        }
        TextButton(
            modifier = Modifier.weight(1f), onClick = onApprove,
        ) {
            Text(text = stringResource(id = R.string.approve), color = Color.Green)
        }
    }
}

@Composable
fun BookingCommentary(
    modifier: Modifier = Modifier,
    commentary: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TextDivider(text = stringResource(id = R.string.commentary))

        if (commentary.isBlank()) {
            SecondaryText(text = stringResource(R.string.no_commentary))
        } else {
            PrimaryText(
                text = stringResource(id = R.string.commentary),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BookingTime(
    modifier: Modifier = Modifier,
    timeStart: LocalDateTime,
    timeEnd: LocalDateTime
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
                text = "${
                    timeStart.toLocalTime().toHoursAndMinutes()
                } - ${
                    timeEnd.toLocalTime().toHoursAndMinutes()
                }"
            )
        }

        IconButton(onClick = { dialogState = !dialogState }) {
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
            )
        }
    }

    if (dialogState) {
        DefaultDialog(primaryText = stringResource(id = R.string.date_and_time)) {
            DateAndTime(
                date = timeStart.toLocalDate(),
                timeStart = timeStart.toLocalTime(),
                timeEnd = timeEnd.toLocalTime(),
                duration = null,
            )
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
fun BookingUser(user: User, shouldShowHeader: Boolean = true, onUserClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (shouldShowHeader) {
            TextDivider(text = stringResource(id = R.string.user))
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
