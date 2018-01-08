package com.example.vulan.bchart.data.api

/**
 * Created by FRAMGIA\nguyen.vu.lan on 1/8/18.
 */
class ResponseException:RuntimeException() {

    private var type: Type? = null


    enum class Type{
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
                return errorResponse!!.getCode()
            }
        }

        return null
    }
}