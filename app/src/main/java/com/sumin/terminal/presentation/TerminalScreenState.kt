package com.sumin.terminal.presentation

import com.sumin.terminal.data.Bar

sealed class TerminalScreenState {

    object Initial : TerminalScreenState()

    object Loading : TerminalScreenState()

    object Error : TerminalScreenState()

    data class Content(val barList: List<Bar>, val timeFrame: TimeFrame) : TerminalScreenState()

    data class SnapshotContent(val snapshotBars: List<Bar>) : TerminalScreenState()
}
