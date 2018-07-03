package com.example.bon300_25.exitwatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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
    private ArrayList<String> mData = new ArrayList<>();
    private HashMap<String, Device> devices = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        // 값 불러오기
        loadDevice();

        // 스피너에 보이기
        spinnerDevice = (Spinner) findViewById(R.id.spinnerForDevice);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, mData);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerDevice.setAdapter(adapter);
        spinnerDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 선택된 device로 watch 실행
                String str1 = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        newDevice = (Button) findViewById(R.id.newDevice);
        newDevice.setOnClickListener(mlistener);
        nextProcess = (Button) findViewById(R.id.nextProcess);
        nextProcess.setOnClickListener(mlistener);
    }

    // DB통신 메소드
    private void loadDevice() {
        int mno = getSharedPreferences("jaemoon", MODE_PRIVATE).getInt("MNO", -1);
        String mnoStr = "" + mno;
        Call<List<Device>> req =  DeviceRetrofit.getInstance().getService().showDevices(mnoStr);
        req.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                List<Device> list = response.body();
                if(list.isEmpty()) {
                    mData.add("신규 등록 하세요!");
                }
                for(int i=0; i<list.size(); i++) {
                    Log.d("확인"+i, list.get(i).getName());
                    mData.add(list.get(i).getName());
                    devices.put(list.get(i).getName(), list.get(i));
                }
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {

            }
        });

    }

    // 버튼 리스너
    private View.OnClickListener mlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.newDevice:
                    // TODO: 신규등록 액티비티
                    break;
                case R.id.nextProcess:
                    // TODO: 감시 액티비티
                    break;
            }
        }
    };
}