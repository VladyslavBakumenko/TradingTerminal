package com.sumin.terminal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumin.terminal.data.ApiFactory
import com.sumin.terminal.data.Bar
import com.sumin.terminal.data.UNDEFINED_INT_VALUE
import com.sumin.terminal.domain.tradesSnapshot.TradesSnapshotRepository
import com.sumin.terminal.domain.tradingBot.TradingBotRepository
import com.sumin.terminal.domain.utils.BuySell
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TerminalViewModel : ViewModel() {

    private val apiService = ApiFactory.apiService

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _state = MutableStateFlow<TerminalScreenState>(TerminalScreenState.Initial)
    val state = _state.asStateFlow()

    private var lastState: TerminalScreenState = TerminalScreenState.Initial

    private val tradesSnapshotRepository = TradesSnapshotRepository.getInstance()
    fun getTradesSnapshotRepository() = tradesSnapshotRepository

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _state.value = TerminalScreenState.Error
    }

    init {
        loadBarList()
    }

    private fun loadBarList(timeFrame: TimeFrame = TimeFrame.MIN_1) {
        lastState = _state.value
        _state.value = TerminalScreenState.Loading
        viewModelScope.launch {
            val response = apiService.loadBars(TimeFrame.MIN_1.value)
            val barList = response.barList
            val tradeBarList = tradeBarrList(barList)
            createSnapshotsAndSetState(tradeBarList)
            _state.value = TerminalScreenState.Content(barList = tradeBarList, timeFrame = timeFrame)
        }
    }

    private fun tradeBarrList(barList: List<Bar>): List<Bar> {
        val tradingBotRepository = TradingBotRepository(barList)
        return tradingBotRepository.trade()
    }

    fun setSnapshotView(snapshotBars: List<Bar>) {
        _state.value = TerminalScreenState.SnapshotContent(snapshotBars)
    }


    private fun createSnapshotsAndSetState(bars: List<Bar>) {
        var startIndex = UNDEFINED_INT_VALUE
        var endIndex = UNDEFINED_INT_VALUE
        var snapshotCounter = 0
        bars.reversed().forEachIndexed { index, bar ->

            bar.tradeInfoList?.let {
                val firstTradeInfoInList = if (it.isEmpty()) return else it.first()

                if (firstTradeInfoInList.buySell == BuySell.Buy && startIndex == -1) {
                    startIndex = index
                }

                if (firstTradeInfoInList.buySell == BuySell.Sell &&
                    firstTradeInfoInList.snapshotSequenceNumber == snapshotCounter
                ) {
                    endIndex = index
                }

                if (startIndex != -1 && endIndex != -1) {
//                    tradesSnapshotRepository.addSnapshotInList(
//                        bars.reversed().subList(startIndex - 60, endIndex + 61).reversed()
//                    )
                    startIndex = UNDEFINED_INT_VALUE
                    endIndex = UNDEFINED_INT_VALUE
                    snapshotCounter++
                }
            }
        }
    }
}
