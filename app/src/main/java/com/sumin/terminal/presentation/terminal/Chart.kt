package com.sumin.terminal.presentation.terminal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sumin.terminal.data.Bar
import com.sumin.terminal.domain.utils.BuySell
import com.sumin.terminal.presentation.TerminalState
import com.sumin.terminal.presentation.TimeFrame
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

private const val MIN_VISIBLE_BARS_COUNT = 20

@OptIn(ExperimentalTextApi::class)
@Composable
fun Chart(
    modifier: Modifier = Modifier,
    terminalState: State<TerminalState>,
    onTerminalStateChanged: (TerminalState) -> Unit,
    timeFrame: TimeFrame
) {
    val currentState = terminalState.value
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val visibleBarsCount = (currentState.visibleBarsCount / zoomChange).roundToInt()
            .coerceIn(MIN_VISIBLE_BARS_COUNT, currentState.barList.size)

        val scrolledBy = (currentState.scrolledBy + panChange.x)
            .coerceAtLeast(0f)
            .coerceAtMost(currentState.barList.size * currentState.barWidth - currentState.terminalWidth)

        onTerminalStateChanged(
            currentState.copy(
                visibleBarsCount = visibleBarsCount,
                scrolledBy = scrolledBy
            )
        )
    }

    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clipToBounds()
            .padding(
                top = 32.dp,
                bottom = 32.dp,
                end = 32.dp
            )
            .transformable(transformableState)
            .onSizeChanged {
                onTerminalStateChanged(
                    currentState.copy(
                        terminalWidth = it.width.toFloat(),
                        terminalHeight = it.height.toFloat()
                    )
                )
            }
    ) {
        val min = currentState.min
        val pxPerPoint = currentState.pxPerPoint
        translate(left = currentState.scrolledBy) {
            currentState.barList.forEachIndexed { index, bar ->
                val offsetX = size.width - index * currentState.barWidth
                drawTimeDelimiter(
                    bar = bar,
                    nextBar = if (index < currentState.barList.size - 1) {
                        currentState.barList[index + 1]
                    } else {
                        null
                    },
                    timeFrame = timeFrame,
                    offsetX = offsetX,
                    textMeasurer = textMeasurer
                )
                drawLine(
                    color = Color.White,
                    start = Offset(offsetX, size.height - ((bar.low - min) * pxPerPoint)),
                    end = Offset(offsetX, size.height - ((bar.high - min) * pxPerPoint)),
                    strokeWidth = 1f
                )
                drawLine(
                    color = if (bar.open < bar.close) Color.Green else Color.Red,
                    start = Offset(offsetX, size.height - ((bar.open - min) * pxPerPoint)),
                    end = Offset(offsetX, size.height - ((bar.close - min) * pxPerPoint)),
                    strokeWidth = currentState.barWidth / 2
                )
                bar.tradeInfoList?.let { tradingInfoList ->
                    for (tradingInfo in tradingInfoList) {
                        drawCircle(
                            if (tradingInfo.buySell == BuySell.Buy) Color.Magenta else Color.Cyan,
                            radius = 15f,
                            center = Offset(
                                offsetX,
                                size.height - (tradingInfo.cryptocurrencyPrice.toFloat() - min) * pxPerPoint
                            )
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawTimeDelimiter(
    bar: Bar,
    nextBar: Bar?,
    timeFrame: TimeFrame,
    offsetX: Float,
    textMeasurer: TextMeasurer
) {
    val calendar = bar.calendar

    val minutes = calendar.get(Calendar.MINUTE)
    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val shouldDrawDelimiter = when (timeFrame) {
        TimeFrame.MIN_1 -> {
            minutes == 0
        }

        TimeFrame.MIN_5 -> {
            minutes == 0
        }

        TimeFrame.MIN_15 -> {
            minutes == 0 && hours % 2 == 0
        }

        TimeFrame.MIN_30, TimeFrame.HOUR_1 -> {
            val nextBarDay = nextBar?.calendar?.get(Calendar.DAY_OF_MONTH)
            day != nextBarDay
        }

        else -> throw RuntimeException()

    }
    if (!shouldDrawDelimiter) return

    drawLine(
        color = Color.White.copy(alpha = 0.5f),
        start = Offset(offsetX, 0f),
        end = Offset(offsetX, size.height),
        strokeWidth = 1f,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(4.dp.toPx(), 4.dp.toPx())
        )
    )

    val nameOfMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
    val text = when (timeFrame) {

        TimeFrame.MIN_1, TimeFrame.MIN_5, TimeFrame.MIN_15 -> {
            String.format("%02d:00", hours)
        }

        TimeFrame.MIN_30, TimeFrame.HOUR_1 -> {
            String.format("%s %s", day, nameOfMonth)
        }

        else -> throw RuntimeException()
    }
    val textLayoutResult = textMeasurer.measure(
        text = text,
        style = TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(offsetX - textLayoutResult.size.width / 2, size.height)
    )
}