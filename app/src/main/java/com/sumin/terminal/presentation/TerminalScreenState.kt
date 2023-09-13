package com.sumin.terminal.presentation

import com.sumin.terminal.data.Bar

sealed class TerminalScreenState {

    object Initial : TerminalScreenState()

    data class Content(val barList: List<Bar>) : TerminalScreenState()
}
