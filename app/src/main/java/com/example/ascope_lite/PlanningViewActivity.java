package com.example.ascope_lite;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ascope_lite.Planlist.PlanListDTO;
import com.example.ascope_lite.Planlist.PlanResponse;
import com.example.ascope_lite.Project.ProjectGetListResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

//@SuppressLint("SourceLockedOrientationActivity")
public class PlanningViewActivity extends AppCompatActivity {
    ApiService service;
    Context context = this;

    AlertDialog dialog;
    Button btn_search, btn_input, btn_back_content;
    Spinner sp_floor;
    com.example.ascope_lite.CamTest_v01.DrawView graphicView;

    Uri update_planImgUri;
    String facilityid;
    String result_floor;
    String ab_path;
    String Token;

    ArrayList<PlanResponse> plan_list = new ArrayList<>();
    List<PlanListDTO> planList = new ArrayList<>();


    //activity 종료 시 결과 값 받는 ActivityResultLauncher (startActivityForResult deprecated 대체)
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
//                Log.e("result.getResultCode()", result.getResultCode() + "");
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    update_planImgUri = intent.getData();
                    ab_path = getFileName(update_planImgUri);
                    String mimeType = getContentResolver().getType(update_planImgUri);
//                    Log.e("ab_path: ", ab_path);
//                    Log.e("mimeType: ", mimeType);
                    if (!mimeType.endsWith("/jpeg")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        dialog = builder.setMessage(".jpg 형식의 파일이 아닙니다.")
                                .setNegativeButton("취소", (dialogInterface, i) -> {
                                    dialog.dismiss();
                                })
                                .create();
                        dialog.show();

                    } else {
                        GlideApp.with(context).asBitmap().load(update_planImgUri)
                                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                        Log.e("onResourceReady:", "getHeight: " + resource.getHeight());
//                                        Log.e("onResourceReady:", "getWidth: " + resource.getWidth());
                                        if (resource.getHeight() >= resource.getWidth()) {
                                            Toast.makeText(getApplicationContext(), "가로가 더 긴 이미지를 사용하세요.", Toast.LENGTH_SHORT).show();

                                        } else {
                                            createFileFromBitmap(resource, ab_path);
                                            graphicView.ReSetDraw();
                                            graphicView.PutImage(resource, 0);
                                            graphicView.invalidate();

                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            dialog = builder.setMessage("도면을 등록/수정 하시겠습니까?")
                                                    .setCancelable(false)
                                                    .setPositiveButton("확인", (dialogInterface, i) -> {
                                                        Map<String, RequestBody> body = new HashMap<>();
                                                        File f = new File(context.getCacheDir(), ab_path);
                                                        RequestBody requestBody = RequestBody.create(f, MediaType.parse("multipart/form-data"));
                                                        MultipartBody.Part f_img = MultipartBody.Part.createFormData("img", f.getName(), requestBody);

                                                        body.put("facilityid", RequestBody.create(facilityid, MediaType.parse("text/plain")));
                                                        body.put("floor", RequestBody.create(result_floor, MediaType.parse("text/plain")));

//                                                        service.post_plan(Token, f_img, body).enqueue(new Callback<PlanUpdateResponse>() {
//                                                            @Override
//                                                            public void onResponse(@NonNull Call<PlanUpdateResponse> call, @NonNull Response<PlanUpdateResponse> response) {
//                                                                PlanUpdateResponse result = response.body();
//                                                                Log.e("onResponse: ", new Gson().toJson(result));
//                                                                Toast.makeText(getApplicationContext(), "해당 층의 도면이 등록/수정 되었습니다.", Toast.LENGTH_SHORT).show();
//                                                                clearApplicationCache(null);
//                                                            }
//
//                                                            @Override
//                                                            public void onFailure(@NonNull Call<PlanUpdateResponse> call, @NonNull Throwable t) {
//                                                                Toast.makeText(getApplicationContext(), "도면 등록/수정 오류. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
//                                                                Log.e("onFailure: ", t.getMessage());
//                                                                clearApplicationCache(null);
//                                                            }
//                                                        });
                                                    })
                                                    .setNegativeButton("취소", (dialogInterface, i) -> {
                                                        clearApplicationCache(null);
                                                        dialog.dismiss();
                                                    })
                                                    .create();
                                            dialog.show();
                                        }
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 세로 고정
        setContentView(R.layout.activity_planningview);

        SharedPreferences preferences = getSharedPreferences("tokenSave", MODE_PRIVATE);
        Token = "Bearer " + preferences.getString("token", "null");
        int URL_flag = preferences.getInt("URL_flag", 0);

        service = RetrofitClient.getClient(URL_flag).create(ApiService.class);

        Bundle bundle = getIntent().getExtras();
        plan_list = (ArrayList<PlanResponse>) bundle.getSerializable("plan_list");
        ProjectGetListResponse data = (ProjectGetListResponse) bundle.getSerializable("planning_bundle");

        /*
        facilityid = data.getFacilityid();

        graphicView = findViewById(R.id.graphicsView);
        graphicView.setVisibility(View.GONE);
        sp_floor = findViewById(R.id.sp_floor);

        //floor spinner
        int up_size = Integer.parseInt(data.getFacilityfloorup());
        int down_size = Integer.parseInt(data.getFacilityfloordown());
        int size = up_size + down_size;

        String[] up = new String[up_size];
        for (int i = 0; i < up_size; i++)
            up[i] = String.valueOf(i + 1);

        String[] down = new String[down_size];
        for (int i = 0; i < down_size; i++)
            down[i] = String.valueOf(-i - 1);

        String[] floor = new String[size];
        System.arraycopy(up, 0, floor, 0, up_size);
        System.arraycopy(down, 0, floor, up_size, down_size);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, floor);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_floor.setAdapter(adapter);

        sp_floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                result_floor = String.valueOf(adapterView.getAdapter().getItem(i));
                Log.e("onItemSelected1", String.valueOf(adapterView.getAdapter().getItem(i)));
                Log.e("onItemSelected2", String.valueOf(adapterView.getItemAtPosition(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                result_floor = String.valueOf(adapterView.getAdapter().getItem(0));
                Log.e("onNothingSelected", String.valueOf(adapterView.getAdapter().getItem(0)));
//                Toast.makeText(getApplicationContext(), "onNothingSelected", Toast.LENGTH_SHORT).show();
            }
        });

        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(v -> {
            Log.e("btn_search: ", result_floor);
            service.get_planList(Token, facilityid).enqueue(new Callback<PlanListResponse>() {
                @Override
                public void onResponse(@NonNull Call<PlanListResponse> call, @NonNull Response<PlanListResponse> response) {
                    PlanListResponse result = response.body();
                    if (response.isSuccessful() && result != null) {
                        Log.e("onResponse: ", new Gson().toJson(result));

                        for (int i = 0; i < result.getResults().size(); i++) {
                            if (result_floor.equals(result.getResults().get(i).getFloor())) {
                                planList.add(result.getResults().get(i));
                            }
                        }

                        if (planList.size() != 0) {
                            GlideApp.with(context).asBitmap().load(planList.get(0).getImg())
                                    .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            graphicView.ReSetDraw();
                                            graphicView.PutImage(resource, 0);
                                            graphicView.invalidate();
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {

                                        }
                                    });
                            graphicView.setVisibility(View.VISIBLE);
                            Log.e("onResponse: ", planList.get(0).getImg());
                        } else {
                            Toast.makeText(getApplicationContext(), "해당 층에 등록된 도면이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        planList.clear();

                    } else {
                        Toast.makeText(getApplicationContext(), "해당 층에 등록된 도면이 없습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("onResponse: ", "해당 층 도면 데이터 없음");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PlanListResponse> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(), "5xx_error.", Toast.LENGTH_SHORT).show();
                    Log.e("onFailure: ", t.getMessage());
                }
            });
        });

        btn_input = findViewById(R.id.btn_input);
        btn_input.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.setType("image/*");
            mStartForResult.launch(intent);
        });

        btn_back_content = findViewById(R.id.btn_back_content);
        btn_back_content.setOnClickListener(v -> {
            finish();
        });
        */
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

    /*
    //MediaStore uri to realPath
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    */

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
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
