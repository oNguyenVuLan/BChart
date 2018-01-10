package com.example.vulan.bchart.data.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by FRAMGIA\nguyen.vu.lan on 1/10/18.
 */
class CoinData(@SerializedName("status") var status: Int,
               @SerializedName("currencies") var currency: Currency):Serializable