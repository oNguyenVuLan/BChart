package com.example.vulan.bchart.data.api

import com.example.vulan.bchart.data.data.CoinData
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

/**
 * Created by FRAMGIA\nguyen.vu.lan on 1/9/18.
 */
interface ApiService {
    //get list price
    @GET("api/prices")
    fun getListPrice(): Observable<CoinData>

    //get list price
    @GET("api/history")
    fun getHistory(): Observable<Response<*>>

}