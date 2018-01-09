package com.example.vulan.bchart.data.api;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by FRAMGIA\nguyen.vu.lan on 1/8/18.
 */

public class RxErrorHandlerFactory extends CallAdapter.Factory {

    private final RxJava2CallAdapterFactory original;

    private RxErrorHandlerFactory() {
        original = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());
    }

    public static CallAdapter.Factory create() {
        return new RxErrorHandlerFactory();
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        return new RxCallAdapterWrapper<>(original.get(returnType, annotations, retrofit));
    }

    private static class RxCallAdapterWrapper<R> implements CallAdapter<R, Object> {

        private final CallAdapter<R, Object> wrapped;

        private RxCallAdapterWrapper(CallAdapter<R, Object> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public Type responseType() {
            return wrapped.responseType();
        }

        @Override
        public Object adapt(Call<R> call) {
            return ((Observable) wrapped.adapt(call)).onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                @Override
                public ObservableSource apply(Throwable throwable) throws Exception {
                    return Observable.error(convertToResponseException(throwable));
                }
            });
        }

        private ResponseException convertToResponseException(Throwable throwable) {
            if (throwable instanceof ResponseException) {
                return (ResponseException) throwable;
            }

            if (throwable instanceof IOException) {
                try {
                    return ResponseException.Companion.toNetworkError(throwable);
                } catch (IllegalStateException | OnErrorNotImplementedException e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage());
                }
            }
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                Response response = httpException.response();
                if (response.errorBody() == null) {
                    return ResponseException.Companion.toHTTPError(response);
                }
                try{
                 String errorData=response.errorBody().string();
                 ErrorResponse errorResponse=deserializeErrorBody(errorData);
                 if(errorResponse!=null&& TextUtils.isEmpty(errorResponse.getCode())){
                     return ResponseException.Companion.toServerError(errorResponse);
                 }else{
                     return ResponseException.Companion.toHTTPError(response);
                 }
                }catch (IOException e){
                    Log.e(this.getClass().getSimpleName(), e.getMessage());
                }
            }
            return  ResponseException.Companion.toUnexpectedError(throwable);
        }

        private ErrorResponse deserializeErrorBody(String errorString){
            Gson gson=new Gson();
            try{
                return gson.fromJson(errorString,ErrorResponse.class);
            }catch (JsonSyntaxException e){
                Log.e("",e.getMessage());
                return null;
            }
        }
    }
}
