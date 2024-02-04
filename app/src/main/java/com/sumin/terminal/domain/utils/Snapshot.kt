package com.sumin.terminal.domain.utils

import com.sumin.terminal.data.Bar

data class Snapshot(val bars: List<Bar>, val tradeInfo: List<TradeInfo>)