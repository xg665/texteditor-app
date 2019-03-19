package com.EulerityHackathon.texteditor.retrofit;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    public static Retrofit newInstance() {

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()                                       //Build a retrofit instance
                    .baseUrl("https://eulerity-hackathon.appspot.com/")                 //with adapter, converter and client
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getHttpClient())
                    .build();

        }
        return retrofit;
    }

    private static OkHttpClient getHttpClient() {                                   //Return a httpcilent instance

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new NewsInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
        return builder.build();
    }

    private static class NewsInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request original = chain.request();
            Request request = original
                    .newBuilder()
                    .build();

            return chain.proceed(request);
        }
    }
}
