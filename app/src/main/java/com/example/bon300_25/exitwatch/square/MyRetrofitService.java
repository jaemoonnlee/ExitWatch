package com.example.bon300_25.exitwatch.square;

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

public interface MyRetrofitService {
    /*
     * 요청 URL 메소드: @GET(), @POST(), @PUT(), @DELETE(), @HEAD()
     * 요청 URL 메소드의 동적 부분 치환
        ex1)@요청URL메소드(app/{param}/age)
            Call<T:반환값> 메소드이름(@Path("param") 타입값 변수명);
        ex2)@요청URL메소드(app/age)
            Call<int> 메소드이름(@Query("param") String abc);
            -> URL/app/age?param=abc 로 요청
     * Call<T>는 Callback Interface이다.
     */

    // 토큰 값 보내기
    @GET("sawQuery/registToken")
    Call<Integer> registToken(@Query("mno") int mno, @Query("token") String token);

    // 토큰 불러오기
    @GET("sawQuery/loadToken")
    Call<Map<String, Object>> loadToken(@Query("mnoStr") String mnoStr);

    // 로그인
    @GET("sawQuery/signin")
    Call<Map<String, Object>> signin(@Query("mid") String mid, @Query("pw") String pw);

    /* ///////////////////////////////////////////////////////////////////////////여기서 */
    // 스냅샷 불러오기
    @GET("sawQuery/loadPic")
    Call<Map<String, Object>> loadPic(@Query("mnoStr") String mnoStr);

    // 사진 DB 저장 Multipart 이용
    // @Headers("Content-Type: application/json")
    @Multipart
    @POST("sawQuery/uploadPic")
    Call<ResponseBody> postImage(
            @Part("mno") RequestBody mno,
            @Part MultipartBody.Part file
    );

    // 회원가입
    @GET("sawQuery/register")
    Call<Integer> register(@Query("mid") String mid, @Query("pw") String pw,
                           @Query("nickname") String nickname, @Query("phone") String phone,
                           @Query("email") String email, @Query("city") String city);
    /* 여기까지//////////////////////////////////////////////////////////////// 삭제 예정 */
}