package com.example.ascope_lite.Notice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ascope_lite.ApiService;
import com.example.ascope_lite.R;
import com.example.ascope_lite.RetrofitClient;

@SuppressLint("SourceLockedOrientationActivity")
public class NoticeDetailActivity extends AppCompatActivity {
    ApiService service;
    Context context = this;

    TextView tv_title, tv_date, tv_content, tv_author;
    Button btn_end;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 세로 고정
        setContentView(R.layout.activity_notice_detail);

        SharedPreferences preferences = getSharedPreferences("tokenSave", MODE_PRIVATE);
        String Token = "Bearer " + preferences.getString("token", "null");
        int URL_flag = preferences.getInt("URL_flag", 0);

        service = RetrofitClient.getClient(URL_flag).create(ApiService.class);

        Intent intent = getIntent();
        int id = intent.getIntExtra("notice_id", 0);
        Log.e("id: ", "" + id);


        tv_title = findViewById(R.id.tv_title);
        tv_author = findViewById(R.id.tv_author);
        tv_date = findViewById(R.id.tv_date);
        tv_content = findViewById(R.id.tv_content);
        btn_end = findViewById(R.id.btn_end);

//        service.get_notice_detail(Token, String.valueOf(id)).enqueue(new Callback<NoticeDetailResponse>() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onResponse(@NonNull Call<NoticeDetailResponse> call, @NonNull Response<NoticeDetailResponse> response) {
//                NoticeDetailResponse result = response.body();
//                if (result != null) {
//                    Log.e("onResponse: ", new Gson().toJson(result));
//
//                    String date_st = result.getUpdated_at();
//
//                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//
//                    Date date = null;
//                    try {
//                        date = format.parse(date_st);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
//                    SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    new_format.setTimeZone(TimeZone.getDefault());
//                    date_st = new_format.format(date);
//
//                    tv_title.setText(result.getTitle());
//                    tv_author.setText("작성자: " + result.getAuthor());
//                    tv_date.setText("작성일: " + date_st);
//                    tv_content.setText(Html.fromHtml(result.getContent())); //html 태그 처리
//                } else {
//                    Toast.makeText(context, "통신 오류: 다시 시도하세요.", Toast.LENGTH_SHORT).show();
//                    Log.e("onResponse:", "result:: null");
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<NoticeDetailResponse> call, @NonNull Throwable t) {
//                Log.e("onFailure", t.getMessage());
//            }
//        });

        btn_end.setOnClickListener(v -> {
            finish();
        });
    }
}
