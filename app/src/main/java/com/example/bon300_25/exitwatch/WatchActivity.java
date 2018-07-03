package com.example.bon300_25.exitwatch;

import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.bon300_25.exitwatch.square.MyRetrofit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
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

public class WatchActivity extends AppCompatActivity implements SensorEventListener {
    // variables for camera
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;

    // variable for FCM
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        // FCM 토큰 저장
        int mno = getSharedPreferences("appData", MODE_PRIVATE).getInt("MNO", -1);
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
                int mno = getSharedPreferences("appData", MODE_PRIVATE).getInt("MNO", -1);
                String mnoStr = "" + mno;
                // retrofit + multipart
                RequestBody descPart = RequestBody.create(MultipartBody.FORM, mnoStr);
                RequestBody filePart = RequestBody.create(MediaType.parse("image/*"), mainPicture);
                MultipartBody.Part file = MultipartBody.Part.createFormData("photo", mainPicture.getName(), filePart);


                Call<ResponseBody> req = MyRetrofit.getInstance().getService().postImage(descPart, file);
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("DB access","check DB");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            } catch (Exception error) {
                Log.d("failure","Image could not be saved\n"+error.getMessage());
            } finally {
                // TODO: 센서 리스너 재등록
//                sensorManager.registerListener(sensorEventListener, gyroscope, DELAY);
            }
        }
    };
}