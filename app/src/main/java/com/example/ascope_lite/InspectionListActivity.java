package com.example.ascope_lite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ascope_lite.CamTest_v01.calculation.SizeCalculationCameraActivity;
import com.example.ascope_lite.CrackInspect.CrackInspectActivity;
import com.example.ascope_lite.Inspect_ViewList.Inspect_ViewListActivity;
import com.example.ascope_lite.Planlist.PlanResponse;
import com.example.ascope_lite.Project.ProjectGetListResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InspectionListActivity extends AppCompatActivity {
    ApiService service;
    TextView tv_facilityName, tv_address, tv_user_name;
    Button btn_reselect, btn_planningView, btn_01, btn_07, btn_08;

    ArrayList<PlanResponse> plan_list = new ArrayList<>();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 세로 고정
        setContentView(R.layout.activity_inspectionlist);
        SharedPreferences preferences = getSharedPreferences("tokenSave", Context.MODE_PRIVATE);
        int URL_flag = preferences.getInt("URL_flag", 0);
        String Token = "Bearer " + preferences.getString("token", "null");

        service = RetrofitClient.getClient(URL_flag).create(ApiService.class);

        //ViewListActivity 전달값
        Bundle bundle = getIntent().getExtras();
        ProjectGetListResponse project_data = (ProjectGetListResponse) bundle.getSerializable("project_data");

        service.get_planList(Token, String.valueOf(project_data.getId())).enqueue(new Callback<List<PlanResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<PlanResponse>> call, @NonNull Response<List<PlanResponse>> response) {
                List<PlanResponse> result = response.body();

                if (response.isSuccessful() && result != null) {
                    Log.e("get_planList: ", new Gson().toJson(result));
                    plan_list = (ArrayList<PlanResponse>) response.body();

                } else {
                    Toast.makeText(getApplicationContext(), "등록된 도면이 없습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("get_planList: ", "해당 도면 데이터 없음");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PlanResponse>> call, @NonNull Throwable t) {
                Log.e("get_planList: ", t.getMessage());
            }
        });

        String facilityname = project_data.getName();
        String facilityaddress = project_data.getAddress();
        String userName = project_data.getUserid();

        tv_facilityName = findViewById(R.id.tv_facilityName);
        tv_address = findViewById(R.id.tv_address);
        tv_user_name = findViewById(R.id.tv_user_name);

        tv_facilityName.setText(facilityname);
        tv_address.setText(facilityaddress);
        tv_user_name.setText(userName);

        btn_reselect = findViewById(R.id.btn_reselect);
        btn_planningView = findViewById(R.id.btn_planningView);
        btn_01 = findViewById(R.id.btn_01);
        btn_07 = findViewById(R.id.btn_07);
        btn_08 = findViewById(R.id.btn_08);

        //균열 및 외관조사
        btn_01.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CrackInspectActivity.class);
                Bundle crack_bundle = new Bundle();
                crack_bundle.putSerializable("crack_bundle", project_data);
                crack_bundle.putSerializable("plan_list", plan_list);
                intent.putExtras(crack_bundle);
                startActivity(intent);
//            finish();
            }
        });

        //조회
        btn_07.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Inspect_ViewListActivity.class);
                Bundle list_bundle = new Bundle();
                list_bundle.putSerializable("list_data", project_data);
                list_bundle.putSerializable("plan_list", plan_list);
                intent.putExtras(list_bundle);
                startActivity(intent);
//              finish();
            }
        });

        //사전 촬영
        btn_08.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SizeCalculationCameraActivity.class);
                Bundle crack_bundle = new Bundle();
                crack_bundle.putInt("type", 2);
                crack_bundle.putSerializable("project_data", project_data);
                crack_bundle.putSerializable("plan_list", plan_list);
                intent.putExtras(crack_bundle);
                startActivity(intent);
//              finish();
            }
        });

        //시설물 재선택
        btn_reselect.setOnClickListener(view -> {
            finish();
        });

        //도면 조회
        btn_planningView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PlanningViewActivity.class);
            Bundle planning_bundle = new Bundle();
            planning_bundle.putSerializable("planning_bundle", project_data);
            planning_bundle.putSerializable("plan_list", plan_list);
            intent.putExtras(planning_bundle);
            startActivity(intent);
//            finish();
        });
    }
}
