@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.aspark.tindercardswipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.aspark.tindercardswipe.ui.theme.TinderCardSwipeTheme
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TinderCardSwipeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CardSwipeScreen()
                }
            }
        }
    }
}

@Composable
fun CardSwipeScreen() {
    val density = LocalDensity.current

    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.START,
            positionalThreshold = {
                    totalDistance: Float ->  totalDistance * 0.4f
            },
            velocityThreshold = {
                with(density) { 3000.dp.toPx() }
            },
            animationSpec = tween(
                200,
                0,
                EaseIn
            )
        ).apply {
            updateAnchors(
                DraggableAnchors {
                    DragAnchors.START at 0f
                    DragAnchors.LEFT_END at -1200f
                    DragAnchors.RIGHT_END at 1200f
                }
            )
        }
    }
    
    val cardModifier =
        Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(horizontal = 16.dp)

    Box(
        contentAlignment = Alignment.Center
    ) {
        SwipeableCard(SwipeableCard(0, "Dani", "IYKYK", "BLR"),
            draggableState, cardModifier)
    }

    Column(
        modifier = Modifier
            .padding(vertical = 32.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        UndoButton(draggableState, cardModifier)
    }
}

@Composable
fun SwipeableCard(card: SwipeableCard, draggableState: AnchoredDraggableState<DragAnchors>,
                  modifier: Modifier) {

    ElevatedCard(
        modifier = modifier
            .offset {

                val xStateOffset = draggableState
                    .requireOffset()
                    .toInt()

                // used parabola equation x = y*y and -x = y*y
                val yStateOffset = if (xStateOffset >= 0) sqrt(xStateOffset.toDouble())
                else sqrt(-xStateOffset.toDouble())

                IntOffset(
                    x = xStateOffset,
                    y = -yStateOffset.toInt() * 10
                )
            }
            .anchoredDraggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
            ),
        onClick = {},
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(24.dp)
    ) {

    }
}

@Composable
fun UndoButton(draggableState: AnchoredDraggableState<DragAnchors>, cardModifier: Modifier) {

    val remState = remember {
        mutableStateOf(false)
    }

    SmallFloatingActionButton(
        modifier = Modifier.size(60.dp),
        onClick = { remState.value = true },
        shape = CircleShape
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_undo) , contentDescription = "")
    }

    if (remState.value) {
        LaunchedEffect(Unit) {
            draggableState.snapTo(DragAnchors.START)
        }
        remState.value = false
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TinderCardSwipeTheme {
        CardSwipeScreen()
    }
}