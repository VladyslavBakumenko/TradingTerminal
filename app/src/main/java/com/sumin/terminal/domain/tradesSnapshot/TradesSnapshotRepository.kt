package com.sumin.terminal.domain.tradesSnapshot

import com.sumin.terminal.data.Bar

class TradesSnapshotRepository {

    private val tradesSnapshotImpl = TradesSnapshotImpl.getInstance()


    fun addSnapshotInList(snapshot: List<Bar>) {
        tradesSnapshotImpl.getTradeSnapshotList().add(snapshot)
    }

    fun getSnapshotList() = tradesSnapshotImpl.getTradeSnapshotList()

    companion object {
        private var instance: TradesSnapshotRepository? = null

        fun getInstance(): TradesSnapshotRepository {
            if (instance == null) {
                instance = TradesSnapshotRepository()
            }
            return instance!!
        }
    }
}