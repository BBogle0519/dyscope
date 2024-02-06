package com.example.ascope_lite.CrackInspect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ascope_lite.ApiService;
import com.example.ascope_lite.CamTest_v01.calculation.SizeCalculationCameraActivity;
import com.example.ascope_lite.CameraActivity;
import com.example.ascope_lite.OnSingleClickListener;
import com.example.ascope_lite.Plan_Adapter;
import com.example.ascope_lite.Planlist.PlanResponse;
import com.example.ascope_lite.Project.ProjectGetListResponse;
import com.example.ascope_lite.R;
import com.example.ascope_lite.ResponseMessage;
import com.example.ascope_lite.RetrofitClient;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SourceLockedOrientationActivity")
public class CrackInspectActivity extends AppCompatActivity {
    ApiService service;
    Context context = this;

    TextView tv_inspectDate, tv_facilityName, tv_address, tv_user_name;
    RadioGroup rg_crack;
    Spinner sp_crackKind, sp_planList_name;
    Button btn_reselect, btn_ok, btn_end;
    int type;

    ArrayList<PlanResponse> plan_list = new ArrayList<>();
    Plan_Adapter plan_adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 세로 고정
        setContentView(R.layout.activity_crackinspect);

        SharedPreferences preferences = getSharedPreferences("tokenSave", MODE_PRIVATE);
        String Token = "Bearer " + preferences.getString("token", "null");
        int URL_flag = preferences.getInt("URL_flag", 0);

        service = RetrofitClient.getClient(URL_flag).create(ApiService.class);

        //넘길 데이터
        CrackData result_data = new CrackData();

        Bundle bundle = getIntent().getExtras();
        ProjectGetListResponse data = (ProjectGetListResponse) bundle.getSerializable("crack_bundle");
        type = bundle.getInt("update", 0);

        plan_list = (ArrayList<PlanResponse>) bundle.getSerializable("plan_list");

        tv_inspectDate = findViewById(R.id.tv_inspectDate);
        tv_facilityName = findViewById(R.id.tv_facilityName);
        tv_address = findViewById(R.id.tv_address);
        tv_user_name = findViewById(R.id.tv_user_name);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String date_st = format.format(date);

        tv_inspectDate.setText(date_st);
        tv_facilityName.setText(data.getName());
        tv_address.setText(data.getAddress());
        tv_user_name.setText(data.getUserid());

        rg_crack = findViewById(R.id.rg_crack);
        btn_reselect = findViewById(R.id.btn_reselect);
        btn_ok = findViewById(R.id.btn_ok); //조사 시작
        btn_end = findViewById(R.id.btn_end); //조사 종료

        //도면 불러오기
        sp_planList_name = findViewById(R.id.sp_planList_name);

        plan_adapter = new Plan_Adapter(CrackInspectActivity.this, plan_list);
        sp_planList_name.setAdapter(plan_adapter);

        final String[] plan_id = new String[1];
        sp_planList_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                plan_id[0] = String.valueOf(adapterView.getAdapter().getItem(i));
                Log.e("dialog_spinner", String.valueOf(adapterView.getAdapter().getItem(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                plan_id[0] = String.valueOf(adapterView.getAdapter().getItem(0));
                Log.e("dialog_spinner", String.valueOf(adapterView.getAdapter().getItem(0)));
            }
        });


        if (type == 1) {
            result_data.setCoordinateid(data.getCoordinateid());
            result_data.setCrackid(data.getCrackid());
            btn_ok.setText("수정 시작");
            btn_reselect.setVisibility(View.INVISIBLE);
            btn_end.setText("수정 취소");
        }

        //측정위치 rg
        final String[] zone = new String[1];
        zone[0] = "내부";
        rg_crack.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_crack01:
                    zone[0] = "내부";
                    break;

                case R.id.rb_crack02:
                    zone[0] = "외부";
                    break;
            }
        });

        //측정 타입 spinner
        sp_crackKind = findViewById(R.id.sp_crackKind);

        sp_crackKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                result_data.setType(String.valueOf(adapterView.getAdapter().getItem(i)));
                result_data.setSub_type(i);
//                Log.e("onItemSelected1", String.valueOf(i));
//                Log.e("onItemSelected1", String.valueOf(adapterView.getAdapter().getItem(i)));
//                Log.e("onItemSelected2", String.valueOf(adapterView.getItemAtPosition(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                result_data.setType(String.valueOf(adapterView.getAdapter().getItem(0)));
//                Toast.makeText(getApplicationContext(), "onNothingSelected", Toast.LENGTH_SHORT).show();
            }
        });

        //조사 재선택 btn
        btn_reselect.setOnClickListener(view -> {
            finish();
        });

        //확인
        btn_ok.setOnClickListener(view -> {
            //input data
            result_data.setProjectid(String.valueOf(data.getId()));
            result_data.setZone(zone[0]);

            if (result_data.getType().equals("균열")) {//자동
                result_data.setAuto_analyze("1");
                Intent intent = new Intent(getApplicationContext(), SizeCalculationCameraActivity.class);
                Bundle crackInspect_bundle = new Bundle();
                crackInspect_bundle.putSerializable("crackInspect_bundle", result_data);
                crackInspect_bundle.putSerializable("plan_list", plan_list);
                crackInspect_bundle.putString("plan_id", plan_id[0]);
                crackInspect_bundle.putInt("type", type);
                intent.putExtras(crackInspect_bundle);
                startActivity(intent);
            } else {//수동
                result_data.setAuto_analyze("0");
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                Bundle crackInspect_bundle = new Bundle();
                crackInspect_bundle.putSerializable("crackInspect_bundle", result_data);
                crackInspect_bundle.putSerializable("plan_list", plan_list);
                crackInspect_bundle.putString("plan_id", plan_id[0]);
                crackInspect_bundle.putInt("type", type);
                crackInspect_bundle.putInt("menu", 1);
                intent.putExtras(crackInspect_bundle);
                startActivity(intent);
            }
        });

        //조사 종료
        if (type == 1) {
            btn_end.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    finish();
                }
            });
        } else {
            btn_end.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    service.get_inspection_end(Token, String.valueOf(data.getId())).enqueue(new Callback<ResponseMessage>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseMessage> call, @NonNull Response<ResponseMessage> response) {
                            ResponseMessage result = response.body();

                            if (result != null) {
                                Log.e("onResponse: ", new Gson().toJson(result));
                                Toast.makeText(getApplicationContext(), "종료 처리 되었습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "통신 오류: 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                                Log.e("2onResponse:", "result:: null");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseMessage> call, @NonNull Throwable t) {
                            Log.e("onFailure", t.getMessage());
                        }
                    });
                }
            });
        }
    }
}


