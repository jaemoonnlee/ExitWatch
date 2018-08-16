package com.example.bon300_25.exitwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bon300_25.exitwatch.beans.Building;
import com.example.bon300_25.exitwatch.square.DeviceRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterDeviceActivity extends AppCompatActivity {

    private Spinner spinnerBid;
    private EditText editText_name, editText_desc;
    private String str_name, str_desc;
    private Button register_btn;
    private int selectedBid;
    private Building[] buildings;
    private String[] buildingNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_device);

        // 장치 이름
        editText_name = (EditText) findViewById(R.id.editText_device_name);
        // 장치 설명
        // ex) xx건물 o층 ww장소
        editText_desc = (EditText) findViewById(R.id.editText_device_desc);
        // 건물 선택 스피너(옵션)
        spinnerBid = (Spinner) findViewById(R.id.spinnerForBid);

        // 등록 버튼
        register_btn = (Button) findViewById(R.id.register_btn);
        register_btn.setOnClickListener(listener);

        // buildings 불러오기
        Call<List<Building>> req = DeviceRetrofit.getInstance().getService().showBuildings();
        req.enqueue(new Callback<List<Building>>() {
            @Override
            public void onResponse(Call<List<Building>> call, Response<List<Building>> response) {
                int result = response.body().size();
                if(result != 0) {
                    buildings = new Building[result];
                    buildingNames = new String[result];

                    for(int i=0; i<result; i++) {
                        buildings[i] = response.body().get(i);
                        buildingNames[i] = buildings[i].getBname();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, buildingNames);
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spinnerBid.setAdapter(adapter);
                    spinnerBid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selectedBid = (Integer) parent.getItemAtPosition(position);
                            selectedBid = buildings[position].getBid();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedBid = -1;
                        }
                    });
                } else {
                    String[] emptyString = new String[]{"선택할 건물이 없습니다."};
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, emptyString);
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spinnerBid.setAdapter(adapter);
                    spinnerBid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedBid = -1;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedBid = -1;
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Building>> call, Throwable t) {

            }
        });
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            str_name = editText_name.getText().toString();
            str_desc = editText_desc.getText().toString();
            if(checkBlank())
                return;
            int mno = getSharedPreferences("jaemoon", MODE_PRIVATE).getInt("MNO", -1);
            Call<Integer> req2 = DeviceRetrofit.getInstance().getService().registerDevice(mno, selectedBid, str_name, str_desc);
            req2.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    switch (response.body().intValue()) {
                        case 1:// 등록 성공(장치 선택으로 돌아감)
                            Intent oa = new Intent(getApplicationContext(), OptionActivity.class);
                            oa.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(oa);
                            break;
                        case 0:// 등록 실패
                            break;
                        case -1:// DB 오류
                            break;
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });
        }
    };

    private boolean checkBlank() {
        if(str_name.length()==0) {
            Toast.makeText(this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            editText_name.requestFocus();
            return true;
        }
        if(str_desc.length()==0) {
            Toast.makeText(this, "설명을 써주세요.", Toast.LENGTH_SHORT).show();
            editText_desc.requestFocus();
            return true;
        }
        return false;
    }
}