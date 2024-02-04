package com.sumin.terminal.domain.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TradeInfo(
    val buySell: BuySell,
    val cryptocurrencyPrice: Double,
    val snapshotSequenceNumber: Int
): Parcelable