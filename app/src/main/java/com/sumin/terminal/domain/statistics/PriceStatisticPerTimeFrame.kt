package com.sumin.terminal.domain.statistics

import com.sumin.terminal.data.Bar
import com.sumin.terminal.data.UNDEFINED_DOUBLE_VALUE
import com.sumin.terminal.data.UNDEFINED_FLOAT_VALUE
import com.sumin.terminal.data.UNDEFINED_INT_VALUE
import com.sumin.terminal.presentation.TimeFrame


data class PriceStatisticPerTimeFrame(private val timeFrame: TimeFrame) {

    private var statisticAreComplete = false
    private var greenBarsCounter = UNDEFINED_INT_VALUE
    private var redBardCounter = UNDEFINED_INT_VALUE
    private var percentageDifference = UNDEFINED_FLOAT_VALUE
    private var highestPricePerTimeFrame = UNDEFINED_FLOAT_VALUE
    private var lowestPricePerTimeFrame = UNDEFINED_FLOAT_VALUE
    private var avgOpenClosePrice = UNDEFINED_DOUBLE_VALUE
    private var avgOpenPricePerTimeFrame = UNDEFINED_DOUBLE_VALUE
    private var avgClosePricePerTimeFrame = UNDEFINED_DOUBLE_VALUE
    private var avgLowPricePerTimeFrame = UNDEFINED_DOUBLE_VALUE
    private var avgHighPricePerTimeFrame = UNDEFINED_DOUBLE_VALUE
    private var statisticBarListPerTimeFrame = mutableListOf<Bar>()

    fun getTimeFrame() = timeFrame

    fun isStatisticAreComplete() = statisticAreComplete

    fun getGreenBarsCounter() = greenBarsCounter

    fun getRedBardCounter() = redBardCounter

    fun getPercentageDifference() = percentageDifference

    fun getHighestPricePerTimeFrame() = highestPricePerTimeFrame

    fun getLowestPricePerTimeFrame() = lowestPricePerTimeFrame

    fun getAvgOpenClosePrice() = avgOpenClosePrice

    fun getAvgOpenPricePerTimeFrame() = avgOpenPricePerTimeFrame

    fun getAvgClosePricePerTimeFrame() = avgClosePricePerTimeFrame

    fun getAvgLowPricePerTimeFrame() = avgLowPricePerTimeFrame

    fun getAvgHighPricePerTimeFrame() = avgHighPricePerTimeFrame

    fun getStatisticBarListPerTimeFrame() = statisticBarListPerTimeFrame

    fun addBarToStatistics(bar: Bar) {
        when (timeFrame) {
            TimeFrame.MIN_1 -> addBarInList(1, bar)
            TimeFrame.MIN_2 -> addBarInList(2, bar)
            TimeFrame.MIN_3 -> addBarInList(3, bar)
            TimeFrame.MIN_5 -> addBarInList(5, bar)
            TimeFrame.MIN_10 -> addBarInList(10, bar)
            TimeFrame.MIN_15 -> addBarInList(15, bar)
            TimeFrame.MIN_20 -> addBarInList(20, bar)
            TimeFrame.MIN_25 -> addBarInList(25, bar)
            TimeFrame.MIN_30 -> addBarInList(30, bar)
            TimeFrame.MIN_35 -> addBarInList(35, bar)
            TimeFrame.MIN_40 -> addBarInList(40, bar)
            TimeFrame.MIN_45 -> addBarInList(45, bar)
            TimeFrame.MIN_50 -> addBarInList(50, bar)
            TimeFrame.MIN_55 -> addBarInList(55, bar)
            TimeFrame.HOUR_1 -> addBarInList(60, bar)
            TimeFrame.HOUR_2 -> addBarInList(120, bar)
            TimeFrame.HOUR_3 -> addBarInList(180, bar)
            TimeFrame.HOUR_4 -> addBarInList(240, bar)
            TimeFrame.HOUR_5 -> addBarInList(300, bar)
            TimeFrame.HOUR_6 -> addBarInList(360, bar)
            TimeFrame.HOUR_7 -> addBarInList(420, bar)
            TimeFrame.HOUR_8 -> addBarInList(480, bar)
            TimeFrame.HOUR_9 -> addBarInList(540, bar)
            TimeFrame.HOUR_10 -> addBarInList(600, bar)
            TimeFrame.HOUR_11 -> addBarInList(660, bar)
            TimeFrame.HOUR_12 -> addBarInList(720, bar)
            TimeFrame.HOUR_13 -> addBarInList(780, bar)
            TimeFrame.HOUR_14 -> addBarInList(840, bar)
            TimeFrame.HOUR_15 -> addBarInList(900, bar)
            TimeFrame.HOUR_16 -> addBarInList(960, bar)
            TimeFrame.HOUR_17 -> addBarInList(1020, bar)
            TimeFrame.HOUR_18 -> addBarInList(1080, bar)
            TimeFrame.HOUR_19 -> addBarInList(1140, bar)
            TimeFrame.HOUR_20 -> addBarInList(1200, bar)
            TimeFrame.HOUR_21 -> addBarInList(1260, bar)
            TimeFrame.HOUR_22 -> addBarInList(1320, bar)
            TimeFrame.HOUR_23 -> addBarInList(1380, bar)
            TimeFrame.HOUR_24 -> addBarInList(1420, bar)
        }
    }

