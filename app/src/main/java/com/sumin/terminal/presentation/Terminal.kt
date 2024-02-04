package com.sumin.terminal.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sumin.terminal.R
import com.sumin.terminal.data.Bar
import com.sumin.terminal.presentation.terminal.Chart

@Composable
fun Terminal(
    modifier: Modifier = Modifier
) {
    val viewModel: TerminalViewModel = viewModel()
    val screenState = viewModel.state.collectAsState()
    when (val currentState = screenState.value) {
        is TerminalScreenState.Content -> {
            val terminalState = rememberTerminalState(bars = currentState.barList)

            Chart(
                modifier = modifier,
                terminalState = terminalState,
                onTerminalStateChanged = {
                    terminalState.value = it
                },
                timeFrame = currentState.timeFrame
            )

            currentState.barList.firstOrNull()?.let {
                Prices(
                    modifier = modifier,
                    terminalState = terminalState,
                    lastPrice = it.close
                )
            }

            Test(viewModel.getTradesSnapshotRepository().getSnapshotList()) {
                viewModel.setSnapshotView(it)
            }
        }

        is TerminalScreenState.Initial -> {

        }

        TerminalScreenState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        TerminalScreenState.Error -> {
            Text(text = "Error")
        }

        is TerminalScreenState.SnapshotContent -> {
            val terminalState = rememberTerminalState(bars = currentState.snapshotBars)

            Chart(
                modifier = modifier,
                terminalState = terminalState,
                onTerminalStateChanged = {
                    terminalState.value = it
                },
                timeFrame = TimeFrame.MIN_1
            )

            currentState.snapshotBars.firstOrNull()?.let {
                Prices(
                    modifier = modifier,
                    terminalState = terminalState,
                    lastPrice = it.close
                )
            }

            Test(viewModel.getTradesSnapshotRepository().getSnapshotList()) {
                viewModel.setSnapshotView(it)
            }
        }
    }
}

@Composable
private fun TimeFrames(
    selectedFrame: TimeFrame,
    onTimeFrameSelected: (TimeFrame) -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimeFrame.values().forEach { timeFrame ->
            val labelResId = when (timeFrame) {
                TimeFrame.MIN_1 -> R.string.timeframe_1_minutes
                TimeFrame.MIN_5 -> R.string.timeframe_5_minutes
                TimeFrame.MIN_15 -> R.string.timeframe_15_minutes
                TimeFrame.MIN_30 -> R.string.timeframe_30_minutes
                TimeFrame.HOUR_1 -> R.string.timeframe_1_hour
                else -> null
            }
            labelResId?.let {
                TimeFrame(timeFrame, selectedFrame, it, onTimeFrameSelected)
            }
        }
    }
}

private var selectedId = -1

@Composable
private fun Test(
    snapshotBarsList: List<List<Bar>>,
    selectedSnapshotListener: (snapshotBars: List<Bar>) -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp)
    ) {
        snapshotBarsList.forEachIndexed { index, snapshot ->
            Tests(index, selectedId) {
                selectedId = it
                selectedSnapshotListener(snapshot)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Tests(
    id: Int,
    selectedId: Int,
    onSelectedSnapshot: (id: Int) -> Unit
) {
    val isSelected = id == selectedId
    AssistChip(
        onClick = {
            onSelectedSnapshot(id)
        },
        label = {
            Text(text = id.toString())
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected) Color.White else Color.Black,
            labelColor = if (isSelected) Color.Black else Color.White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeFrame(
    timeFrame: TimeFrame,
    selectedFrame: TimeFrame,
    labelResId: Int,
    onTimeFrameSelected: (timeFrame: TimeFrame) -> Unit
) {
    val isSelected = timeFrame == selectedFrame
    AssistChip(
        onClick = { onTimeFrameSelected(timeFrame) },
        label = { Text(text = stringResource(id = labelResId)) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected) Color.White else Color.Black,
            labelColor = if (isSelected) Color.Black else Color.White
        )
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun Prices(
    modifier: Modifier = Modifier,
    terminalState: State<TerminalState>,
    lastPrice: Float
) {
    val currentState = terminalState.value
    val textMeasurer = rememberTextMeasurer()
    val max = currentState.max
    val min = currentState.min
    val pxPerPoint = currentState.pxPerPoint

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .padding(vertical = 32.dp)
    ) {
        drawPrices(max, min, pxPerPoint, lastPrice, textMeasurer)
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawPrices(
    max: Float,
    min: Float,
    pxPerPoint: Float,
    lastPrice: Float,
    textMeasurer: TextMeasurer
) {
    // max
    val maxPriceOffsetY = 0f
    drawDashedLine(
        start = Offset(0f, maxPriceOffsetY),
        end = Offset(size.width, maxPriceOffsetY),
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = max,
        offsetY = maxPriceOffsetY
    )

    // last price
    val lastPriceOffsetY = size.height - ((lastPrice - min) * pxPerPoint)
    drawDashedLine(
        start = Offset(0f, lastPriceOffsetY),
        end = Offset(size.width, lastPriceOffsetY),
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = lastPrice,
        offsetY = lastPriceOffsetY
    )

    // min
    val minPriceOffsetY = size.height
    drawDashedLine(
        start = Offset(0f, minPriceOffsetY),
        end = Offset(size.width, minPriceOffsetY),
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = min,
        offsetY = minPriceOffsetY
    )
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawTextPrice(
    textMeasurer: TextMeasurer,
    price: Float,
    offsetY: Float
) {
    val textLayoutResult = textMeasurer.measure(
        text = price.toString(),
        style = TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(size.width - textLayoutResult.size.width - 4.dp.toPx(), offsetY)
    )
}

private fun DrawScope.drawDashedLine(
    color: Color = Color.White,
    start: Offset,
    end: Offset,
    strokeWidth: Float = 1f
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                4.dp.toPx(), 4.dp.toPx()
            )
        )
    )
}
