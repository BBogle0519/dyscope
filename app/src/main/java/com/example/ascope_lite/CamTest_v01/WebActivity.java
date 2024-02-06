package com.example.ascope_lite.CamTest_v01;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ascope_lite.ApiService;
import com.example.ascope_lite.CrackInspect.CrackData;
import com.example.ascope_lite.CrackInspect.CrackResponse;
import com.example.ascope_lite.GlideApp;
import com.example.ascope_lite.Loading_Progress;
import com.example.ascope_lite.OnSingleClickListener;
import com.example.ascope_lite.Plan_Adapter;
import com.example.ascope_lite.Planlist.PlanListImgActivity;
import com.example.ascope_lite.Planlist.PlanResponse;
import com.example.ascope_lite.R;
import com.example.ascope_lite.ResponseMessage;
import com.example.ascope_lite.RetrofitClient;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebActivity extends AppCompatActivity {
    Loading_Progress loading_progress;
    AlertDialog alertDialog;
    Bitmap DownloadBitmap;
    Spinner crack_position_sp;
    Button btn_input, btn_recapture;
    Dialog dialog1;
    Button inputBtn, cancel_Btn, planListBtn, btn_download;
    EditText crackSize_edt, crackLength_edt, crack_etcEdt;
    CheckBox size_ch, length_ch, planData_ch;
    ConstraintLayout crack_layout2;
    Context context = this;

    private com.example.ascope_lite.CamTest_v01.DrawView graphicsView;
    private float x;
    private float y;

    String f_absolutePath;
    int type;
    int ch4;
    int border_type = 0;

    Plan_Adapter plan_adapter;
    ArrayList<PlanResponse> plan_list = new ArrayList<>();
    String plan_id;

    String analysis_url = null;

    //retrofit2로 새로 연결
    private ApiService service;

    //activity 종료 시 결과 값 받는 ActivityResultLauncher (startActivityForResult deprecated 대체)
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.e("getResultCode()", " " + result.getResultCode());
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    x = intent.getFloatExtra("x", 0);
                    y = intent.getFloatExtra("y", 0);
                    if (x == 0 && y == 0) {
                        inputBtn.setEnabled(false);
                        inputBtn.setBackground(getResources().getDrawable(R.drawable.button_cancle));
                    } else {
                        inputBtn.setEnabled(true);
                        inputBtn.setBackground(getResources().getDrawable(R.drawable.button_ok));

                        planData_ch.setText("입력 완료");
                        planData_ch.setChecked(true);
                    }
//                    Log.e("ActivityResultLauncher", " " + intent.getFloatExtra("x", 0));
//                    Log.e("ActivityResultLauncher", " " + intent.getFloatExtra("y", 0));
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_camtest);

        loading_progress = new Loading_Progress(this);
        loading_progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_progress.setCancelable(false);

        //token
        SharedPreferences preferences = getSharedPreferences("tokenSave", Context.MODE_PRIVATE);
        int URL_flag = preferences.getInt("URL_flag", 0);
        int inspect_URL = preferences.getInt("inspect_URL", 0);
