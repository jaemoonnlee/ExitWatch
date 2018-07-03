package com.example.bon300_25.exitwatch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bon300_25.exitwatch.square.MyRetrofit;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    /*
     * SharedPreference
     */
    private SharedPreferences sharedPreferences;
    private boolean saveLoginData;
    private CheckBox checkBox;
    /*
     * 필드 선언
     */
    private static final String WIFI_STATE = "WIFI";
    public static final String MOBILE_STATE = "MOBILE";
    public static final String NONE_STATE = "NONE";
    public static final String CONNECTION_CONFIRM_CLIENT_URL = "http://clients3.google.com/generate_204";
    private boolean check_conn = false;
    private Button btnSign;
    private EditText id, password;
    private String mid, pw;


    /**
     * [MainActivity.class] 개요
     * 1.인터넷 연결 확인
     * 2.권한 관리(sdk version 23 이상인 경우)
     * 3.회원가입/로그인
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * 0.기존 설정값
         */
        sharedPreferences = getSharedPreferences("jaemoon", MODE_PRIVATE);
        load();


        /*
         * 0_1.FCM 런타임 호출
         * TODO: 무슨 목적으로 쓰는 건지 모르겠다.
         */
//        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        /*
         * 1.인터넷 연결 확인
         */
        switch (getWhatKindOfNetwork(this)) {
            case WIFI_STATE: // 와이파이 연결
                check_conn = isOnline();
                break;
            case MOBILE_STATE: // 모바일 데이터 연결
                Toast.makeText(this, "데이터 사용으로 인한 추가 요금이 발생할 수 있습니다.\n\n이 점 주의해주시기 바랍니다.", Toast.LENGTH_LONG);
                check_conn = isOnline();
                break;
            case NONE_STATE: // 연결 없음
                Toast.makeText(this, "인터넷을 연결해주세요", Toast.LENGTH_LONG);
                finish();
                break;
        }
        /*
         * 1_1.온/오프라인 확인
         */
        if(check_conn) {
            // 진행
            Toast.makeText(this, "온라인입니다.", Toast.LENGTH_LONG);
        } else {
            // 끄기
            Toast.makeText(this, "오프라인입니다. 서비스를 사용할 수 없습니다.", Toast.LENGTH_LONG);
            finish();
        }

        /*
         * 2.권한 관리: 안드로이드 마시멜로(M) 이상인 경우
         * 필요 권한: Camera 사용 Manifest.permission.CAMERA
         */
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("다음 기능에 대한 권한에 동의하지 않으시면 관련 서비스를 이용할 수 없습니다.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
        //If you reject permission, you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]

        /*
         * 3.회원가입/로그인
         */
        id = (EditText) findViewById(R.id.editText_id);
        password = (EditText) findViewById(R.id.editText_password);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        if(saveLoginData) {
            id.setText(mid);
            password.setText(pw);
            checkBox.setChecked(saveLoginData);
        }

        btnSign = (Button) findViewById(R.id.btnSign);
        btnSign.setOnClickListener(join);

    }// end of onCreate()

    /*
     * [TedPermission] 라이브러리
     * 런타임 퍼미션을 위한 리스너
     */
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    /*
     * 인터넷 연결 종류 확인 메소드
     */
    public static String getWhatKindOfNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return WIFI_STATE;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE_STATE;
            }
        }
        return NONE_STATE;
    }

    /*
     * 연결 상태 확인 메소드
     */
    public static boolean isOnline() {
        CheckConnect cc = new CheckConnect(CONNECTION_CONFIRM_CLIENT_URL);
        cc.start();
        try {
            cc.join();
            return cc.isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /*
     * 온라인 확인 객체(스레드)
     */
    private static class CheckConnect extends Thread {
        private boolean success;
        private String host;

        public CheckConnect(String host) {
            this.host = host;
        }

        /*
         * responseCode == 204: 인터넷 사용 권한 보유: online
         * responseCode == 200: 인터넷 사용 권한 미보유: offline
         */
        @Override
        public void run() {
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection)new URL(host).openConnection();
                conn.setRequestProperty("User-Agent", "Android");
                conn.setConnectTimeout(1000);
                conn.connect();
                int responseCode = conn.getResponseCode();
                if(responseCode == 204)
                    success = true;
                else
                    success = false;
            }
            catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        public boolean isSuccess() {
            return success;
        }
    }
    /*
     * 로그인 리스너
     */
    private View.OnClickListener join = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSign:
                    Call<Map<String, Object>> req = MyRetrofit.getInstance().getService().signin(id.getText().toString(), password.getText().toString());
                    req.enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                            int mno = -1;
                            String mid, pw, nickname, phone, email, city = null;
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());

                                JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                                mno = jsonObject1.getInt("mno");
                                mid = jsonObject1.getString("mid");
                                pw = jsonObject1.getString("pw");
                                nickname = jsonObject1.getString("nickname");
                                phone = "0"+jsonObject1.getString("phone");
                                email = jsonObject1.getString("email");
                                city = jsonObject1.getString("city");

                                if(mno != -1 && mid != null && pw != null && nickname != null && phone != null && email != null && city != null) {
                                    Log.d("객체 정보", mid + pw + nickname + phone + email + city);
                                    // 필요한 정보: mno(사진 저장), nickname(표시)
                                    save(mno, nickname);

                                    Intent ps = new Intent(getApplicationContext(), OptionActivity.class);
                                    // TODO: 플래그 설정 필요
//                                    ps.setFlags();
                                    startActivity(ps);
                                } else {
                                    Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 잘못됐습니다.", Toast.LENGTH_SHORT);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            Log.e("LOGIN ERROR", t.getMessage());
                        }
                    });

                    break;
            }
        }
    };

    private void save(int a, String b) {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
        editor.putString("ID", id.getText().toString().trim());
        editor.putString("PWD", password.getText().toString().trim());

        // 회원정보 저장
        editor.putInt("MNO", a);
        editor.putString("NICKNAME", b);

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = sharedPreferences.getBoolean("SAVE_LOGIN_DATA", false);
        mid = sharedPreferences.getString("ID", "");
        pw = sharedPreferences.getString("PWD", "");
    }
}