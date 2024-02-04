package com.sumin.terminal.domain.statistics

import com.sumin.terminal.presentation.TimeFrame

fun createListFromOneToTwelveHourTimeFrames(): List<TimeFrame> {
    return mutableListOf<TimeFrame>().apply {
        add(TimeFrame.HOUR_1)
        add(TimeFrame.HOUR_2)
        add(TimeFrame.HOUR_3)
        add(TimeFrame.HOUR_4)
        add(TimeFrame.HOUR_5)
        add(TimeFrame.HOUR_6)
        add(TimeFrame.HOUR_7)
        add(TimeFrame.HOUR_8)
        add(TimeFrame.HOUR_9)
        add(TimeFrame.HOUR_10)
        add(TimeFrame.HOUR_11)
        add(TimeFrame.HOUR_12)
    }
}

fun createListFromFiveMinutesToOneHourTimeFrames(): MutableList<TimeFrame> {
    return mutableListOf<TimeFrame>().apply {
        add(TimeFrame.MIN_5)
        add(TimeFrame.MIN_10)
        add(TimeFrame.MIN_15)
        add(TimeFrame.MIN_20)
        add(TimeFrame.MIN_25)
        add(TimeFrame.MIN_30)
        add(TimeFrame.MIN_35)
        add(TimeFrame.MIN_40)
        add(TimeFrame.MIN_45)
        add(TimeFrame.MIN_50)
        add(TimeFrame.MIN_55)
        add(TimeFrame.HOUR_1)
    }
}