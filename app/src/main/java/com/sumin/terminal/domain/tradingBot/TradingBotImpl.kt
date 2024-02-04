package com.sumin.terminal.domain.tradingBot

import com.sumin.terminal.data.Bar
import com.sumin.terminal.data.UNDEFINED_DOUBLE_VALUE
import com.sumin.terminal.domain.statistics.PriceStatisticsRepository
import com.sumin.terminal.domain.utils.BuySell
import com.sumin.terminal.domain.utils.PurchaseData
import com.sumin.terminal.domain.utils.TradeInfo
import com.sumin.terminal.presentation.TimeFrame

class TradingBotImpl(private val barList: List<Bar>) {

    private val priceStatisticsRepository = PriceStatisticsRepository()

    private val reversedBarList = barList.reversed()
    private val resultList = reversedBarList.toMutableList()
    private var barCounter = 0
    private lateinit var currentProcessingBar: Bar

    private var capital = 100.0
    private var purchaseCapital = 100.0
    private var avgPurchasePrice = UNDEFINED_DOUBLE_VALUE
    private var onePercentOfInitialCapital = capital / 100
    private var openClosePositionCounter = 0
    private var positionMustBeClosePrice = UNDEFINED_DOUBLE_VALUE
    private var potentialProfitIfCloseAllPositionNow = UNDEFINED_DOUBLE_VALUE
    private var amountOfCryptocurrencyPurchased = 0.0

    private val listOfOpenPosition = mutableListOf<PurchaseData>()
    private var listOfPurchasePrices = mutableListOf<PurchaseData>()
    private var listOfOpenPositionIsFilled = false
    private var listOfPurchasePricesIsFilled = false

    private var tradeInfoList = mutableListOf<TradeInfo>()
    private var positionMustBeOpenPrice = 0.0

    private var snapshotSerialNumber = 0
    private var currentTestPurchaseData = PurchaseData(0.0, 0.0)

//    якшо росте то не закриваєм позицію поки ціна не впаде на 30 відсотків

    fun tradeReceivedBars(): List<Bar> {
        priceStatisticsRepository.initPriceStatisticsPerTimeListList()
        reversedBarList.forEach { bar ->
            currentProcessingBar = bar
            processBar(bar)
        }
        return resultList.reversed()
    }

    private fun processBar(bar: Bar) {
        priceStatisticsRepository.addBarToStatistics(bar)
        tradeBar(bar)
        barCounter++
    }

    private fun tradeBar(bar: Bar) {
        val priceStatistics12Hour =
            priceStatisticsRepository.getAllStatisticsPerTimeFrame(TimeFrame.HOUR_12)
        val priceStatistics1Hour =
            priceStatisticsRepository.getAllStatisticsPerTimeFrame(TimeFrame.HOUR_1)
        val trueStat =
            if (openClosePositionCounter == 0) priceStatistics12Hour else priceStatistics1Hour

        if (listOfOpenPosition.isNotEmpty()) tryClosePositionAndSetDefaultValues(bar)
        if (listOfOpenPosition.isEmpty() && trueStat.isStatisticAreComplete()) {
            if (listOfPurchasePricesIsFilled) tryOpenPositionsInCurrentBar(bar)
            else fillListOfPurchasePrices(trueStat.getAvgOpenClosePrice())
        }
    }

    private fun tryClosePositionAndSetDefaultValues(bar: Bar) {
        if (bar.high >= positionMustBeClosePrice) {
            makeSecondSnapshot(bar)
            calculateCapital(positionMustBeClosePrice)
            listOfOpenPosition.clear()
            listOfPurchasePrices.clear()
            listOfOpenPositionIsFilled = false
            listOfPurchasePricesIsFilled = false
            positionMustBeClosePrice = 0.0
            tradeInfoList.clear()
            positionMustBeOpenPrice = 0.0
            onePercentOfInitialCapital = capital / 100
            openClosePositionCounter++
        }
    }

    private fun fillListOfPurchasePrices(avgOpenClosePosition: Double) {
        listOfPurchasePrices.clear()
        positionMustBeClosePrice = (avgOpenClosePosition / 100) * 101
        val quantity = onePercentOfInitialCapital * 20
        val makerCommission = quantity / 1000 * 1
        val quantityAfterMakerCommissions = quantity - makerCommission
        while (quantityAfterMakerCommissions < purchaseCapital) {
            val lastPrice = if (listOfPurchasePrices.isEmpty()) {
                avgOpenClosePosition / 100 * 99
            } else {
                avgOpenClosePosition / 100 * (100 - (listOfPurchasePrices.size + 1))
            }
            addPurchaseDataInList(lastPrice, quantityAfterMakerCommissions)
            purchaseCapital -= quantity
        }
        listOfPurchasePricesIsFilled = true
        positionMustBeOpenPrice = listOfPurchasePrices.first().purchaseCost
        currentTestPurchaseData = listOfPurchasePrices.first()
    }

