package com.example.ascope_lite.Inspect_ViewList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ascope_lite.ApiService;
import com.example.ascope_lite.CrackInspect.CrackCoordinateData;
import com.example.ascope_lite.CrackInspect.CrackInspectActivity;
import com.example.ascope_lite.CrackInspect.CrackListResponse;
import com.example.ascope_lite.GlideApp;
import com.example.ascope_lite.Loading_Progress;
import com.example.ascope_lite.Planlist.PlanResponse;
import com.example.ascope_lite.Project.ProjectGetListResponse;
import com.example.ascope_lite.R;
import com.example.ascope_lite.ResponseMessage;
import com.example.ascope_lite.RetrofitClient;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Inspect_ViewDetailActivity extends AppCompatActivity {
    ApiService service;
    Context context = this;

    AlertDialog dialog;
    Loading_Progress loading_progress;
    com.example.ascope_lite.CamTest_v01.DrawView graphicView;
    ScrollView scrollView;
    Button btn_content_update, btn_back_content, btn_delete;
    ImageView iv_inspectImg;
    TextView tv_type, tv_zone, tv_position, tv_userid, tv_etc, tv_updated_at;
    TextView tv_crack_size, tv_crack_length, tv_crack_unit;

    String plan_id;
    String file_path;
    Bitmap result_bitmap;
    CrackCoordinateData mark_data = new CrackCoordinateData();
    String base_url;
    String img_url;

    ArrayList<PlanResponse> plan_list = new ArrayList<>();

    @SuppressLint({"SourceLockedOrientationActivity", "SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 세로 고정
        setContentView(R.layout.activity_inspect_viewdetail);

        loading_progress = new Loading_Progress(this);
        loading_progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_progress.setCancelable(false);

        Bundle bundle = getIntent().getExtras();

        plan_list = (ArrayList<PlanResponse>) bundle.getSerializable("plan_list");
        CrackListResponse crack_data = (CrackListResponse) bundle.getSerializable("crack_data");
        ProjectGetListResponse project_data = (ProjectGetListResponse) bundle.getSerializable("project_data");
        Log.e("onCreate:", "crack_id:: " + crack_data.getId());

        SharedPreferences preferences = getSharedPreferences("tokenSave", MODE_PRIVATE);
        String Token = "Bearer " + preferences.getString("token", "null");
        int URL_flag = preferences.getInt("URL_flag", 0);

        service = RetrofitClient.getClient(URL_flag).create(ApiService.class);

        base_url = RetrofitClient.getBaseUrl();
        img_url = base_url + crack_data.getImg();
        plan_id = crack_data.getPlan();
        mark_data = crack_data.getCoordinate();

        btn_content_update = findViewById(R.id.btn_content_update);

        btn_content_update.setOnClickListener(v -> {
            project_data.setCoordinateid(crack_data.getCoordinate().getId());
            project_data.setCrackid(crack_data.getId());
            Intent intent = new Intent(getApplicationContext(), CrackInspectActivity.class);
            Bundle crack_bundle = new Bundle();
            crack_bundle.putSerializable("crack_bundle", project_data);
            crack_bundle.putSerializable("plan_list", plan_list);
            crack_bundle.putInt("update", 1);
            intent.putExtras(crack_bundle);
            startActivity(intent);
        });

        btn_back_content = findViewById(R.id.btn_back_content);
        btn_back_content.setOnClickListener(v -> {
            finish();
        });

        btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            dialog = builder.setMessage("삭제 하시겠습니까?")
                    .setPositiveButton("확인", (dialogInterface, i) -> {
                        service.delete_crack(Token, String.valueOf(crack_data.getId())).enqueue(new Callback<ResponseMessage>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseMessage> call, @NonNull Response<ResponseMessage> response) {
                                ResponseMessage result = response.body();
                                Log.e("onResponse: ", new Gson().toJson(result));
                                Toast.makeText(context, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(@NonNull Call<ResponseMessage> call, @NonNull Throwable t) {
                                Log.e("onFailure", t.getMessage());
                            }
                        });

                        finish();
                        dialog.dismiss();
                    })
                    .setNegativeButton("취소", (dialogInterface, i) -> {
                        dialog.dismiss();
                    })
                    .create();
            dialog.show();

        });
        iv_inspectImg = findViewById(R.id.iv_inspectImg);

        GlideApp.with(this).asBitmap().load(img_url)
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        Log.e("onResourceReady1: ", "resource: " + resource);
                        iv_inspectImg.setImageBitmap(resource);
//                        Log.e("1: ", "resource.getWidth(): " + resource.getWidth());
//                        Log.e("2: ", "resource.getHeight(): " + resource.getHeight());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });


        scrollView = findViewById(R.id.scrollView);
        graphicView = findViewById(R.id.graphicsView);

        graphicView.setOnTouchListener((v, motionEvent) -> {
            int action = motionEvent.getAction();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE:
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    break;
            }
            return false;
        });

