@file:OptIn(ExperimentalMaterial3Api::class)

import android.annotation.SuppressLint
import android.graphics.ImageDecoder.OnPartialImageListener
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zenglow.R
import com.example.zenglow.Screen
import com.example.zenglow.events.AppStateEvent
import com.example.zenglow.states.AppStateState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.Types.NULL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MoodBoostScreen(navController: NavController,
                    appStateState: AppStateState,
                    onAppStateEvent: (AppStateEvent) -> Unit,
                    ) {

    Scaffold(
        topBar = { MoodBoostTopBar { navController.navigateUp()}},

    ) {
        innerPadding -> MainScrollContent(innerPadding, navController, appStateState, onAppStateEvent)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScrollContent(
    innerPadding: PaddingValues,
    navController: NavController,
    appStateState: AppStateState,
    onAppStateEvent: (AppStateEvent) -> Unit,
    ){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color(0xEC, 0xEC, 0xEC)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Current Mood",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.Start) // Aligns the text to the start within the column
                .padding(start = 16.dp, top = 20.dp)  // Adds padding to the start
        )

        val currentPage = appStateState.currentMood
        val mypagerState = rememberPagerState(
                            pageCount = {7},
                            initialPage = currentPage
                            )
        LaunchedEffect(mypagerState) {
            // Collect from the a snapshotFlow reading the currentPage
            snapshotFlow { mypagerState.currentPage }.collect { page ->
                // Do something with each page change, for example:
                val updatedAppState = appStateState.copy(currentMood = mypagerState.currentPage)
                onAppStateEvent(AppStateEvent.UpdateAppState(updatedAppState))
            }
        }

        val imageList = listOf(R.drawable.mood_working3, R.drawable.mood_vibrant, R.drawable.mood_morning, R.drawable.fess_relax, R.drawable.mood_evening, R.drawable.mood_sleep, R.drawable.mood_neutral)
        val moodList = listOf("Focus", "Vibrant", "Morning", "Relax", "Evening", "Sleep", "Neutral")

        ImagePager(mypagerState, imageList, moodList)
        Spacer(modifier = Modifier.height(10.dp))

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            "Suggested Mood",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.Start) // Aligns the text to the start within the column
                .padding(start = 16.dp, top = 10.dp)  // Adds padding to the start
        )
        OverallScore(navController, appStateState)
        SuggestedMood(mypagerState,appStateState, moodList)


    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePager(
        pagerState: PagerState,
        imageList: List<Int>,
        moodList: List<String>
    ){


    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(start = 46.dp, end = 24.dp, top = 20.dp)
    ) {page->
        Card(
            Modifier
                .width(330.dp)
                .height(220.dp)
                .graphicsLayer {
                    val pageOffset = (
                            (pagerState.currentPage - page) + pagerState
                                .currentPageOffsetFraction
                            ).absoluteValue
                }
        ) {
            Box {
                Image(
                    painter = painterResource(id = imageList[page]),
                    contentDescription = "Description for accessibility",
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = moodList[page],
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 30.dp, bottom = 16.dp)// Position the text
                )
            }
        }
    }
}

@Composable
fun MoodBoostTopBar(onGoBackClicked: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text ="Mood Boost",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onGoBackClicked) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    modifier = Modifier.size(36.dp),
                    contentDescription = "Return back to home-page"
                )
            }
        }
    )
}



@Composable
fun OverallScore(
            navController: NavController,
            appStateState: AppStateState
        ){

    val score = ((appStateState.energy*100)*0.3 + (100 - appStateState.stressIndex*100)*0.3 + ((appStateState.mentalState+1)*20)*0.3).toInt()
    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .background(Color(0xEC, 0xEC, 0xEC)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            Modifier
                .width(340.dp)
                .height(250.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 12.dp),
                    text = "Overall score",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.W300,
                    fontSize = 25.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.sp
                )
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.W300,
                    fontSize = 90.sp
                )

                DatabaseProgressIndicator(databaseProgress = (score.toFloat()/100))
                Row(modifier = Modifier
                    .padding(top = 30.dp)){
                    TimeDisplay()
                    EditButtonExample(onClick = {navController.navigate("${Screen.EditMood.route}")})
                }
            }
        }
    }
}



@Composable
fun EditButtonExample(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.CenterEnd, // Aligns children to the center-end
        modifier = Modifier
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .padding(end = 10.dp, start = 10.dp)
                .width(170.dp)
                .height(40.dp)
        ) {
            Text("Edit Mood Factors",
                fontSize = 14.sp)
        }
    }
}


@Preview
@Composable
fun TimeDisplay() {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    // Update the time every second
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            delay(1000) // Wait for a second
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(4.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.AccessTime, // Material design time icon
            contentDescription = "Time Icon",
            modifier = Modifier.size(24.dp) // Icon size
        )
        Spacer(modifier = Modifier.width(6.dp)) // Space between icon and text
        Text(text = "$currentTime",
                fontSize = 26.sp)
        Spacer(modifier = Modifier.width(3.dp))
    }
}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SuggestedMood(pagerState: PagerState,
                  appStateState: AppStateState,
                  moodList: List<String>
                  ) {

    var currentTime by remember { mutableStateOf(getCurrentTime().take(2)) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime().take(2)
            delay(1000) // Wait for a second
        }
    }

    val energy = appStateState.energy
    val stressLevel = appStateState.stressIndex
    val mental = appStateState.mentalState

    var suggested = 6

    if(energy < 0.20 || currentTime.toInt() >= 22 || currentTime.toInt() < 6){
        suggested = 5
    } else if(stressLevel > 0.75){
        suggested = 3
    } else if(stressLevel < 0.5 && mental > 2){
        suggested = 0
    } else if(energy > 0.75 && mental >= 3){
        suggested = 1
    } else if(currentTime.toInt() in 6..10){
        suggested = 2
    } else if(currentTime.toInt() in 11..15){
        suggested = 6
    } else if(currentTime.toInt() in 16..21){
        suggested = 4
    }

    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .background(Color(0xEC, 0xEC, 0xEC)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .width(340.dp)
                .height(80.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically, // Center the content vertically in the row
                horizontalArrangement = Arrangement.SpaceBetween // Space between the text and button
            ) {

                Spacer(modifier = Modifier.width(8.dp))
                Text(moodList[suggested], style = MaterialTheme.typography.headlineMedium,)


                val coroutineScope = rememberCoroutineScope()
                FilledButtonExample(onClick = {
                    coroutineScope.launch {
                        // Call scroll to on pagerState
                        val animationSpec: AnimationSpec<Float> = TweenSpec(
                            durationMillis = 800
                        )
                        pagerState.animateScrollToPage(suggested, animationSpec = animationSpec)
                    }
                }) // Button to the right
            }
        }
    }
}

@Composable
fun FilledButtonExample(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.CenterEnd, // Aligns children to the center-end
        modifier = Modifier
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .padding(end = 10.dp)
                .width(110.dp)
                .height(40.dp)
        ) {
            Text("Apply",
                fontSize = 15.sp)
        }
    }
}

@Composable
fun DatabaseProgressIndicator(databaseProgress: Float) {
    LinearProgressIndicator(
        progress = {
            databaseProgress // Use the database value directly
        },
        modifier = Modifier.fillMaxWidth(),
    )
}
