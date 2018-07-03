package com.example.bon300_25.exitwatch.square;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeviceRetrofit {
    /**
     * [HOW TO USE]
     * Call<?> request = MyRetrofit.getInstance().getService().??();
     * request.enqueue(new Callback<?>() {
     *     ...two override methods
     * });
     */
    // variables
    private final String TAG = "DeviceRetrofit";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://52.78.222.250/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    DeviceRetrofitService service = retrofit.create(DeviceRetrofitService.class);

    // singleton pattern
    private DeviceRetrofit() {
        Log.d(TAG, "singleton pattern instance");
    }
    private static DeviceRetrofit instance = new DeviceRetrofit();
    public static DeviceRetrofit getInstance() {
        return instance;
    }

    // methods
    public DeviceRetrofitService getService() {
        return service;
    }
}