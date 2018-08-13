package com.example.bon300_25.exitwatch.square;

import com.example.bon300_25.exitwatch.beans.Building;
import com.example.bon300_25.exitwatch.beans.Device;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface DeviceRetrofitService {
    // show buildings
    @GET("sawQuery/building/load")
    Call<List<Building>> showBuildings();

    // show device_ids
    @POST("sawQuery/showDevices")
    Call<List<Device>> showDevices(@Query("mnoStr") String mnoStr);

    // register Device
    @GET("sawQuery/registerDevice")
    Call<Integer> registerDevice(@Query("d") Device device);

    // TODO: device update

    // 스냅샷 불러오기
    @GET("sawQuery/loadSnapshot")
    Call<Map<String, Object>> loadSnapshot(@Query("mnoStr") String mnoStr);

    // 사진 DB 저장 Multipart 이용
    // @Headers("Content-Type: application/json")
    @Multipart
    @POST("sawQuery/uploadSnapshot")
    Call<ResponseBody> uploadSnapshot(
            @Part("device_id") RequestBody device_idStr,
            @Part MultipartBody.Part file
    );
}
