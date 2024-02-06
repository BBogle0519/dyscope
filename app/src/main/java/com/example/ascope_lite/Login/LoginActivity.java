package com.example.ascope_lite.Login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ascope_lite.ApiService;
import com.example.ascope_lite.Loading_Progress;
import com.example.ascope_lite.Notice.NoticeActivity;
import com.example.ascope_lite.OnSingleClickListener;
import com.example.ascope_lite.R;
import com.example.ascope_lite.RetrofitClient;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

@SuppressLint("SourceLockedOrientationActivity")
public class LoginActivity extends AppCompatActivity {
    private AlertDialog dialog;
    private ApiService service;
    Context context = this;

    int URL_flag = 0; //"http://211.41.73.29"
    int inspect_URL = 2; //"http://211.41.73.29:12000"

    Loading_Progress loading_progress;
    EditText edt_id, edt_pw;
    Button loginButton, btn_ok;
    ConstraintLayout layout_login, layout_login2;

    private long backpressedTime = 0;

    @Override
    public void onBackPressed() {
//            super.onBackPressed();
        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 세로 고정
        setContentView(R.layout.activity_login);

        loading_progress = new Loading_Progress(this);
        loading_progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_progress.setCancelable(false);

        service = RetrofitClient.getClient(URL_flag).create(ApiService.class);

        edt_id = findViewById(R.id.edt_id);
        edt_pw = findViewById(R.id.edt_pw);
        loginButton = findViewById(R.id.loginButton);
        btn_ok = findViewById(R.id.btn_ok);
        layout_login = findViewById(R.id.layout_login);
        layout_login2 = findViewById(R.id.layout_login2);

        loginButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                LoginData loginData = new LoginData();
                String userid;
                String password;

                if (edt_id.getText().toString().length() == 0) {
                    Toast.makeText(context, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                    Log.e("edt_id", "null");
                    return;
                } else {
                    userid = edt_id.getText().toString();
                }

                if (edt_pw.getText().toString().length() == 0) {
                    Toast.makeText(context, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    Log.e("edt_pw", "null");
                    return;
                } else {
                    password = edt_pw.getText().toString();
                }

                loginData.setUserid(userid);
                loginData.setPassword(password);

                startLogin(loginData);
                Log.e("URL_flag::::", "" + URL_flag);
            }
        });

    }

    private void startLogin(LoginData data) {
        LoginRunnable loginRunnable = new LoginRunnable();
        loginRunnable.setLoginData(data);

        Thread thread = new Thread(loginRunnable);
        thread.start();
        loading_progress.show();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loading_progress.dismiss();
        Log.e("Login Thread::", Thread.currentThread().getName() + " end");
    }

    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    private class LoginRunnable implements Runnable {
        LoginData data;

        public void setLoginData(LoginData data) {
            this.data = data;
        }

        @Override
        public void run() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            Log.e("data::", data.getUserid() + ", " + data.getPassword());
            Call<LoginResponse> login_post = service.login_post(data);

            try {
                Response<LoginResponse> response1 = login_post.execute();
                LoginResponse result1;

                if (!response1.isSuccessful()) {//404 error
                    Log.e("onFailure_inspector", "4xx error");
                    return;
                } else {
                    result1 = response1.body();
                    Log.e("onResponse_inspector", new Gson().toJson(result1));
                }

                if (result1 != null) {
                    if (result1.getMessage().equals("success")) { //login success
                        SharedPreferences preferences = getSharedPreferences("tokenSave", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        Log.e("TOKEN_::: ", result1.getToken());
                        editor.putString("token", result1.getToken());
                        editor.putString("user_id", data.getUserid());
                        editor.putInt("URL_flag", URL_flag);
                        editor.putInt("inspect_URL", inspect_URL);
                        editor.apply();
                        loading_progress.dismiss();
                        runOnUiThread(() -> {
                            layout_login.setVisibility(View.GONE);
                            layout_login2.setVisibility(View.VISIBLE);
                            btn_ok.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
                                        startActivity(intent);
                                        finish();
                                }
                            });

//                            dialog = builder.setMessage(data.getUserid() + "님 환영합니다.")
//                                    .setCancelable(false)
//                                    .setPositiveButton("확인", (dialogInterface, i) -> {
//                                        Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    })
//                                    .create();
//                            dialog.show();
                        });
                    } else if (result1.getMessage().equals("wrong password")) { //pw wrong
                        String count = result1.getAttempt();
                        runOnUiThread(() -> {
                            dialog = builder.setMessage("비밀번호가 " + count + "회 틀렸습니다.(5회 실패시 계정이 잠깁니다.)")
                                    .setPositiveButton("확인", (dialogInterface, i) -> {
                                    })
                                    .create();
                            dialog.show();
                        });

                    } else if (result1.getMessage().equals("locked")) { //account lock
                        runOnUiThread(() -> {
                            dialog = builder.setMessage("비밀번호 입력을 5회 이상 실패하여 계정이 잠금되었습니다. 관리자에게 문의하세요.")
                                    .setPositiveButton("확인", (dialogInterface, i) -> {
                                    })
                                    .create();
                            dialog.show();
                        });
                    } else if (result1.getMessage().equals("deactivated")) { //account deactivated
                        runOnUiThread(() -> {
                            dialog = builder.setMessage("비활성화된 계정입니다. 관리자에게 문의하세요.")
                                    .setPositiveButton("확인", (dialogInterface, i) -> {
                                    })
                                    .create();
                            dialog.show();
                        });
                    } else if (result1.getMessage().equals("no user")) { //id wrong
                        runOnUiThread(() -> {
                            dialog = builder.setMessage("존재하지 않는 id입니다.")
                                    .setPositiveButton("확인", (dialogInterface, i) -> {
                                    })
                                    .create();
                            dialog.show();
                        });
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("onFailure_inspector", e.getMessage());
            }

//            thread2();
        }
    }

    public void thread2() {
//        thread3();
    }

    public void thread3() {

    }
}
