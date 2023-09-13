package com.sumin.terminal.data

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("results") val barList: List<Bar>
)
