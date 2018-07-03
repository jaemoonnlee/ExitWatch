package com.example.bon300_25.exitwatch.square;

import com.example.bon300_25.exitwatch.firebase.RequestNotification;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FcmRetrofitService {
    // Fcm send message
    @POST("fcm/send")
    @Headers({"Authorization: key=AAAAMpt5qo0:APA91bEtT5D9-NNDO0PV9qN4nFszGrwvp2UfJPUv22xKWdg611ntsLuu2Z4W0smTDOxG-FzxSNMzzugcg08di5SoAL2vqW5vrFPe1zSYdNvR59M6lJc-7LqtvthwnvoQui9XEzQeENBV",
            "Content-Type:application/json"})
    Call<ResponseBody> send(@Body RequestNotification requestNotification);
}
