package com.example.vulan.bchart.data.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.Nullable;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created by FRAMGIA\nguyen.vu.lan on 1/8/18.
 */

public class RxHandling extends CallAdapter.Factory {

    private final RxJava2CallAdapterFactory original;

    private RxHandling() {
        original = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());
    }

    public static CallAdapter.Factory create(){
        return new RxHandling();
    }
    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        return null;
    }

    private static class RxCallAdapterWrapper<R> implements CallAdapter<R,Object>{

        private final CallAdapter<R,Object> wrapped;

        private RxCallAdapterWrapper(CallAdapter<R, Object> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public Type responseType() {
            return wrapped.responseType();
        }

        @Override
        public Object adapt(Call<R> call) {
            return ((Observable)wrapped.adapt(call)).onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                @Override
                public ObservableSource apply(Throwable throwable) throws Exception {
                    return Observable.error();
                }
            });
        }

        private ResponseException convertToResponseException(Throwable throwable){
            if(throwable instanceof ResponseException){
                return (ResponseException) throwable;
            }

            if(throwable instanceof IOException){
                try{
                    return ResponseException.
                }catch (IllegalStateException|OnErrorNotImplementedException e){

                }

            }
        }
    }
}
