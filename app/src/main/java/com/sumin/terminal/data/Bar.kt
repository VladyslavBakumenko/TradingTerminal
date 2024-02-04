package com.sumin.terminal.data

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName
import com.sumin.terminal.domain.utils.BuySell
import com.sumin.terminal.domain.utils.TradeInfo
import kotlinx.parcelize.Parcelize
import java.util.Calendar
import java.util.Date

@Parcelize
@Immutable
data class Bar(
    @SerializedName("o") val open: Float,
    @SerializedName("c") val close: Float,
    @SerializedName("l") val low: Float,
    @SerializedName("h") val high: Float,
    @SerializedName("t") val time: Long,
    var buySellInfo: Pair<BuySell, Float>?,
    var tradeInfoList: List<TradeInfo>? = null
) : Parcelable {

    val calendar: Calendar
        get() {
            return Calendar.getInstance().apply {
                time = Date(this@Bar.time)
            }
        }

    fun setBuiSellInfo(boySell: BuySell, openPositionPrise: Float): Bar {
        this.buySellInfo = Pair(boySell, openPositionPrise)
        return this
    }
}
