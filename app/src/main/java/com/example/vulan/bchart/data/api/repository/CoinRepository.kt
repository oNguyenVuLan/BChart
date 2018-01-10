package com.example.vulan.bchart.data.api.repository

import com.example.vulan.bchart.data.api.datasource.CoinListDataSource
import com.example.vulan.bchart.data.data.CoinData
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by FRAMGIA\nguyen.vu.lan on 1/10/18.
 */
@Singleton
class CoinRepository() : CoinListDataSource {
    private var dataSource: CoinListDataSource? = null

    @Inject
    constructor(dataSource: CoinListDataSource) : this() {
        this.dataSource = dataSource
    }

    override fun getCoinList(): Observable<CoinData> {
        return dataSource.getCoinList()
    }
}