//        Log.e("URL_flag : ", "" + URL_flag);
//        Log.e("inspect_URL : ", "" + inspect_URL);

        service = RetrofitClient.getClient(URL_flag).create(ApiService.class);

        Toast.makeText(this, "분석이 완료 되었습니다.", Toast.LENGTH_LONG).show();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String sParam = bundle.getString("PARAM_KEY");
        type = bundle.getInt("type", 0);
        ch4 = bundle.getInt("ch4", 1);
        plan_list = (ArrayList<PlanResponse>) bundle.getSerializable("plan_list");
        plan_id = bundle.getString("plan_id");
        CrackData requestData = (CrackData) bundle.getSerializable("web_bundle");


        Log.e("plan_id : ", "" + plan_id);
        Log.e("type : ", "" + type);
        Log.e("sParam : ", sParam);

        //읽고 쓰는 부분 가능하도록 퍼미션 허용
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        //읽고 쓰는 부분 가능하도록 퍼미션 허용
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        }

        graphicsView = findViewById(R.id.graphicsView);

        if (type != 2) {
            btn_download = findViewById(R.id.btn_download);
            btn_download.setVisibility(View.GONE);

            btn_input = findViewById(R.id.btn_input);
            btn_input.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    showCrackDialog(f_absolutePath, type, requestData);
                }
            });

        } else {
            btn_download = findViewById(R.id.btn_download);
            btn_download.setOnClickListener(v -> {
                if (graphicsView.GetExistView()) {
                    downloadImage(DownloadBitmap, sParam);
                    Toast.makeText(context, "사진이 저장 되었습니다.", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("btn_download", "저장할 이미지가 없습니다.");
                    Toast.makeText(context, "저장할 이미지가 없습니다.", Toast.LENGTH_LONG).show();
                }
            });

            btn_input = findViewById(R.id.btn_input);
            btn_input.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Log.e("btn_input", "사전 촬영 데이터 저장은 준비중입니다.");
                    Toast.makeText(context, "사전 촬영 데이터 저장은 준비중입니다.", Toast.LENGTH_SHORT).show();
//                    showCrackDialog(f_absolutePath, type, requestData);
                }
            });
        }


        btn_recapture = findViewById(R.id.btn_recapture);
        btn_recapture.setOnClickListener(v -> {
            finish();
        });
//        Log.e("sParam", sParam); file_name;

        //분석 이미지 주소
//        if (inspect_URL == 2) {
//            analysis_url = "http://116.39.158.135:12000";
//        } else if (inspect_URL == 3) {
//            analysis_url = "http://211.41.73.28:12000";
//        }

        analysis_url = "http://211.41.73.29:12000";
        String sPP = analysis_url + "/static/masked/" + sParam;
        Log.e("sPP : ", sPP);

        double crackSize = Double.parseDouble(requestData.getSize());

//        Log.e("crackSize: ", "" + crackSize);
        if (crackSize >= 0.3) {//위험
            border_type = 2;
        } else if (crackSize >= 0.2 && crackSize < 0.3) {//주의
            border_type = 1;
        } else {//안전
            border_type = 0;
        }

        GlideApp.with(this).asBitmap().load(sPP)
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        File f = createFileFromBitmap(resource, sParam);
                        f_absolutePath = f.getAbsolutePath();
//                        Log.e("f_absolutePath: ", "" + f_absolutePath);
//                Log.e("border_type: ", "" + border_type);

                        DownloadBitmap = resource;

                        Bitmap borderBitmap = addBorder(resource, 3, border_type);

                        graphicsView.ReSetDraw();
                        graphicsView.PutImage(borderBitmap, 0);
                        graphicsView.invalidate();
