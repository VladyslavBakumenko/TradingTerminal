package com.sumin.terminal.domain.tradesSnapshot

import com.sumin.terminal.data.Bar

class TradesSnapshotImpl {

    private val tradeSnapshotList = mutableListOf<List<Bar>>()

    fun getTradeSnapshotList() = tradeSnapshotList

    companion object {
        private var instance: TradesSnapshotImpl? = null

        fun getInstance(): TradesSnapshotImpl {
            if (instance == null) {
                instance = TradesSnapshotImpl()
            }
            return instance!!
        }
    }
}