package com.example.ascope_lite.CamTest_v01.calculation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ascope_lite.CrackInspect.CrackData;
import com.example.ascope_lite.Planlist.PlanResponse;
import com.example.ascope_lite.Project.ProjectGetListResponse;
import com.example.ascope_lite.R;

import java.util.ArrayList;

public class SizeCalculationCameraActivity extends AppCompatActivity {
    int type;
    String plan_id;
    ArrayList<PlanResponse> plan_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_camtest);
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

        SizeCalculationCameraFragment fragment = new SizeCalculationCameraFragment();

        Bundle send_bundle = new Bundle();

        type = getIntent().getIntExtra("type", 0);

        Bundle bundle = getIntent().getExtras();
        if (type != 2) {
            plan_id = bundle.getString("plan_id");
            plan_list = (ArrayList<PlanResponse>) bundle.getSerializable("plan_list");

            CrackData data = (CrackData) bundle.getSerializable("crackInspect_bundle");
            send_bundle.putSerializable("send_bundle", data);
            send_bundle.putSerializable("plan_list", plan_list);
            send_bundle.putString("plan_id", plan_id);
        }
        send_bundle.putInt("type", bundle.getInt("type"));
        send_bundle.putInt("URL_flag", bundle.getInt("URL_flag"));

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
