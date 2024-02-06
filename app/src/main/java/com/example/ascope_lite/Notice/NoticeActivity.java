package com.example.ascope_lite.Notice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ascope_lite.ApiService;
import com.example.ascope_lite.InspectionListActivity;
import com.example.ascope_lite.OnSingleClickListener;
import com.example.ascope_lite.Project.ProjectGetListResponse;
import com.example.ascope_lite.Project.ProjectListAdapter;
import com.example.ascope_lite.R;
import com.example.ascope_lite.RetrofitClient;
import com.example.ascope_lite.UserInfoActivity;
import com.example.ascope_lite.UserResponse;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SourceLockedOrientationActivity")
public class NoticeActivity extends AppCompatActivity {
    ApiService service;
    Context context = this;

//    ListView lv_project;
//    NoticeListAdapter adapter;
//    List<NoticeDTO> result;
    ImageButton btn_home, btn_inspect, btn_pdf, btn_admin;
    LinearLayout notice_layout, listView_layout;

    private final String tag = "1"; //공지사항 목록 조회 시 tag 값 (현재 앱에선 공지사항만 조회)

    private long backpressedTime = 0;
    int URL_flag = 0;
    String Token = null;

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish();
        }
//        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 세로 고정
        setContentView(R.layout.activity_notice);

//        this.InitializeLayout();

        SharedPreferences preferences = getSharedPreferences("tokenSave", MODE_PRIVATE);
        Token = "Bearer " + preferences.getString("token", "null");
        URL_flag = preferences.getInt("URL_flag", 0);

        service = RetrofitClient.getClient(URL_flag).create(ApiService.class);

        Log.e("NoticeActivity", Token);
        notice_layout = findViewById(R.id.notice_layout);
        listView_layout = findViewById(R.id.listView_layout);
