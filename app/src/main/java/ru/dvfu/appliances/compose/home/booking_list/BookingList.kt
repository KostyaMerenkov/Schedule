package ru.dvfu.appliances.compose.home.booking_list

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.*
import ru.dvfu.appliances.compose.appliance.UserImage
import ru.dvfu.appliances.compose.home.DateAndTime
import ru.dvfu.appliances.compose.views.*
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import java.time.LocalDateTime

@SuppressLint("UnrememberedMutableState")
@OptIn(
    ExperimentalCoilApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.animation.ExperimentalAnimationApi::class,
    androidx.compose.material.ExperimentalMaterialApi::class,
    com.google.accompanist.pager.ExperimentalPagerApi::class
)
@Composable
fun BookingList(navController: NavController) {
    /*val viewModel: BookingListViewModel = getViewModel()
    val currentUser = viewModel.currentUser.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val list by viewModel.bookingList.collectAsState()
    val managingUiState by viewModel.managingUiState.collectAsState()

    if (managingUiState is UiState.InProgress) ModalLoadingDialog(
        mutableStateOf(true), text = stringResource(
            id = R.string.loading
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScheduleAppBar(
                title = "Бронирования",
                backClick = { navController.popBackStack() })
        }) {
        Crossfade(targetState = uiState) {
            when (it) {
                is ViewState.Error -> {
                    //ErrorView()
                }
                is ViewState.Loading -> {
                    LoadingItem(Modifier.fillMaxSize())
                }
                is ViewState.Success -> {
                    if (list.isEmpty()) NoBookingsView(Modifier.fillMaxSize())

                    val tabs = listOf(
                        BookingTabItem.PendingBookingsTabItem(list),
                        BookingTabItem.ApprovedBookingsTabItem(list),
                        BookingTabItem.DeclinedBookingsTabItem(list)
                    )

                    val pagerState = rememberPagerState(pageCount = tabs.size)

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        BookingListTabsView(
                            tabsList = tabs,
                            pagerState = pagerState
                        )

                        BookingTabsContent(
                            tabsList = tabs,
                            pagerState = pagerState
                        )

                    }*/

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
                /*}
            }
        }
    }*/
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
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.no_books))
    }
}

/*@Composable
fun BookingStatus(
    book: UiBooking,
    viewModel: BookingListViewModel,
    currentUser: State<User>,
    onDecline: (UiBooking) -> Unit,
    onApprove: (UiBooking) -> Unit,
    onUserClick: (User) -> Unit,

    ) {
    Divider()
    Spacer(modifier = Modifier.size(12.dp))
    when (book.status) {
        APPROVED -> {
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
                    text = book.managedTime.toLocalDateTime().format(TimeConstants.FULL_DATE_FORMAT)
                )
                book.managedUser?.let {
                    BookingUser(
                        user = book.managedUser,
                        shouldShowHeader = false
                    ) { onUserClick(it) }
                }
            }

        }
        DECLINED -> {
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
                    text = book.managedTime.toZonedDateTime()
                        .format(ofLocalizedDateTime(FormatStyle.MEDIUM))
                )
                book.managedUser?.let {
                    BookingUser(
                        user = book.managedUser,
                        shouldShowHeader = false
                    ) { onUserClick(it) }
                }

            }

        }
        NONE -> {
            if (currentUser.value.isAdmin() || currentUser.value.let {
                    book.appliance?.superuserIds?.contains(it.userId) == true
                }) {
                BookingManagerButtons(
                    onDecline = { onDecline(book) },
                    onApprove = { onApprove(book) })
            }
        }
    }
}*/

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
fun BookingCommentary(commentary: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        PrimaryText(
            text = stringResource(id = R.string.commentary),
            modifier = Modifier.fillMaxWidth()
        )
        Text(text = commentary)
    }
}

@Composable
fun BookingTime(timeStart: LocalDateTime, timeEnd: LocalDateTime) {
    DateAndTime(
        date = timeStart.toLocalDate(),
        timeStart = timeStart.toLocalTime(),
        timeEnd = timeEnd.toLocalTime(),
        duration = null,
    )
}

@Composable
fun BookingAppliance(
    appliance: Appliance,
    shouldShowHeader: Boolean = true,
    onApplianceClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (shouldShowHeader) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SecondaryTextSmall(
                    modifier = Modifier.padding(4.dp),
                    text = stringResource(id = R.string.appliance)
                )
                Divider()
            }
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
                    appliance, modifier = Modifier
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SecondaryTextSmall(
                    modifier = Modifier.padding(4.dp),
                    text = stringResource(id = R.string.user)
                )
                Divider()
            }

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
