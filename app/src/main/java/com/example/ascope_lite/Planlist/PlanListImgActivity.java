package com.example.ascope_lite.Planlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.ascope_lite.ApiService;
import com.example.ascope_lite.CamTest_v01.WebActivity;
import com.example.ascope_lite.GlideApp;
import com.example.ascope_lite.R;
import com.example.ascope_lite.RetrofitClient;

public class PlanListImgActivity extends AppCompatActivity {
    com.example.ascope_lite.CamTest_v01.DrawView graphicView;
    Button btn_ok, btn_scale_mode, btn_mark_mode;
    ApiService service;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 세로 고정
        setContentView(R.layout.activity_planlistimg);

        //token
        SharedPreferences preferences = getSharedPreferences("tokenSave", Context.MODE_PRIVATE);
        String Token = "Bearer " + preferences.getString("token", "null");
        int URL_flag = preferences.getInt("URL_flag", 0);

        service = RetrofitClient.getClient(URL_flag).create(ApiService.class);

        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        String file_path = intent.getStringExtra("plan_img");
        file_path = RetrofitClient.getBaseUrl() + file_path;
        Log.e("file_path: ", "file_path: " + file_path);
        graphicView = findViewById(R.id.graphicsView);

        GlideApp.with(this).asBitmap().load(file_path)
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Log.e("onResourceReady: ", "resource: " + resource);
                        graphicView.ReSetDraw();
                        if (type == 1) {//균열
                            graphicView.PutIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_1_0));
                        }

                        graphicView.PutImage(resource, 1);
                        graphicView.invalidate();
                        Log.e("onResourceReady: ", "resource.getWidth(): " + resource.getWidth());
                        Log.e("onResourceReady: ", "resource.getHeight(): " + resource.getHeight());

                        Log.e("onResourceReady: ", "graphicView.getWidth(): " + graphicView.getWidth());
                        Log.e("onResourceReady: ", "graphicView.getHeight(): " + graphicView.getHeight());

//                        Log.e("getLeft: ", String.valueOf(graphicView.getLeft()));
//                        Log.e("getTop: ", String.valueOf(graphicView.getTop()));
//                        Log.e("getWidth: ", String.valueOf(graphicView.getWidth()));
//                        Log.e("getHeight: ", String.valueOf(graphicView.getHeight()));

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.e("onLoadCleared: ", "onLoadCleared: ");
                    }
                });


        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(v -> {
            if (graphicView.getXY_pos() == null) {
                Log.e("btn_ok: ", "x, y : null");
            } else {
                Intent result_intent = new Intent(PlanListImgActivity.this, WebActivity.class);
                result_intent.putExtra("x", graphicView.getXY_pos()[0]);
                result_intent.putExtra("y", graphicView.getXY_pos()[1]);
                setResult(RESULT_OK, result_intent);

                Log.e("btn_ok: ", "x, y : " + graphicView.getXY_pos()[0] + ", " + graphicView.getXY_pos()[1]);
                finish();
            }
        });

        btn_scale_mode = findViewById(R.id.btn_scale_mode);
        btn_scale_mode.setOnClickListener(v -> {
            graphicView.SetScaleMode();
            AlarmMsg("이동 모드.");
        });

        btn_mark_mode = findViewById(R.id.btn_mark_mode);
        btn_mark_mode.setOnClickListener(v -> {
            graphicView.SetEditMode(0);
            AlarmMsg("마커 모드.");
        });
    }

//    // 이미지 회전
//    public Bitmap rotateImage(Bitmap src, float degree) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(degree);
//
//        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
//                src.getHeight(), matrix, true);
//    }

    private void AlarmMsg(String MsgStr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ImageEditor").setMessage(MsgStr);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
