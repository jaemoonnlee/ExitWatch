package com.example.bon300_25.exitwatch.square;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyRetrofit {
    /**
     * [HOW TO USE]
     * Call<?> request = MyRetrofit.getInstance().getService().??();
     * request.enqueue(new Callback<?>() {
     *     ...two override methods
     * });
     */
    // variables
    private final String TAG = "MyRetrofit";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://52.78.222.250/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    MyRetrofitService service = retrofit.create(MyRetrofitService.class);

    // singleton pattern
    private MyRetrofit() {
        Log.d(TAG, "singleton pattern instance");
    }
    private static MyRetrofit instance = new MyRetrofit();
    public static MyRetrofit getInstance() {
        return instance;
    }

    // methods
    public MyRetrofitService getService() {
        return service;
    }
}