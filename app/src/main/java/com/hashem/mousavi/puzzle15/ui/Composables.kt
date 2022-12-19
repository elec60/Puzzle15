package com.hashem.mousavi.puzzle15.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hashem.mousavi.puzzle15.ui.theme.Purple200
import com.hashem.mousavi.puzzle15.ui.theme.Purple700
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun Puzzle15Board(
    modifier: Modifier,
    cellInfos: List<CellInfo>,
    shakeState: SharedFlow<CellInfo>,
    onCellClicked: (CellInfo) -> Unit
) {
    var boardSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    val sizeOfCell = boardSize.width / 4
    val sizeOfCellInDp = with(LocalDensity.current) { sizeOfCell.toDp() }

    var cellToShake by remember {
        mutableStateOf<CellInfo?>(null)
    }

    LaunchedEffect(Unit) {
        shakeState.collectLatest {
            cellToShake = it
        }
    }

    Box(
        modifier = modifier.onGloballyPositioned {
            boardSize = it.size
        }
    ) {
        Layout(
            content = {
                cellInfos.forEach {
                    Cell(
                        modifier = Modifier
                            .shake(
                                enabled = it == cellToShake,
                                correctionX = it.offsetState.x.toFloat() / it.size,
                                correctionY = it.offsetState.y.toFloat() / it.size,
                                shakeFinished = {
                                    cellToShake = null
                                }
                            )
                            .size(size = sizeOfCellInDp)
                            .padding(1.dp),
                        cellInfo = it.apply { size = sizeOfCell }
                    ) { cellInfo ->
                        onCellClicked(cellInfo)
                    }
                }
            }
        ) { measurables, constraints ->

            val placeables = measurables.map {
                it.measure(constraints)
            }

            layout(width = boardSize.width, height = boardSize.height) {
                var x: Int
                var y: Int
                placeables.forEachIndexed { index, placeable ->
                    x = placeable.width * (index % 4)
                    y = placeable.height * (index / 4)

                    //put empty cell on lower zIndex
                    placeable.place(x, y, zIndex = if (index == placeables.size - 1) 0f else 1f)
                }
            }
        }
    }

}


@Composable
fun Cell(
    modifier: Modifier,
    cellInfo: CellInfo,
    onCellClicked: (CellInfo) -> Unit
) {
    val update = updateTransition(targetState = cellInfo.offsetState, label = "")
    val animateOffset by update.animateIntOffset(label = "") { it }

    Box(
        modifier = modifier
            .offset {
                animateOffset
            }
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = if (cellInfo.number == 0) null else rememberRipple(
                    bounded = true,
                    radius = 100.dp,
                    color = Color.Green
                )
            ) {
                if (cellInfo.number != 0) {
                    onCellClicked(cellInfo)
                }
            }
            .background(
                color = if (cellInfo.number != 0) {
                    Color.Red
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (cellInfo.number != 0) {
            Text(
                text = cellInfo.number.toString(),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
            )
        }
    }
}

fun Modifier.shake(
    enabled: Boolean,
    correctionX: Float,
    correctionY: Float,
    shakeFinished: () -> Unit
) = composed(
    factory = {
        val scale by animateFloatAsState(
            targetValue = if (enabled) 1f else 0.9f,
            animationSpec = repeatable(
                iterations = 5,
                animation = tween(durationMillis = 50, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            finishedListener = {
                shakeFinished()
            }
        )

        Modifier.graphicsLayer(
            transformOrigin = TransformOrigin(
                pivotFractionX = 0.5f + if (!correctionX.isNaN()) correctionX else 0f,
                pivotFractionY = 0.5f + +if (!correctionY.isNaN()) correctionY else 0f
            ),
            scaleX = if (enabled) scale else 1f,
            scaleY = if (enabled) scale else 1f
        )
    }
)

@Preview
@Composable
fun CellPreview() {
    Cell(
        modifier = Modifier.size(50.dp),
        cellInfo = CellInfo(number = 10, 0, 0, 50),
        onCellClicked = {}
    )
}

data class CellInfo(
    val number: Int,
    val row: Int,
    val column: Int,
    var size: Int,
) {
    var offsetState by mutableStateOf(IntOffset.Zero)

    val actualRow: Int
        get() = offsetState.y / size + row

    val actualColumn: Int
        get() = offsetState.x / size + column

}