package com.example.ascope_lite.Planlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ascope_lite.ApiService;
import com.example.ascope_lite.CrackInspect.CrackCoordinateData;
import com.example.ascope_lite.CrackInspect.CrackListResponse;
import com.example.ascope_lite.GlideApp;
import com.example.ascope_lite.Plan_Adapter;
import com.example.ascope_lite.R;
import com.example.ascope_lite.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanCoordinateActivity extends AppCompatActivity {
    private com.example.ascope_lite.CamTest_v01.DrawView graphicView;
    Button btn_ok;
    Spinner sp_filter;
    TextView tv_count;

    ApiService service;

    String file_path;
    Context context = this;

    Bitmap result_bitmap;
    ArrayList<CrackCoordinateData> mark_list = new ArrayList<>();
    ArrayList<PlanResponse> plan_list = new ArrayList<>();

    String base_url;

    Plan_Adapter plan_adapter;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 세로 고정
        setContentView(R.layout.activity_plancoordinate);

        //token
        SharedPreferences preferences = getSharedPreferences("tokenSave", Context.MODE_PRIVATE);
        String Token = "Bearer " + preferences.getString("token", "null");
        int URL_flag = preferences.getInt("URL_flag", 0);

        service = RetrofitClient.getClient(URL_flag).create(ApiService.class);

        Bundle bundle = getIntent().getExtras();
        List<CrackListResponse> coordinate_list = (List<CrackListResponse>) bundle.getSerializable("coordinate_list");
        String projectid = bundle.getString("projectid");
        plan_list = (ArrayList<PlanResponse>) bundle.getSerializable("plan_list");

        base_url = RetrofitClient.getBaseUrl();

        graphicView = findViewById(R.id.graphicsView);
        graphicView.PutIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_1_0));//균열

        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(v -> {
            finish();
        });

        tv_count = findViewById(R.id.tv_count);

        sp_filter = findViewById(R.id.sp_filter);

        plan_adapter = new Plan_Adapter(PlanCoordinateActivity.this, plan_list);
        sp_filter.setAdapter(plan_adapter);

        sp_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int i, long id1) {
                mark_list.clear();

                //도면 data request
                service.get_plan(Token, String.valueOf(parent.getAdapter().getItem(i))).enqueue(new Callback<PlanResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PlanResponse> call, @NonNull Response<PlanResponse> response) {
                        PlanResponse plan_result = response.body();
                        if (response.isSuccessful() && plan_result != null) {
                            Log.e("onResponse: ", "plan data:: " + new Gson().toJson(plan_result));

                            file_path = base_url + plan_result.getImg();

                            //좌표 data request
                            Log.e("onResponse: ", "parent.getAdapter().getItem(i):: " + parent.getAdapter().getItem(i));
                            service.get_crack_list(Token, projectid, String.valueOf(parent.getAdapter().getItem(i))).enqueue(new Callback<List<CrackListResponse>>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onResponse(@NonNull Call<List<CrackListResponse>> call, @NonNull Response<List<CrackListResponse>> response) {
                                    List<CrackListResponse> coordinate_result = response.body();
                                    if (response.isSuccessful() && coordinate_result != null) {
                                        Log.e("onResponse: ", "coordinate data:: " + new Gson().toJson(coordinate_result));

                                        tv_count.setText(coordinate_result.size() + "건");

                                        for (int j = 0; j < coordinate_result.size(); j++) {
                                            CrackCoordinateData data = new CrackCoordinateData();

                                            data.setPlan(coordinate_result.get(j).getCoordinate().getPlan());
                                            data.setX(coordinate_result.get(j).getCoordinate().getX());
                                            data.setY(coordinate_result.get(j).getCoordinate().getY());
                                            data.setIndex(coordinate_result.get(j).getCoordinate().getIndex());
                                            data.setId(coordinate_result.get(j).getCoordinate().getId());
                                            data.setProject(coordinate_result.get(j).getCoordinate().getProject());
                                            mark_list.add(data);
                                            Log.e("mark_list", "mark_list (x ,y) ::: " + mark_list.get(j).getX() + "," + mark_list.get(j).getY());
                                        }

                                        graphicView.setMark_list(mark_list);

                                        GlideApp.with(context).asBitmap().load(file_path)
                                                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                                                .into(new CustomTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                        graphicView.ReSetDraw();
                                                        graphicView.PutImage(resource, 3);

                                                        result_bitmap = createViewToBitmap(graphicView);
                                                        graphicView.PutImage(result_bitmap, 0);
                                                        graphicView.invalidate();
                                                    }

                                                    @Override
                                                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                        Log.e("onLoadCleared: ", "onLoadCleared");
                                                    }
                                                });

                                    } else {
                                        Toast.makeText(getApplicationContext(), "해당 도면이 없습니다.", Toast.LENGTH_SHORT).show();
                                        Log.e("get_plan: ", "해당 도면 데이터 없음");
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<List<CrackListResponse>> call, @NonNull Throwable t) {
                                    Log.e("get_plan: ", t.getMessage());
                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(), "해당 도면이 없습니다.", Toast.LENGTH_SHORT).show();
                            Log.e("get_plan: ", "해당 도면 데이터 없음");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PlanResponse> call, @NonNull Throwable t) {
                        Log.e("get_plan: ", t.getMessage());
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
                Toast.makeText(getApplicationContext(), "onNothingSelected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Bitmap createViewToBitmap(com.example.ascope_lite.CamTest_v01.DrawView graphicView) {
        Bitmap bitmap = Bitmap.createBitmap(graphicView.getMeasuredWidth(), graphicView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        graphicView.draw(canvas);
        return bitmap;
    }
}