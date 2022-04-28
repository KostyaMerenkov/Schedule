package ru.dvfu.appliances.compose.home.booking_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.ui.theme.customColors
import ru.dvfu.appliances.compose.viewmodels.BookingListViewModel
import ru.dvfu.appliances.compose.viewmodels.CalendarEventDateAndTime
import ru.dvfu.appliances.compose.views.DefaultButton
import ru.dvfu.appliances.compose.views.HeaderText
import ru.dvfu.appliances.compose.views.PrimaryText
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.utils.toMillis
import java.time.LocalDateTime


sealed class BookingTabItem(var titleRes: Int, var screen: @Composable () -> Unit) {

    class PendingBookingsTabItem(bookings: List<CalendarEvent>, viewModel: BookingListViewModel) :
        BookingTabItem(
            titleRes = R.string.pending,
            screen = { PendingBookingsList(bookings, viewModel) }
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
    onApproveClick: () -> Unit,
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
            booking.user?.let {
                BookingUser(booking.user, onUserClick = {
//                    navController.navigate(
//                        MainDestinations.USER_DETAILS_ROUTE,
//                        Arguments.USER to book.user
//                    )
                })
            }

            BookingCommentary(commentary = booking.commentary)

            BookingButtons(
                onApproveClick = onApproveClick,
                onDeclineClick = onDeclineClick
            )

            Spacer(modifier = Modifier.size(16.dp))
//            BookingStatus(
//                book = booking,
//                viewModel = viewModel,
//                currentUser = currentUser,
//                onApprove = viewModel::approveBook,
//                onDecline = viewModel::declineBook,
//                onUserClick = {
//                    navController.navigate(
//                        MainDestinations.USER_DETAILS_ROUTE,
//                        Arguments.USER to it
//                    )
//                }
//            )
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
//            booking.user?.let {
//                BookingUser(booking.user, onUserClick = {
////                    navController.navigate(
////                        MainDestinations.USER_DETAILS_ROUTE,
////                        Arguments.USER to book.user
////                    )
//                })
//            }

            BookingCommentary(commentary = booking.commentary)

            DeclineBookingButton(onDeclineClick = onDeclineClick)

            Spacer(modifier = Modifier.size(16.dp))
//            BookingStatus(
//                book = booking,
//                viewModel = viewModel,
//                currentUser = currentUser,
//                onApprove = viewModel::approveBook,
//                onDecline = viewModel::declineBook,
//                onUserClick = {
//                    navController.navigate(
//                        MainDestinations.USER_DETAILS_ROUTE,
//                        Arguments.USER to it
//                    )
//                }
//            )
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

            BookingCommentary(commentary = booking.managerCommentary)

            DeclineBookingButton(onDeclineClick = onDeclineClick)


//            BookingStatus(
//                book = booking,
//                viewModel = viewModel,
//                currentUser = currentUser,
//                onApprove = viewModel::approveBook,
//                onDecline = viewModel::declineBook,
//                onUserClick = {
//                    navController.navigate(
//                        MainDestinations.USER_DETAILS_ROUTE,
//                        Arguments.USER to it
//                    )
//                }
//            )
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

            BookingCommentary(commentary = booking.managerCommentary)

            Spacer(modifier = Modifier.size(32.dp))

//            BookingStatus(
//                book = booking,
//                viewModel = viewModel,
//                currentUser = currentUser,
//                onApprove = viewModel::approveBook,
//                onDecline = viewModel::declineBook,
//                onUserClick = {
//                    navController.navigate(
//                        MainDestinations.USER_DETAILS_ROUTE,
//                        Arguments.USER to it
//                    )
//                }
//            )
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

