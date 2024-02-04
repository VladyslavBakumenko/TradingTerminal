package com.sumin.terminal.domain.tradingBot

import com.sumin.terminal.data.Bar

class TradingBotRepository(bars: List<Bar>) {

    private val tradingBotImpl = TradingBotImpl(bars)

    fun trade() = tradingBotImpl.tradeReceivedBars()
}