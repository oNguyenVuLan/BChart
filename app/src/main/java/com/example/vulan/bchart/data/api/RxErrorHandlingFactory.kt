package com.example.vulan.bchart.data.api

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.HttpException
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.schedulers.Schedulers
import java.io.IOException
import java.lang.reflect.Type

/**
 * Created by vulan on 1/2/2018.
 */
class RxErrorHandlingFactory : CallAdapter.Factory() {

    private var originalAdapterFactory: RxJavaCallAdapterFactory =
            RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io())

    override fun get(returnType: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?)
            : CallAdapter<*, *>? {
    }

    companion object {
        private class RxCallAdapterWrapper internal constructor(private val retrofit: Retrofit, private val wrapped: CallAdapter<*, *>) : CallAdapter<Observable<*, *>> {

            override fun responseType(): Type {
                return wrapped.responseType()
            }

            override fun <R> adapt(call: Call<R>): rx.Observable<*> {
                return (wrapped.adapt(call) as rx.Observable<*>)
                        .onErrorResumeNext(Func1<Throwable, rx.Observable<*>> { throwable ->
                            //throwable.printStackTrace();
                            rx.Observable.error<Any>(asRetrofitException(throwable))
                        })
            }

            private fun asRetrofitException(throwable: Throwable): RetrofitException {
                // We had non-200 http error
                if (throwable is HttpException) {
                    val response = throwable.response()
                    return RetrofitException.httpError(response.raw().request().url().toString(),
                            response, retrofit)
                }
                // A network error happened
                return if (throwable is IOException) {
                    RetrofitException.networkError(throwable)
                } else RetrofitException.unexpectedError(throwable)
                // We don't know what happened. We need to simply convert to an unknown error
            }
        }

        companion object {

            internal fun create(): CallAdapter.Factory {
                return RxErrorHandlingCallAdapterFactory()
            }
        }
    }

}