//                clearApplicationCache(null);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.e("onPause: ", "onPause");
//    }
//
//    @Override
//    protected void onStop() {
//        Log.e("onStop: ", "onStop");
//        super.onStop();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.e("onResume: ", "onResume");
//    }
//
//    @Override
//    protected void onRestart() {
//        Log.e("onRestart: ", "onRestart");
//        super.onRestart();
//    }

    //비트맵 border 추가
    private Bitmap addBorder(Bitmap bitmap, int borderSize, int type) {
        Bitmap bitmapWithBorder = Bitmap.createBitmap(bitmap.getWidth() + borderSize * 2, bitmap.getHeight() + borderSize * 2, bitmap.getConfig());
        Canvas canvas = new Canvas(bitmapWithBorder);

        if (type == 0) {//안전
            canvas.drawColor(Color.GREEN);
        } else if (type == 1) {//주의
            canvas.drawColor(Color.YELLOW);
        } else if (type == 2) {//위험
            canvas.drawColor(Color.RED);
        } else {//판단 불가 x, 원본 return
            return bitmap;
        }
        canvas.drawBitmap(bitmap, (float) borderSize, (float) borderSize, null);
        return bitmapWithBorder;
    }

    private void showCrackDialog(String f_path, int type, CrackData requestData) {
        dialog1 = new Dialog(context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.dialog_crackinput);
        dialog1.setCancelable(false);
        dialog1.show();

        // Dialog 사이즈 조절 하기
        WindowManager.LayoutParams params = dialog1.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog1.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        //token
        SharedPreferences preferences = getSharedPreferences("tokenSave", Context.MODE_PRIVATE);
        String Token = "Bearer " + preferences.getString("token", "null");

        //user input data
        CrackData send_data = new CrackData();
        send_data.setImg(f_path);

//        Log.e("f_path: ", send_data.getImg());

        crack_position_sp = dialog1.findViewById(R.id.crack_position_sp);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.subjectPosition, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        crack_position_sp.setAdapter(adapter);

        crack_position_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                send_data.setPosition(String.valueOf(adapterView.getAdapter().getItem(i)));
//                Log.e("dialog_spinner", String.valueOf(adapterView.getAdapter().getItem(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                send_data.setPosition(String.valueOf(adapterView.getAdapter().getItem(0)));
            }
        });


        if (type != 2) {
            planListBtn = dialog1.findViewById(R.id.planListBtn);
            planListBtn.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    service.get_plan(Token, plan_id).enqueue(new Callback<PlanResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<PlanResponse> call, @NonNull Response<PlanResponse> response) {
                            PlanResponse result = response.body();
                            if (response.isSuccessful() && result != null) {
                                Log.e("get_plan: ", new Gson().toJson(result));
                                Intent intent = new Intent(getApplicationContext(), PlanListImgActivity.class);
                                intent.putExtra("type", 1);
                                intent.putExtra("plan_img", result.getImg());
                                mStartForResult.launch(intent);
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
            });
        } else {
            crack_layout2 = dialog1.findViewById(R.id.crack_layout2);
            crack_layout2.setVisibility(View.GONE);
        }

        crackSize_edt = dialog1.findViewById(R.id.crackSize_edt);
        crackLength_edt = dialog1.findViewById(R.id.crackLength_edt);
        crack_etcEdt = dialog1.findViewById(R.id.crack_etcEdt);

        size_ch = dialog1.findViewById(R.id.size_ch);
        length_ch = dialog1.findViewById(R.id.length_ch);
        planData_ch = dialog1.findViewById(R.id.planData_ch);

        size_ch.setVisibility(View.GONE);
        length_ch.setVisibility(View.GONE);

        if (ch4 == 0) {
            crackSize_edt.setText(requestData.getSize());
            crackSize_edt.setEnabled(true);
        } else if (ch4 == 1) {
            crackSize_edt.setText(requestData.getSize());
            crackSize_edt.setEnabled(false);
        }
        crackLength_edt.setText(requestData.getLength());
        crackLength_edt.setEnabled(true);

        inputBtn = dialog1.findViewById(R.id.inputBtn);
        inputBtn.setEnabled(false);
        inputBtn.setBackground(getResources().getDrawable(R.drawable.button_cancle));
        inputBtn.setOnClickListener(view -> {
            String crack_size;
            String crack_length;
            String crack_etc;

            crack_size = crackSize_edt.getText().toString();
            crack_length = crackLength_edt.getText().toString();

            if (crack_etcEdt.getText().toString().equals("")) {
                crack_etc = "-";
//                Log.e("crack_etcEdt", "1");
            } else {
                crack_etc = crack_etcEdt.getText().toString();
//                Log.e("crack_etcEdt", "2");
            }

            send_data.setSize(crack_size);
            send_data.setLength(crack_length);
//            send_data.setCrack_box_size(crack_area);
            send_data.setEtc(crack_etc);

            if (type == 1) {
                Log.e("수정: ", "type: " + type);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                alertDialog = builder.setMessage("수정 하시겠습니까?")
                        .setPositiveButton("확인", (dialogInterface, i) -> {
                            //데이터 전송
                            SendCrackData(Token, send_data, type);
                            alertDialog.dismiss();
                            dialog1.dismiss();
                            finish();
                        })
                        .setNegativeButton("취소", (dialogInterface, i) -> {
                            alertDialog.dismiss();
                        })
                        .create();
                alertDialog.show();
            } else if (type == 2) {
                //사전 촬영
                SendCrackData(Token, send_data, type);
                dialog1.dismiss();
                finish();
            } else {
                //데이터 전송
                SendCrackData(Token, send_data, type);
                dialog1.dismiss();
                finish();
            }
        });

        cancel_Btn = dialog1.findViewById(R.id.cancel_Btn);
        cancel_Btn.setOnClickListener(view -> {
            dialog1.dismiss();
        });
    }

    @SuppressLint("CheckResult")
    private void SendCrackData(String token, CrackData data, int type) {
        Bundle bundle = getIntent().getExtras();
        CrackData requestData = (CrackData) bundle.getSerializable("web_bundle");
        Log.e("getZone:::", requestData.getZone());

        Log.e("SendCrackData", "start");
        CrackRunnable crackRunnable = new CrackRunnable();
        crackRunnable.setToken(token);
        crackRunnable.setData(data);
        crackRunnable.setType(type);
        crackRunnable.setRequestData(requestData);
        Thread thread = new Thread(crackRunnable);

        thread.start();
        loading_progress.show();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        loading_progress.dismiss();
        Log.e("SendCrackData", Thread.currentThread().getName() + " end");
    }

    private class CrackRunnable implements Runnable {
        String token;
        int type;
        CrackData data;
        CrackData requestData;

        public void setToken(String token) {
            this.token = token;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setData(CrackData data) {
            this.data = data;
        }

        public void setRequestData(CrackData requestData) {
            this.requestData = requestData;
        }

        @Override
        public void run() {
            if (type == 1) {//update case
                Log.e("CrackRunnable: ", "run(): " + "update coordinate");
                try {
                    ResponseMessage result = service.patch_coordinate(token, String.valueOf(requestData.getCoordinateid()), String.valueOf(Math.round(x)), String.valueOf(Math.round(y))).execute().body();
                    Log.e("onResponse", new Gson().toJson(result));
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("onFailure", e.getMessage());
                }
                //update crackInfo
                thread3();

            } else if (type == 2) {//pre_capture case
                thread4();

            } else {//input case
                HashMap<String, RequestBody> body = new HashMap<>();

                Log.e("plan_id ", plan_id);

                body.put("projectid", RequestBody.create(requestData.getProjectid(), MediaType.parse("text/plain")));
                body.put("planid", RequestBody.create(plan_id, MediaType.parse("text/plain")));
                body.put("x", RequestBody.create(String.valueOf(Math.round(x)), MediaType.parse("text/plain")));
                body.put("y", RequestBody.create(String.valueOf(Math.round(y)), MediaType.parse("text/plain")));

                try {
                    ResponseMessage result = service.post_coordinate(token, body).execute().body();
                    if (result != null) {
                        Log.e("onResponse", new Gson().toJson(result));
                        if (result.getMessage().equals("success")) {
                            thread2();
                        } else {
                            Log.e("onResponse", "좌표 응답 에러");
                            runOnUiThread(() -> {
                                        Toast.makeText(context, "좌표 데이터 등록 오류. 재시도하세요.", Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }
                    }

                } catch (IOException e) {
                    Log.e("onFailure", e.getMessage());
                    e.printStackTrace();
                }
            }

            //cache clear
//            clearApplicationCache(null);
        }

        public void thread2() {//input crackInfo
            Log.e("thread2()", "getProjectid: " + requestData.getProjectid());

            File f = new File(data.getImg());

            RequestBody requestBody = RequestBody.create(f, MediaType.parse("multipart/form-data"));
            MultipartBody.Part f_img = MultipartBody.Part.createFormData("img", f.getName(), requestBody);

            Map<String, RequestBody> body = new HashMap<>();
            body.put("projectid", RequestBody.create(requestData.getProjectid(), MediaType.parse("text/plain")));
            body.put("planid", RequestBody.create(plan_id, MediaType.parse("text/plain")));
            body.put("x", RequestBody.create(String.valueOf(Math.round(x)), MediaType.parse("text/plain")));
            body.put("y", RequestBody.create(String.valueOf(Math.round(y)), MediaType.parse("text/plain")));
            body.put("zone", RequestBody.create(requestData.getZone(), MediaType.parse("text/plain")));
            body.put("position", RequestBody.create(data.getPosition(), MediaType.parse("text/plain")));
            body.put("size", RequestBody.create(data.getSize(), MediaType.parse("text/plain")));
            body.put("length", RequestBody.create(data.getLength(), MediaType.parse("text/plain")));
            body.put("type", RequestBody.create(requestData.getType(), MediaType.parse("text/plain")));
            body.put("etc", RequestBody.create(data.getEtc(), MediaType.parse("text/plain")));
            body.put("auto_analyze", RequestBody.create(requestData.getAuto_analyze(), MediaType.parse("text/plain")));

            try {
                CrackResponse result = service.post_crack_insert(token, f_img, body).execute().body();

                Log.e("onResponse", new Gson().toJson(result));
                runOnUiThread(() -> {
                            Toast.makeText(context, "등록 완료", Toast.LENGTH_SHORT).show();
                        }
                );
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("onFailure", e.getMessage());
                runOnUiThread(() -> {
                            Toast.makeText(context, "등록 실패", Toast.LENGTH_SHORT).show();
                        }
                );
            }
        }

        public void thread3() {//update crackInfo
            Log.e("CrackRunnable: ", "thread3(): " + "update crack_data");
            File f = new File(data.getImg());

            RequestBody requestBody = RequestBody.create(f, MediaType.parse("multipart/form-data"));
            MultipartBody.Part f_img = MultipartBody.Part.createFormData("img", f.getName(), requestBody);

            Map<String, RequestBody> body = new HashMap<>();
            //현재 면적 컬럼 없어서 빠진 상태로 보내는 중
            body.put("crackid", RequestBody.create(String.valueOf(requestData.getCrackid()), MediaType.parse("text/plain")));
            body.put("zone", RequestBody.create(requestData.getZone(), MediaType.parse("text/plain")));
            body.put("position", RequestBody.create(data.getPosition(), MediaType.parse("text/plain")));
            body.put("type", RequestBody.create(requestData.getType(), MediaType.parse("text/plain")));
            body.put("size", RequestBody.create(data.getSize(), MediaType.parse("text/plain")));
            body.put("length", RequestBody.create(data.getLength(), MediaType.parse("text/plain")));
            body.put("etc", RequestBody.create(data.getEtc(), MediaType.parse("text/plain")));
            body.put("auto_analyze", RequestBody.create(requestData.getAuto_analyze(), MediaType.parse("text/plain")));

            try {
                ResponseMessage result = service.post_crack_update(token, f_img, body).execute().body();
                Log.e("onResponse", new Gson().toJson(result));
                runOnUiThread(() -> {
                            Toast.makeText(context, "데이터 수정 완료", Toast.LENGTH_SHORT).show();
                        }
                );
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("onFailure", e.getMessage());
                runOnUiThread(() -> {
                            Toast.makeText(context, "데이터 수정 실패", Toast.LENGTH_SHORT).show();
                        }
                );
            }
        }

        public void thread4() {//input pre_captureInfo
            Log.e("thread4()", "getProjectid: " + requestData.getProjectid());

            File f = new File(data.getImg());

            RequestBody requestBody = RequestBody.create(f, MediaType.parse("multipart/form-data"));
            MultipartBody.Part f_img = MultipartBody.Part.createFormData("img", f.getName(), requestBody);

            Map<String, RequestBody> body = new HashMap<>();
            body.put("projectid", RequestBody.create(requestData.getProjectid(), MediaType.parse("text/plain")));
//            body.put("planid", RequestBody.create(plan_id, MediaType.parse("text/plain")));
//            body.put("x", RequestBody.create(String.valueOf(Math.round(x)), MediaType.parse("text/plain")));
//            body.put("y", RequestBody.create(String.valueOf(Math.round(y)), MediaType.parse("text/plain")));
            body.put("zone", RequestBody.create(requestData.getZone(), MediaType.parse("text/plain")));
            body.put("position", RequestBody.create(data.getPosition(), MediaType.parse("text/plain")));
            body.put("size", RequestBody.create(data.getSize(), MediaType.parse("text/plain")));
            body.put("length", RequestBody.create(data.getLength(), MediaType.parse("text/plain")));
            body.put("type", RequestBody.create(requestData.getType(), MediaType.parse("text/plain")));
            body.put("etc", RequestBody.create(data.getEtc(), MediaType.parse("text/plain")));
            body.put("auto_analyze", RequestBody.create(requestData.getAuto_analyze(), MediaType.parse("text/plain")));

            runOnUiThread(() -> {
                        Toast.makeText(context, "사전 촬영 데이터 저장은 준비중입니다.", Toast.LENGTH_SHORT).show();
                    }
            );

//            try {
//                CrackResponse result = service.post_crack_insert(token, f_img, body).execute().body();
//
//                Log.e("onResponse", new Gson().toJson(result));
//                runOnUiThread(() -> {
//                            Toast.makeText(context, "데이터 저장 완료", Toast.LENGTH_SHORT).show();
//                        }
//                );
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("onFailure", e.getMessage());
//                runOnUiThread(() -> {
//                            Toast.makeText(context, "데이터 저장 실패", Toast.LENGTH_SHORT).show();
//                        }
//                );
//            }
        }
    }

    private File createFileFromBitmap(Bitmap b, String f_name) {
        File f = new File(this.getCacheDir(), f_name);

        try {
            //create a file to write bitmap data
            f.createNewFile();
            Log.e("분석 완료 이미지 저장: ", "cache: " + f_name);
            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 75, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    private void downloadImage(Bitmap b, String f_name) {
        FileOutputStream fos;

        Log.e("f_name::: ", f_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, f_name);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);

            ContentResolver contentResolver = getContentResolver();
            Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri item = contentResolver.insert(collection, values);

            try {
                // Uri(item)의 위치에 파일을 생성해준다.
                ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);
                if (pdf == null) {

                } else {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    fos = new FileOutputStream(pdf.getFileDescriptor());
                    fos.write(bitmapdata);
                    fos.close();
                    pdf.close();
                    contentResolver.update(item, values, null, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            contentResolver.update(item, values, null, null);
        } else {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), f_name);
            try {
                //create a file to write bitmap data
                f.createNewFile();
//            Log.e("이미지 저장: ", "Environment.DIRECTORY_PICTURES: " + f_name);
                Log.e("이미지 저장: ", "Environment.DIRECTORY_PICTURES: " + f.getAbsolutePath());
                //Convert bitmap to byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                byte[] bitmapdata = bos.toByteArray();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
                fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //cache clear
    private void clearApplicationCache(java.io.File dir) {
        if (dir == null)
            dir = getCacheDir();
        if (dir == null)
            return;
        java.io.File[] children = dir.listFiles();
        try {
            for (java.io.File child : children)
                if (child.isDirectory())
                    clearApplicationCache(child);
                else child.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
