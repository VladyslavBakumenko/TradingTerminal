package com.sumin.terminal.domain.statistics

import com.sumin.terminal.data.Bar
import com.sumin.terminal.presentation.TimeFrame

class PriceStatisticsImpl {

    private var timeFrameList = mutableListOf<TimeFrame>()
    private var priceStatisticPerTimeFrameList = mutableListOf<PriceStatisticPerTimeFrame>()

    fun initPriceStatisticsPerTimeListList() {
        for (i in TimeFrame.values()) {
            priceStatisticPerTimeFrameList.add(PriceStatisticPerTimeFrame(i))
            timeFrameList.add(i)
        }
    }

    fun addBarToStatistics(bar: Bar) {
        for (i in priceStatisticPerTimeFrameList) {
            i.addBarToStatistics(bar)
        }
    }

    fun getAllStatisticsPerTimeFrame(timeFrame: TimeFrame): PriceStatisticPerTimeFrame {
        return priceStatisticPerTimeFrameList.find {
            it.getTimeFrame() == timeFrame
        } ?: throw IllegalArgumentException("element not found")
    }

    fun getPercentageDifferentListByTimeFrameList(timeFrameList: List<TimeFrame>): MutableList<Pair<TimeFrame, Float>> {
        val result = mutableListOf<Pair<TimeFrame, Float>>()
        for (timeFrame in timeFrameList) {
            result.add(
                Pair(
                    timeFrame,
                    getAllStatisticsPerTimeFrame(timeFrame).getPercentageDifference()
                )
            )
        }
        return result
    }
}