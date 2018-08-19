/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bon300_25.exitwatch.firebase;

import android.util.Log;

import com.example.bon300_25.exitwatch.cookies.MySharedPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        /*
        if (isValidString(refreshedToken)) { //토큰이 널이거나 빈 문자열이 아닌 경우
            if (!isValidString(getSharedPreferencesStringData(getApplicationContext(), AD_FCM_TOKEN))) { //토큰에 데이터가 없는 경우에만 저장
                setSharedPreferencesStringData(getApplicationContext(), AD_FCM_TOKEN, refreshedToken);
            }

            if (isValidString(getSharedPreferencesStringData(getApplicationContext(), AD_LOGIN_ID))) { //로그인 상태일 경우에는 서버로 보낸다.
                if (!refreshedToken.equals(getSharedPreferencesStringData(getApplicationContext(), AD_FCM_TOKEN))) { //기존에 저장된 토큰과 비교하여 다를 경우에만 서버 업데이트
                    setSharedPreferencesStringData(getApplicationContext(), AD_FCM_TOKEN, refreshedToken);
                    sendRegistrationToServer(refreshedToken);
                }
            }
        }
        */

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // 생성된 토큰을 자신의 서버로 보내어 저장하거나 추가 작업을 할 수 있도록 한다.
        // TODO: 사용자ID와 토큰 값을 같이 DB에 저장하기
//        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: 서버 통신 - 서버로 token 값을 보내어 저장 등 추가 작업을 위한 코드
        // OkHttp 혹은 Retrofit 사용을 권장
        // 쿠키에서 mno 값 가져오기
        MySharedPreferences preferences = MySharedPreferences.getInstanceOf(this);
        HashSet<String> blank = new HashSet<>();
        HashSet<String> cookies = preferences.getHashSet(MySharedPreferences.KEY_COOKIE, blank);


        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                /*.add("mno", cookie)*/
                .add("token", token)
                .build();

        // 요청
        Request request = new Request.Builder()
                .url("http://52.78.222.250/sawQuery/registToken")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}