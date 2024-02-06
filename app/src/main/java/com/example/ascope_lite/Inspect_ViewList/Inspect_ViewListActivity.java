package com.example.ascope_lite.Inspect_ViewList;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ascope_lite.ApiService;
import com.example.ascope_lite.CrackInspect.CrackListResponse;
import com.example.ascope_lite.Plan_Adapter;
import com.example.ascope_lite.Planlist.PlanCoordinateActivity;
import com.example.ascope_lite.Planlist.PlanResponse;
import com.example.ascope_lite.Project.ProjectGetListResponse;
import com.example.ascope_lite.R;
import com.example.ascope_lite.RetrofitClient;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inspect_ViewListActivity extends AppCompatActivity {
    ApiService service;
    Context context = this;

    TextView tv_count;
    Button btn_planList;
    ImageButton reload_img_btn;
    Spinner sp_planList_name;

    ListView lv_crack;
    Inspect_ViewListAdapter lv_adapter;
    Plan_Adapter plan_adapter;

    ArrayList<PlanResponse> plan_list = new ArrayList<>();
    List<CrackListResponse> coordinate_list;
    String projectid;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 세로 고정
        setContentView(R.layout.activity_inspect_viewlist);

        SharedPreferences preferences = getSharedPreferences("tokenSave", MODE_PRIVATE);
        String Token = "Bearer " + preferences.getString("token", "null");
        int URL_flag = preferences.getInt("URL_flag", 0);

        service = RetrofitClient.getClient(URL_flag).create(ApiService.class);

        Bundle bundle = getIntent().getExtras();
        ProjectGetListResponse project_data = (ProjectGetListResponse) bundle.getSerializable("list_data");

        projectid = String.valueOf(project_data.getId());
//        String upper = String.valueOf(project_data.getUpper());
//        String under = String.valueOf(project_data.getUnder());

        plan_list = (ArrayList<PlanResponse>) bundle.getSerializable("plan_list");

        tv_count = findViewById(R.id.tv_count);

        reload_img_btn = findViewById(R.id.reload_img_btn);

        reload_img_btn.setOnClickListener(v -> {
            try {
                Intent intent = getIntent();
                finish(); //현재 액티비티 종료 실시
                overridePendingTransition(0, 0); //인텐트 애니메이션 제거
                startActivity(intent); //현재 액티비티 재실행 실시
                overridePendingTransition(0, 0); //인텐트 애니메이션 제거
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        sp_planList_name = findViewById(R.id.sp_planList_name);
        lv_crack = findViewById(R.id.lv_contents);

        plan_adapter = new Plan_Adapter(Inspect_ViewListActivity.this, plan_list);
        sp_planList_name.setAdapter(plan_adapter);

        sp_planList_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int i, long id1) {
                Log.e("onItemSelected: ", "parent.getAdapter().getItem(i):: " + parent.getAdapter().getItem(i));
                service.get_crack_list(Token, projectid, String.valueOf(parent.getAdapter().getItem(i))).enqueue(new Callback<List<CrackListResponse>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call<List<CrackListResponse>> call, @NonNull Response<List<CrackListResponse>> response) {
                        List<CrackListResponse> result = response.body();
                        if (result != null) {
                            Log.e("onResponse: ", new Gson().toJson(result));
                            coordinate_list = response.body();

                            tv_count.setText(result.size() + "건");

                            lv_adapter = new Inspect_ViewListAdapter(context, result);
                            lv_crack.setAdapter(lv_adapter);
                            lv_crack.setOnItemClickListener((adapterView, view, position, id) -> {
                                Intent intent = new Intent(getApplicationContext(), Inspect_ViewDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("project_data", project_data);
                                bundle.putSerializable("plan_list", plan_list);
                                bundle.putSerializable("crack_data", lv_adapter.getData().get(position));
                                intent.putExtras(bundle);
//                                Log.e("onItemSelected: ", "crack_id: " + lv_adapter.getData().get(position).getId());
                                startActivity(intent);
                            });

                        } else {
                            Log.e("onResponse: ", "result is null");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<CrackListResponse>> call, @NonNull Throwable t) {
                        Log.e("onFailure: ", t.getMessage());
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                service.get_crack_list(Token, projectid, String.valueOf(parent.getAdapter().getItem(0))).enqueue(new Callback<List<CrackListResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<CrackListResponse>> call, @NonNull Response<List<CrackListResponse>> response) {
                        List<CrackListResponse> result = response.body();
                        Log.e("onResponse: ", new Gson().toJson(result));
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<CrackListResponse>> call, @NonNull Throwable t) {
                        Log.e("onFailure: ", t.getMessage());
                    }
                });
            }
        });

        btn_planList = findViewById(R.id.btn_planList);
        btn_planList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PlanCoordinateActivity.class);
            Bundle plan_bundle = new Bundle();

            plan_bundle.putSerializable("coordinate_list", (Serializable) coordinate_list);
            plan_bundle.putSerializable("plan_list", plan_list);
            plan_bundle.putString("projectid", projectid);
            intent.putExtras(plan_bundle);
            startActivity(intent);
        });
    }
}