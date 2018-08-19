package com.example.bon300_25.exitwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bon300_25.exitwatch.beans.Device;
import com.example.bon300_25.exitwatch.square.DeviceRetrofit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OptionActivity extends AppCompatActivity {

    private Spinner spinnerDevice;
    private Button newDevice, nextProcess;
    private Device[] devices;
    private String[] deviceNames;
    private int selectedDevice_id;
    private String selectedDevice_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        // 스피너
        spinnerDevice = (Spinner) findViewById(R.id.spinnerForDevice);
        int mno = getSharedPreferences("jaemoon", MODE_PRIVATE).getInt("MNO", -1);
        String mnoStr = "" + mno;
        Call<List<Device>> req =  DeviceRetrofit.getInstance().getService().showDevices(mnoStr);
        req.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                int result = response.body().size();
                if(result != 0) {
                    devices = new Device[result];
                    deviceNames = new String[result];

                    for(int i=0; i<result; i++) {
                        devices[i] = response.body().get(i);
                        deviceNames[i] = devices[i].getName();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, deviceNames);
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spinnerDevice.setAdapter(adapter);
                    spinnerDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedDevice_id = devices[position].getDevice_id();
                            selectedDevice_name = devices[position].getName();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedDevice_id = -1;
                        }
                    });
                } else {
                    String[] emptyString = new String[]{"장치를 신규 등록 하세요!"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, emptyString);
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spinnerDevice.setAdapter(adapter);
                    spinnerDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedDevice_id = -1;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedDevice_id = -1;
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {

            }
        });

        newDevice = (Button) findViewById(R.id.newDevice);
        newDevice.setOnClickListener(mlistener);
        nextProcess = (Button) findViewById(R.id.nextProcess);
        nextProcess.setOnClickListener(mlistener);
    }

    // 버튼 리스너
    private View.OnClickListener mlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.newDevice:
                    Intent rd = new Intent(getApplicationContext(), RegisterDeviceActivity.class);
                    startActivity(rd);
                    break;
                case R.id.nextProcess:
                    if(selectedCheck()) {
                        saveDeviceID(selectedDevice_id);
                        // TODO: 선택된 device_id로 감시 시작
                        Intent wa = new Intent(getApplicationContext(), WatchActivity.class);
                        wa.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(wa);
                    } else {
                        Toast.makeText(getApplicationContext(), "현재 장치 이름을 선택하세요.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private boolean selectedCheck() {
        if(selectedDevice_id == -1)
            return false;
        else
            return true;
    }

    private void saveDeviceID(int num) {
        SharedPreferences.Editor editor = getSharedPreferences("jaemoon", MODE_PRIVATE).edit();
        editor.putInt("DEVICE_ID", num);
        editor.putString("DEVICE_NAME", selectedDevice_name);
        editor.apply();
    }
}