package com.example.bon300_25.exitwatch.cookies;

import android.content.Context;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {
    private MySharedPreferences mySharedPreferences;
    public ReceivedCookiesInterceptor(Context context) {
        mySharedPreferences = MySharedPreferences.getInstanceOf(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }
            mySharedPreferences.putHashSet(MySharedPreferences.KEY_COOKIE, cookies);
        }
        return originalResponse;
    }
}
