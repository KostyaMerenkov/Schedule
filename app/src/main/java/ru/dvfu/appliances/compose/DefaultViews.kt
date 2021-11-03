package ru.dvfu.appliances.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.dvfu.appliances.R
import ru.dvfu.appliances.model.repository.entity.User

@Composable
fun MyCardNoPadding(content: @Composable () -> Unit) {
    Card(elevation = 4.dp, shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(), content = content)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyCard(modifier: Modifier = Modifier, onClick: () -> Unit = {}, content: @Composable () -> Unit) {
    Card(elevation = 8.dp, modifier = modifier.fillMaxWidth().padding(4.dp), content = content,
        shape = RoundedCornerShape(12.dp), onClick = onClick)
}

@Composable
fun SubtitleWithIcon(modifier: Modifier = Modifier, icon: ImageVector, text: String) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.size(8.dp))
        Icon(
            icon,
            contentDescription = text,
            //tint = pr,
            modifier = Modifier.size(30.dp)
        )
        Spacer(Modifier.size(8.dp))
        Text(text)
    }
}

@Composable
fun ScheduleAppBar(
    title: String = "",
    backClick: () -> Unit = {},
    actionDelete: Boolean = false,
    deleteClick: () -> Unit = {},
    navIconBack: Boolean = true) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = backClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            if (actionDelete) {
                IconButton(
                    onClick = deleteClick) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = stringResource(R.string.delete), tint = Color.White)
                }
            }
        },
        elevation = 2.dp
    )
}

/*
@Composable
fun UserProfile(user: User?) {
    user?.let { nutNullUser ->
        Crossfade(nutNullUser, animationSpec = tween(500)) { animatedUser ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberImagePainter(
                        data = animatedUser.userPic,
                        builder = {
                            transformations(CircleCropTransformation())
                            //crossfade(500)
                        }
                    ),
                    contentDescription = stringResource(R.string.fisher),
                    modifier = Modifier.padding(9.dp),
                )
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        animatedUser.userName.split(" ")[0],
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.button.fontSize
                    )
                    Text(
                        "@" + stringResource(R.string.fisher),
                        fontSize = MaterialTheme.typography.caption.fontSize
                    )
                }
            }
        }
    } ?: Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.ic_fisher),
            contentDescription = stringResource(R.string.fisher),
            Modifier.fillMaxHeight().padding(10.dp)
        )
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                stringResource(R.string.fisher),
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.button.fontSize
            )
            Text(
                "@" + stringResource(R.string.fisher),
                fontSize = MaterialTheme.typography.caption.fontSize
            )
        }
    }
}

@Composable
fun PlaceInfo(user: User?, place: UserMapMarker, placeClicked: (UserMapMarker) -> Unit) {
    MyCardNoPadding {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp).padding(horizontal = 5.dp).clickable { placeClicked(place) }
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Place, stringResource(R.string.place), tint = secondaryFigmaColor)
                Spacer(modifier = Modifier.width(150.dp))
                UserProfile(user)
            }
            Text(place.title, fontWeight = FontWeight.Bold)
            if (!place.description.isNullOrEmpty()) Text(place.description!!)
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}

@Composable
fun CatchInfo(catch: UserCatch, user: User?) {
    MyCardNoPadding {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp).padding(horizontal = 5.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth().height(50.dp)
            ) {
                Text(
                    catch.title,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Row( modifier = Modifier
                    .padding(horizontal = 10.dp).fillMaxHeight()) {
                UserProfile(user) }
            }
            if (!catch.description.isNullOrEmpty()) Text(
                catch.description, modifier = Modifier.fillMaxWidth(),
                fontSize = MaterialTheme.typography.button.fontSize
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(catch.time, fontSize = MaterialTheme.typography.caption.fontSize)
                Text(catch.date, fontSize = MaterialTheme.typography.caption.fontSize)
            }
        }
    }
}*/