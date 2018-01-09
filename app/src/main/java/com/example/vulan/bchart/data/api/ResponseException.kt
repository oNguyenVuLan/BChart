package com.example.vulan.bchart.data.api

import android.content.Context
import com.example.vulan.bchart.R
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by FRAMGIA\nguyen.vu.lan on 1/8/18.
 */
class ResponseException : RuntimeException {

    private var type: Type? = null
    private var errorResponse: ErrorResponse? = null
    private var response: Response<Any>? = null

    constructor(type: Type, errorResponse: ErrorResponse) {
        this.type = type
        this.errorResponse = errorResponse
    }

    constructor(type: Type, response: Response<Any>) {
        this.type = type
        this.response = response
    }

    constructor(type: Type, throwable: Throwable) {
        this.type = type
        this.errorResponse = errorResponse
    }

    override val message: String?
        get() = super.message

    fun getMessage(context: Context): String {
        when (type) {
            ResponseException.Type.SERVER -> {
                return if (errorResponse != null) {
                    errorResponse!!.message
                } else ""
            }
            ResponseException.Type.NETWORK -> return "network error"
            ResponseException.Type.HTTP -> {
                return if (response != null) {
                    getHttpErrorMessage(context, response!!.code())
                } else ""
            }
            ResponseException.Type.UNEXPECTED -> return super.message!!
            else -> return ""
        }
    }

    private fun getHttpErrorMessage(context: Context, httpCode: Int): String {
        return ""
    }
    private fun getNetworkErrorMessage(context: Context, throwable: Throwable): String {
        // TODO update later with Japanese
        if (throwable is SocketTimeoutException) {
            return context.getString(R.string.no_network_connection)
        }

        if (throwable is UnknownHostException) {
            return context.getString(R.string.no_network_connection)
        }

        return if (throwable is IOException) {
            context.getString(R.string.no_network_connection)
        } else context.getString(R.string.no_network_connection)
    }
    companion object {
        fun toHTTPError(response: Response<Any>):ResponseException{
            return ResponseException(Type.HTTP,response)
        }

        fun toNetworkError(throwable: Throwable):ResponseException{
            return ResponseException(Type.NETWORK,throwable)
        }

        fun toServerError(errorResponse: ErrorResponse):ResponseException{
            return ResponseException(Type.SERVER,errorResponse)
        }

        fun toUnexpectedError(throwable: Throwable):ResponseException{
            return ResponseException(Type.UNEXPECTED,throwable)
        }
    }
    enum class Type {
        /**
         * An [IOException] occurred while communicating to the server.
         */
        NETWORK,
        /**
         * A non-2xx HTTP status code was received from the server.
         */
        HTTP,
        /**
         * A error server with code & mMessage
         */
        SERVER,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    fun getErrorCode(): String? {
        when (type) {
            ResponseException.Type.SERVER -> if (errorResponse != null) {
                return errorResponse!!.code
            }
        }

        return null
    }


}