    private fun addBarInList(numberOfBars: Int, bar: Bar) {
        if (numberOfBars < statisticBarListPerTimeFrame.size) {
            if (timeFrame == TimeFrame.HOUR_12) {
                println()
            }
            statisticBarListPerTimeFrame.removeFirst()
            statisticBarListPerTimeFrame.add(bar)
            statisticAreComplete = true
            if (timeFrame == TimeFrame.HOUR_12) {
                println()
            }
        } else {
            statisticBarListPerTimeFrame.add(bar)
        }
        calculateAvgPrices()
        setHighLowPrices(bar)
    }

    private fun calculateAvgPrices() {
        setDefaultAvgPricesValues()
        calculateDifference()
        for (bar in statisticBarListPerTimeFrame) {
            if (bar.open < bar.close) greenBarsCounter++ else redBardCounter++
            avgOpenClosePrice += (bar.open + bar.close)
            avgOpenPricePerTimeFrame += bar.open
            avgClosePricePerTimeFrame += bar.close
            avgLowPricePerTimeFrame += bar.low
            avgHighPricePerTimeFrame += bar.high
            setHighLowPrices(bar)
        }
        avgOpenClosePrice /= statisticBarListPerTimeFrame.size * 2
        avgOpenPricePerTimeFrame /= statisticBarListPerTimeFrame.size
        avgClosePricePerTimeFrame /= statisticBarListPerTimeFrame.size
        avgLowPricePerTimeFrame /= statisticBarListPerTimeFrame.size
        avgHighPricePerTimeFrame /= statisticBarListPerTimeFrame.size
    }

    private fun setHighLowPrices(bar: Bar) {
        if (bar.high > highestPricePerTimeFrame) highestPricePerTimeFrame = bar.high
        if (bar.low < lowestPricePerTimeFrame || lowestPricePerTimeFrame == UNDEFINED_FLOAT_VALUE) {
            highestPricePerTimeFrame = bar.high
        }
    }

    private fun setDefaultAvgPricesValues() {
        highestPricePerTimeFrame = UNDEFINED_FLOAT_VALUE
        lowestPricePerTimeFrame = UNDEFINED_FLOAT_VALUE
        avgOpenClosePrice = UNDEFINED_DOUBLE_VALUE
        avgOpenPricePerTimeFrame = UNDEFINED_DOUBLE_VALUE
        avgClosePricePerTimeFrame = UNDEFINED_DOUBLE_VALUE
        avgLowPricePerTimeFrame = UNDEFINED_DOUBLE_VALUE
        avgHighPricePerTimeFrame = UNDEFINED_DOUBLE_VALUE
    }

    private fun calculateDifference() {
        val firstBarOpenPrice = statisticBarListPerTimeFrame.first().open / 100
        percentageDifference = statisticBarListPerTimeFrame.last().close / firstBarOpenPrice - 100
    }
}