package com.sumin.terminal.domain.statistics

import com.sumin.terminal.data.Bar
import com.sumin.terminal.presentation.TimeFrame

class PriceStatisticsRepository {

    private val priceStatisticsImpl = PriceStatisticsImpl()

    fun initPriceStatisticsPerTimeListList() {
        priceStatisticsImpl.initPriceStatisticsPerTimeListList()
    }

    fun addBarToStatistics(bar: Bar) {
        priceStatisticsImpl.addBarToStatistics(bar)
    }

    fun getAllStatisticsPerTimeFrame(timeFrame: TimeFrame) =
        priceStatisticsImpl.getAllStatisticsPerTimeFrame(timeFrame)

    fun getPercentageDifferentListByTimeFrameList(timeFrameList: List<TimeFrame>) =
        priceStatisticsImpl.getPercentageDifferentListByTimeFrameList(timeFrameList)
}