//        linearLayout = findViewById(R.id.linearLayout);

        /*
        service.get_notice_list(tag).enqueue(new Callback<List<NoticeDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<NoticeDTO>> call, @NonNull Response<List<NoticeDTO>> response) {
                result = response.body();
                if (result != null) {
                    Log.e("onResponse: ", new Gson().toJson(result));

                    lv_project = findViewById(R.id.lv_contents);
                    adapter = new NoticeListAdapter(context, result);
                    lv_project.setAdapter(adapter);
                    lv_project.setOnItemClickListener((adapterView, view, position, id) -> {
                        Intent intent = new Intent(getApplicationContext(), NoticeDetailActivity.class);
                        intent.putExtra("notice_id", adapter.getNoticeID(position));
                        startActivity(intent);
                    });
                } else {
                    Toast.makeText(context, "통신 오류: 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                    Log.e("1onResponse:", "result:: null");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NoticeDTO>> call, @NonNull Throwable t) {
                Toast.makeText(context, "서버 연결 실패: 5xx", Toast.LENGTH_SHORT).show();
                Log.e("onFailure", t.getMessage());
            }
        });
        */

        service.get_project_list(Token).enqueue(new Callback<List<ProjectGetListResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProjectGetListResponse>> call, @NonNull Response<List<ProjectGetListResponse>> response) {
                List<ProjectGetListResponse> result = response.body();
                if (result != null) {
                    Log.e("ViewList onResponse", new Gson().toJson(result));

                    ListView lv_project = findViewById(R.id.lv_contents2);
                    ProjectListAdapter adapter = new ProjectListAdapter(context, result);
                    lv_project.setAdapter(adapter);

                    lv_project.setOnItemClickListener((adapterView, view, position, id) -> {
//                    Toast.makeText(context, adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), InspectionListActivity.class);
                        Bundle bundle = new Bundle();
                        ProjectGetListResponse project_data = result.get(position);
//                    Log.e("ViewList onResponse", project_list.get(position).getFacilityname());
                        bundle.putSerializable("project_data", project_data);
                        intent.putExtras(bundle);
                        startActivity(intent);
//                    finish();
                    });
                } else {
                    Toast.makeText(context, "통신 오류: 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                    Log.e("ViewList onResponse:", "result:: null");
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<ProjectGetListResponse>> call, @NonNull Throwable t) {
                Toast.makeText(context, "서버 연결 실패: 5xx", Toast.LENGTH_SHORT).show();
                Log.e("ViewList onFailure: 5xx", t.getMessage());
            }
        });

        btn_home = findViewById(R.id.btn_home);
        btn_inspect = findViewById(R.id.btn_inspect);
        btn_home.setImageResource(R.drawable.ic_btn_navbar_page_true);
        btn_inspect.setImageResource(R.drawable.ic_btn_navbar_page_true);

        btn_home.setOnClickListener(v -> {
            btn_home.setImageResource(R.drawable.ic_btn_navbar_page_true);
            btn_inspect.setImageResource(R.drawable.ic_btn_inspect_false);
            notice_layout.setVisibility(View.VISIBLE);
            listView_layout.setVisibility(View.GONE);

            /*
            service.get_notice_list(tag).enqueue(new Callback<List<NoticeDTO>>() {
                @Override
                public void onResponse(@NonNull Call<List<NoticeDTO>> call, @NonNull Response<List<NoticeDTO>> response) {
                    result = response.body();
                    if (result != null) {
                        Log.e("onResponse: ", new Gson().toJson(result));

                        ListView lv_project = findViewById(R.id.lv_contents);
                        NoticeListAdapter adapter = new NoticeListAdapter(context, result);
                        lv_project.setAdapter(adapter);

                        lv_project.setOnItemClickListener((adapterView, view, position, id) -> {
                            Intent intent = new Intent(getApplicationContext(), NoticeDetailActivity.class);
                            intent.putExtra("notice_id", adapter.getNoticeID(position));
                            startActivity(intent);
                        });
                    } else {
                        Toast.makeText(context, "통신 오류: 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                        Log.e("2onResponse:", "result:: null");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<NoticeDTO>> call, @NonNull Throwable t) {
                    Toast.makeText(context, "서버 연결 실패: 5xx", Toast.LENGTH_SHORT).show();
                    Log.e("onFailure", t.getMessage());
                }
            });
            */
        });

        btn_inspect.setOnClickListener(v -> {
//            btn_inspect.setImageResource(R.drawable.ic_btn_inspect_true);
            btn_inspect.setImageResource(R.drawable.ic_btn_navbar_page_true);
            btn_home.setImageResource(R.drawable.ic_btn_navbar_page_false);
            btn_admin.setImageResource(R.drawable.ic_btn_admin);
            notice_layout.setVisibility(View.GONE);
            listView_layout.setVisibility(View.VISIBLE);
//            linearLayout.setVisibility(View.VISIBLE);
            Log.e("TOKEN::::: ", Token);

            service.get_project_list(Token).enqueue(new Callback<List<ProjectGetListResponse>>() {
                @Override
                public void onResponse(@NonNull Call<List<ProjectGetListResponse>> call, @NonNull Response<List<ProjectGetListResponse>> response) {
                    List<ProjectGetListResponse> result = response.body();
                    if (result != null) {
                        Log.e("ViewList onResponse", new Gson().toJson(result));

                        ListView lv_project = findViewById(R.id.lv_contents2);
                        ProjectListAdapter adapter = new ProjectListAdapter(context, result);
                        lv_project.setAdapter(adapter);

                        lv_project.setOnItemClickListener((adapterView, view, position, id) -> {
//                    Toast.makeText(context, adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), InspectionListActivity.class);
                            Bundle bundle = new Bundle();
                            ProjectGetListResponse project_data = result.get(position);
//                    Log.e("ViewList onResponse", project_list.get(position).getFacilityname());
                            bundle.putSerializable("project_data", project_data);
                            intent.putExtras(bundle);
                            startActivity(intent);
//                    finish();
                        });
                    } else {
                        Toast.makeText(context, "통신 오류: 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                        Log.e("ViewList onResponse:", "result:: null");
                    }

                }

                @Override
                public void onFailure(@NonNull Call<List<ProjectGetListResponse>> call, @NonNull Throwable t) {
                    Toast.makeText(context, "서버 연결 실패: 5xx", Toast.LENGTH_SHORT).show();
                    Log.e("ViewList onFailure: 5xx", t.getMessage());
                }
            });

        });


        btn_pdf = findViewById(R.id.btn_pdf);
        btn_pdf.setOnClickListener(v -> {
            Toast.makeText(context, "(임시 메뉴)준비중입니다.", Toast.LENGTH_SHORT).show();
        });

        btn_admin = findViewById(R.id.btn_admin);
        btn_admin.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
//                btn_inspect.setImageResource(R.drawable.ic_btn_navbar_page_true);
//                btn_inspect.setImageResource(R.drawable.ic_btn_navbar_page_false);
//                btn_home.setImageResource(R.drawable.ic_btn_navbar_page_false);
//                btn_admin.setImageResource(R.drawable.ic_btn_admin_true);
//                notice_layout.setVisibility(View.GONE);
//                listView_layout.setVisibility(View.GONE);
//                linearLayout.setVisibility(View.VISIBLE);

                service.get_user_info(Token).enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                        UserResponse result = response.body();

                        if (result != null) {
                            Log.e("onResponse: ", new Gson().toJson(result));
                            Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user_data", result);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        } else {
                            Toast.makeText(context, "통신 오류: 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                            Log.e("3onResponse:", "result:: null");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                        Toast.makeText(context, "서버 연결 실패: 5xx", Toast.LENGTH_SHORT).show();
                        Log.e("onFailure", t.getMessage());
                    }
                });
            }
        });
    }

    /*
    public void InitializeLayout() {
        //toolBar를 통해 App Bar 생성
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //App Bar의 좌측 영역에 Drawer를 Open 하기 위한 Incon 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24);

        DrawerLayout drawLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.item_user_info:
                    Toast.makeText(getApplicationContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.item_inspect:
                    Toast.makeText(getApplicationContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.item_viewList:
                    Toast.makeText(getApplicationContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.item_e_today:
                    Toast.makeText(getApplicationContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.item_contact:
                    Toast.makeText(getApplicationContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
                    break;
            }

            drawLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        );
        drawLayout.addDrawerListener(actionBarDrawerToggle);
    }
    */
}
