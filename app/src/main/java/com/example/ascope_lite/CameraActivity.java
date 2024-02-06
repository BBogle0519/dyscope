package com.example.ascope_lite;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ascope_lite.CrackInspect.CrackData;
import com.example.ascope_lite.Planlist.PlanResponse;

import java.util.ArrayList;

public class CameraActivity extends AppCompatActivity {
    int menu;
    int type;
    String plan_id = null;
    ArrayList<PlanResponse> plan_list = new ArrayList<>();

    ApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_camtest);

        SharedPreferences preferences = getSharedPreferences("tokenSave", MODE_PRIVATE);
        String Token = "Bearer " + preferences.getString("token", "null");
        int URL_flag = preferences.getInt("URL_flag", 0);

        service = RetrofitClient.getClient(URL_flag).create(ApiService.class);

        Bundle send_bundle = new Bundle();
        Bundle bundle = getIntent().getExtras();
        plan_list = (ArrayList<PlanResponse>) bundle.getSerializable("plan_list");
        plan_id = bundle.getString("plan_id");
        CrackData data = (CrackData) bundle.getSerializable("crackInspect_bundle");

        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("checkPermission:", "권한 설정 완료");
            } else {
                Log.e("checkPermission:", "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                //makeDir();
                //getSaveFolder();
            }
        }

        CameraFragment fragment = new CameraFragment();

        menu = getIntent().getIntExtra("menu", 0);
        type = getIntent().getIntExtra("type", 0);
        if (menu == 1) {//crack (수동)
            Log.e("onCreate()", "menu = " + menu);
            Log.e("onCreate()", "type = " + type);
            send_bundle.putSerializable("send_bundle", data);
            send_bundle.putSerializable("plan_list", plan_list);
            send_bundle.putString("plan_id", plan_id);
            send_bundle.putInt("menu_f", bundle.getInt("menu"));
            send_bundle.putInt("type", bundle.getInt("type"));

        } else {
            Log.e("onCreate()", "menu = " + menu);
            Log.e("onCreate()", "type = " + type);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        fragment.setArguments(send_bundle);

    }

    // 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("checkPermission", "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.e("checkPermission", "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }
}
