package com.example.vulan.bchart.data.api.datasource

import com.example.vulan.bchart.data.data.CoinData
import io.reactivex.Observable
/**
 * Created by FRAMGIA\nguyen.vu.lan on 1/10/18.
 */
interface CoinListDataSource {
    fun getCoinList():Observable<CoinData>
}