package com.example.bon300_25.exitwatch.cookies;

import android.content.Context;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {
    private MySharedPreferences mySharedPreferences;

    public AddCookiesInterceptor(Context context) {
        mySharedPreferences = MySharedPreferences.getInstanceOf(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        HashSet<String> preferences = (HashSet) mySharedPreferences.getHashSet(MySharedPreferences.KEY_COOKIE, new HashSet<String>());

        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
        }

        // Web,Android,iOS 구분을 위해 User-Agent세팅
        builder.removeHeader("User-Agent").addHeader("User-Agent", "Android");

        return chain.proceed(builder.build());
    }
}