//        Display display = getWindowManager().getDefaultDisplay();  // in Activity
//        /* getActivity().getWindowManager().getDefaultDisplay() */ // in Fragment
//        Point size = new Point();
//        display.getSize(size); // or getSize(size)
//        int width = size.x;
//        int height = size.y;

        graphicView.PutIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_1_0));//균열
        graphicView.setMark_data(mark_data);

        service.get_plan(Token, plan_id).enqueue(new Callback<PlanResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlanResponse> call, @NonNull Response<PlanResponse> response) {
                PlanResponse result = response.body();
                if (response.isSuccessful() && result != null) {
                    Log.e("get_plan: ", new Gson().toJson(result));

                    file_path = base_url + result.getImg();

                    GlideApp.with(context).asBitmap().load(file_path)
                            .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                    Log.e("onResourceReady: ", "resource: " + resource);
                                    graphicView.ReSetDraw();
                                    graphicView.PutImage(resource, 2);

                                    result_bitmap = createViewToBitmap(graphicView);
                                    graphicView.PutImage(result_bitmap, 0);
                                    graphicView.invalidate();

//                        Log.e("onResourceReady: ", "getHeight: " + graphicView.getHeight());
//                        Log.e("onResourceReady: ", "getWidth: " + graphicView.getWidth());
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
            public void onFailure(@NonNull Call<PlanResponse> call, @NonNull Throwable t) {
                Log.e("get_plan: ", t.getMessage());
            }
        });


        tv_type = findViewById(R.id.tv_type);
        tv_type.setText(crack_data.getType());

        tv_zone = findViewById(R.id.tv_zone);
        tv_zone.setText(crack_data.getZone());

        tv_position = findViewById(R.id.tv_position);
        tv_position.setText(crack_data.getPosition());

        tv_userid = findViewById(R.id.tv_userid);
        tv_userid.setText(crack_data.getUserid());

        tv_etc = findViewById(R.id.tv_etc);
        tv_etc.setText(crack_data.getEtc());

        String date_st = crack_data.getUpdated_at();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Date date = null;
        try {
            date = format.parse(date_st);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        new_format.setTimeZone(TimeZone.getDefault());
        date_st = new_format.format(date);

        tv_updated_at = findViewById(R.id.tv_updated_at);
        tv_updated_at.setText(date_st);

        ////각 조사 별 layout setting
        tv_crack_unit = findViewById(R.id.tv_crack_unit);
        tv_crack_unit.setVisibility(View.VISIBLE);

        tv_crack_size = findViewById(R.id.tv_crack_size);
        tv_crack_size.setText(crack_data.getSize());

        tv_crack_length = findViewById(R.id.tv_crack_length);
        tv_crack_length.setText(crack_data.getLength());

    }

    public static Bitmap createViewToBitmap(com.example.ascope_lite.CamTest_v01.DrawView graphicView) {
        Bitmap bitmap = Bitmap.createBitmap(graphicView.getMeasuredWidth(), graphicView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        graphicView.draw(canvas);
        return bitmap;
    }
}
