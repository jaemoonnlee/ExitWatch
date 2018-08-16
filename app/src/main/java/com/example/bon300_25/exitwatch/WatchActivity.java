package com.example.bon300_25.exitwatch;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.bon300_25.exitwatch.firebase.RequestNotification;
import com.example.bon300_25.exitwatch.firebase.SendNotificationModel;
import com.example.bon300_25.exitwatch.square.DeviceRetrofit;
import com.example.bon300_25.exitwatch.square.FcmRetrofit;
import com.example.bon300_25.exitwatch.square.MyRetrofit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Double.isNaN;

public class WatchActivity extends AppCompatActivity implements SensorEventListener {
    // variables for camera
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;

    // variables for sensor
    private SensorEventListener sensorEventListener;
    private SensorManager sensorManager;
    private Sensor gyroscope;
    private static int gyro_sensor = Sensor.TYPE_GYROSCOPE;
    private static int DELAY = SensorManager.SENSOR_DELAY_NORMAL;

    private double pitch;
    private double roll;
    private double yaw;

    private double timestamp;
    private double dt;

    //    private double RAD2DGR = 180 / Math.PI;// never used
    private static final float NS2S = 1.0f/1000000000.0f;

    double tempX;
    double tempY;
    double tempZ;

    // variable for FCM
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        // FCM 토큰 저장
        int mno = getSharedPreferences("jaemoon", MODE_PRIVATE).getInt("MNO", -1);
        String mnoStr = "" + mno;
        Call<Map<String, Object>> req = MyRetrofit.getInstance().getService().loadToken(mnoStr);
        req.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                JSONObject jsonObject = new JSONObject(response.body());
                String to = null;
                try {
                    to = jsonObject.getString("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                token = to;
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) { }
        });

        // 센서
        sensorEventListener = this;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(gyro_sensor);
        sensorManager.registerListener(sensorEventListener, gyroscope, DELAY);

        // for camera
        surfaceView = (SurfaceView) findViewById(R.id.preview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camera = Camera.open(0);
                    if(camera == null)
                        camera = Camera.open(1);
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                try {
                    camera.stopPreview();
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                camera.stopPreview();
                camera.release();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == gyro_sensor) {
            String msg = "초기값 없음";

            /* 각 축의 각속도 성분을 받는다. */
            double gyroX = event.values[0];
            double gyroY = event.values[1];
            double gyroZ = event.values[2];

            /**
             * 현재값과 temp값 비교후 0.3 이상 차이가 나면
             * 1.사진 촬영
             * 2.DB 저장
             * 3.PUSH
             */
            if(isNaN(tempX) || isNaN(tempY) || isNaN(tempZ)) {
                // 비교값 초기값 설정
                tempX = gyroX;
                tempY = gyroY;
                tempZ = gyroZ;
            } else {
                if(calAbs(tempX, gyroX) >= 0.3 || calAbs(tempY, gyroY) >= 0.3 || calAbs(tempZ, gyroZ) >= 0.3) {
                    msg = "변화값\n[X]: " + String.format("%.4f", calAbs(tempX, gyroX))
                            + "\n[Y]: " + String.format("%.4f", calAbs(tempY, gyroY))
                            + "\n[Z]: " + String.format("%.4f", calAbs(tempZ, gyroZ));
                    Log.d("진동값 설정 기준 초월값 감지", msg);

                    // 1.사진촬영
                    camera.takePicture(null, null, mPictureCallback);
                    // 사진을 찍는 동안 센서 리스너 해제
                    sensorManager.unregisterListener(this);

                    // TODO: 3.PUSH to client who has same MNO
                    SendNotificationModel sendNotificationModel = new SendNotificationModel("감지기", "check your snapshot!");
                    RequestNotification requestNotification = new RequestNotification();
                    requestNotification.setSendNotificationModel(sendNotificationModel);
                    //token is id, whom you want to send notification
                    requestNotification.setToken(token);
                    Call<ResponseBody> req = FcmRetrofit.getInstance().getService().send(requestNotification);
                    req.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d("푸시 확인", response.body().toString());
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("푸시 실패", t.getMessage());
                        }
                    });
                }
            }
            /* 각속도를 적분하여 회전각을 추출하기 위해 적분 간격(dt)을 구한다.
             * dt : 센서가 현재 상태를 감지하는 시간 간격
             * NS2S : nano second -> second */
            dt = (event.timestamp - timestamp) * NS2S;
            timestamp = event.timestamp;

            /* 맨 센서 인식을 활성화 하여 처음 timestamp가 0일때는 dt값이 올바르지 않으므로 넘어간다. */
            if (dt - timestamp*NS2S != 0) {
                /* 각속도 성분을 적분 -> 회전각(pitch, roll)으로 변환.
                 * 여기까지의 pitch, roll의 단위는 '라디안'이다.
                 * SO 아래 로그 출력부분에서 멤버변수 'RAD2DGR'를 곱해주어 degree로 변환해줌.  */
                pitch = pitch + gyroY*dt;
                roll = roll + gyroX*dt;
                yaw = yaw + gyroZ*dt;

                // 비교용 저장
                tempX = event.values[0];
                tempY = event.values[1];
                tempZ = event.values[2];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // 카메라 촬영 리스너
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera1) {
            String str = Environment.getExternalStorageDirectory().getAbsolutePath();
            File pictureFileDir = new File(str + "/DCIM/SensingAndWarning");

            if (!pictureFileDir.exists()) {
                pictureFileDir.mkdirs();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            String date = dateFormat.format(new Date());
            String photoFile = "snapshot_" + date + ".jpg";
            String filename = pictureFileDir.getPath() + File.separator + photoFile;
            File mainPicture = new File(filename);

            try {
                FileOutputStream fos = new FileOutputStream(mainPicture);
                fos.write(data);
                fos.flush();
                fos.close();
                Log.d("success","image saved");
                camera1.startPreview();

                // 2.DB 저장
                int device_id = getSharedPreferences("jaemoon", MODE_PRIVATE).getInt("DEVICE_ID", -1);
                String device_idStr = "" + device_id;
                // retrofit + multipart
                RequestBody descPart = RequestBody.create(MultipartBody.FORM, device_idStr);
                RequestBody filePart = RequestBody.create(MediaType.parse("image/*"), mainPicture);
                MultipartBody.Part file = MultipartBody.Part.createFormData("photo", mainPicture.getName(), filePart);

                Call<ResponseBody> req = DeviceRetrofit.getInstance().getService().uploadSnapshot(descPart, file);
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("DB access","check DB");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) { }
                });
            } catch (Exception error) {
                Log.d("failure","Image could not be saved\n"+error.getMessage());
            } finally {
                sensorManager.registerListener(sensorEventListener, gyroscope, DELAY);
            }
        }
    };

    private double calAbs(double a, double b) {
        double result;
        if(a - b > 0)
            result = a - b;
        else
            result = b - a;
        return Math.abs(result);
    }

    @Override
    protected void onDestroy() {
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        sensorManager.unregisterListener(this);
        super.onBackPressed();
        finish();
    }
}