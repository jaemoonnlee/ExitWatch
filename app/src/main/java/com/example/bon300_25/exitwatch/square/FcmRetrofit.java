package com.example.bon300_25.exitwatch.square;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FcmRetrofit {
    /**
     * [HOW TO USE]
     * Call<?> request = MyRetrofit.getInstance().getService().??();
     * request.enqueue(new Callback<?>() {
     *     ...two override methods
     * });
     */
    // variables
    private final String TAG = "FcmRetrofit";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    FcmRetrofitService service = retrofit.create(FcmRetrofitService.class);

    // singleton pattern
    private FcmRetrofit() {
        Log.d(TAG, "singleton pattern instance");
    }
    private static FcmRetrofit instance = new FcmRetrofit();
    public static FcmRetrofit getInstance() {
        return instance;
    }

    // methods
    public FcmRetrofitService getService() {
        return service;
    }
}