    private var tryOpenPositionNotSuccessCounter = 0

    private fun tryOpenPositionsInCurrentBar(bar: Bar) {
        val tradeInfoList = mutableListOf<TradeInfo>()
        if (listOfPurchasePrices.isNotEmpty()) {
            println()
            if (listOfPurchasePrices.first().purchaseCost > bar.low) {
                println()
            }
        }
        if (listOfPurchasePrices.isNotEmpty() && listOfPurchasePrices.first().purchaseCost > bar.low) {
            println()
        }

        if (!listOfOpenPositionIsFilled) {
            while (checkIsCurrentBarCorrectForOpenPositionAndProcessTryOpenPositionNotSuccessCounter(
                    bar
                )
                && !listOfOpenPositionIsFilled
            ) {
                tradeInfoList.add(
                    TradeInfo(
                        buySell = BuySell.Buy,
                        positionMustBeOpenPrice,
                        snapshotSerialNumber
                    )
                )
                addPositionToListOfOpenPosition(PurchaseData(positionMustBeOpenPrice, 19.98))
                setAmountOfCryptocurrencyPurchased()
                try {
                    positionMustBeOpenPrice =
                        listOfPurchasePrices[listOfOpenPosition.size].purchaseCost
                } catch (e: Exception) {
                }
                if (listOfOpenPosition.size == listOfPurchasePrices.size) {
                    listOfOpenPositionIsFilled = true
                }
            }
        }
        addBarToSnapshot(bar.copy(tradeInfoList = tradeInfoList.toList()))
    }

    private val testList = mutableListOf<Int>()

    private fun checkIsCurrentBarCorrectForOpenPositionAndProcessTryOpenPositionNotSuccessCounter(
        bar: Bar
    ): Boolean {
        return if (bar.low <= positionMustBeOpenPrice) {
            testList.add(tryOpenPositionNotSuccessCounter)
            tryOpenPositionNotSuccessCounter = 0
            true
        } else {
            tryOpenPositionNotSuccessCounter++
            false
        }
    }

    private fun addBarToSnapshot(bar: Bar) {
        if (bar.tradeInfoList?.isNotEmpty() == true) {
            updateReversedList(bar)
        }
    }

    private fun addPurchaseDataInList(purchaseCost: Double, quantityAfterMakerCommission: Double) {
        listOfPurchasePrices.add(
            PurchaseData(
                purchaseCost,
                quantityAfterMakerCommission
            )
        )
    }

    private fun calculateCapital(sellPrise: Double) {
        val takerCommission = amountOfCryptocurrencyPurchased * sellPrise / 1000
        capital += amountOfCryptocurrencyPurchased * sellPrise - takerCommission
        purchaseCapital = capital
    }

    private fun setAmountOfCryptocurrencyPurchased() {
        amountOfCryptocurrencyPurchased = 0.0
        for (i in listOfOpenPosition) {
            this.amountOfCryptocurrencyPurchased += (i.quantity / i.purchaseCost)
        }
    }

    private fun addPositionToListOfOpenPosition(purchaseData: PurchaseData) {
        listOfOpenPosition.add(purchaseData)
        avgPurchasePrice = listOfOpenPosition.map { it.purchaseCost }.average()
        capital -= purchaseData.quantity
    }

    private fun calculateTest(bar: Bar) {
        potentialProfitIfCloseAllPositionNow = amountOfCryptocurrencyPurchased * ((bar.low + bar.high) / 2)
    }

    private fun makeSecondSnapshot(sellBar: Bar) {
        val sellBarWithTradingInfo = sellBar.copy(
            tradeInfoList = listOf(
                TradeInfo(
                    BuySell.Sell,
                    positionMustBeClosePrice,
                    snapshotSerialNumber
                )
            )
        )
        calculateTest(sellBar)
        updateReversedList(sellBarWithTradingInfo)
        snapshotSerialNumber++
    }

    private fun updateReversedList(newBar: Bar) {
        resultList.removeAt(barCounter)
        resultList.add(barCounter, newBar)
    }

    private fun setDefaultValues() {
        listOfPurchasePrices.clear()
        listOfPurchasePricesIsFilled = false
        positionMustBeOpenPrice = 0.0
        purchaseCapital = capital
        tryOpenPositionNotSuccessCounter = 0
    }
}