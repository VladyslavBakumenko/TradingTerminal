package com.sumin.terminal.data

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    //def 2023-11-01/2024-01-13
    //test 2023-11-01/2024-11-10
    @GET("aggs/ticker/X:SOLUSD/range/{timeframe}/2023-12-01/2023-12-02?adjusted=true&sort=desc&limit=50000&apiKey=1X31VsWmKXosLQ8h9SLYzw7tEcOxJEPt")
    suspend fun loadBars(
        @Path("timeframe") timeFrame: String
    ): Result
}
