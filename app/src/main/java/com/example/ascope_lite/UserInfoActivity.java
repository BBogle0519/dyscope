package com.example.ascope_lite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ascope_lite.Login.LoginActivity;

@SuppressLint("SourceLockedOrientationActivity")
public class UserInfoActivity extends AppCompatActivity {
    AlertDialog dialog;
    Context context = this;

    Button btn_ok, btn_logout;
    TextView tv_company_name, tv_user_name, tv_user_id, tv_user_email;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 세로 고정
        setContentView(R.layout.activity_user_info);

        SharedPreferences preferences = getSharedPreferences("tokenSave", MODE_PRIVATE);
        String user_id = preferences.getString("user_id", "null");

        Bundle bundle = getIntent().getExtras();
        UserResponse user_data = (UserResponse) bundle.getSerializable("user_data");

        tv_company_name = findViewById(R.id.tv_company_name);
        tv_company_name.setText(user_data.getCompany_name());


        tv_user_id = findViewById(R.id.tv_user_id);
        tv_user_id.setText(user_id);

        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_name.setText(user_data.getName());

        tv_user_email = findViewById(R.id.tv_user_email);
        tv_user_email.setText(user_data.getEmail());

        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                finish();
            }
        });

        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                dialog = builder.setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("확인", (dialogInterface, i) -> {
                            SharedPreferences sharedPreferences = getSharedPreferences("tokenSave", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();
                            Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                            ActivityCompat.finishAffinity((Activity) context);
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                        })
                        .setNegativeButton("취소", (dialogInterface, i) -> {
                            dialog.dismiss();
                        })
                        .create();
                dialog.show();

            }
        });
    }
}
