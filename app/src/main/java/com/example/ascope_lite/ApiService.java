package com.example.ascope_lite;

import com.example.ascope_lite.CrackInspect.CrackAutoResponse;
import com.example.ascope_lite.CrackInspect.CrackListResponse;
import com.example.ascope_lite.CrackInspect.CrackResponse;
import com.example.ascope_lite.Login.LoginData;
import com.example.ascope_lite.Login.LoginResponse;
import com.example.ascope_lite.Planlist.PlanResponse;
import com.example.ascope_lite.Project.ProjectGetListResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface ApiService {
    //로그인
    @POST("/api/members/login/")
    Call<LoginResponse> login_post(@Body LoginData data);

    //등록 시설물 조회
    @GET("/api/project/getList/")
    Call<List<ProjectGetListResponse>> get_project_list(@Header("Authorization") String token);

    //분석 서버(촬영 분석)
    @Multipart
    @POST("/uploader2")
    Call<CrackAutoResponse> post_server_auto(@Part MultipartBody.Part img, @PartMap Map<String, RequestBody> body);

    //균열 정보 등록
    @Multipart
    @POST("/api/project/create/crack/")
    Call<CrackResponse> post_crack_insert(@Header("Authorization") String token, @Part MultipartBody.Part img, @PartMap Map<String, RequestBody> body);

    //균열 정보 수정
    @Multipart
    @PATCH("/api/project/update/crack/")
    Call<ResponseMessage> post_crack_update(@Header("Authorization") String token, @Part MultipartBody.Part img, @PartMap Map<String, RequestBody> body);

    //균열 정보 리스트 조회
    @GET("/api/project/getList/crack/")
    Call<List<CrackListResponse>> get_crack_list(@Header("Authorization") String token, @Query("projectid") String projectid, @Query("planid") String planid);

    //균열 정보 조회
    @GET("/api/project/get/crack/")
    Call<CrackListResponse> get_crack(@Header("Authorization") String token, @Query("crackid") String crackid);

    //균열 정보 삭제(좌표 데이터 일괄 삭제)
    @DELETE("/api/project/delete/crack/")
    Call<ResponseMessage> delete_crack(@Header("Authorization") String token, @Query("crackid") String crackid);

    //조사 종료
    @GET("/api/project/end/inspection/")
    Call<ResponseMessage> get_inspection_end(@Header("Authorization") String token, @Query("projectid") String projectid);

    //도면 리스트 조회
    @GET("/api/project/getList/plan/")
    Call<List<PlanResponse>> get_planList(@Header("Authorization") String token, @Query("projectid") String projectid);

    //도면 조회
    @GET("/api/project/get/plan/")
    Call<PlanResponse> get_plan(@Header("Authorization") String token, @Query("planid") String planid);

    //좌표 생성
    @Multipart
    @POST("/api/project/create/coordinate/")
    Call<ResponseMessage> post_coordinate(@Header("Authorization") String token, @PartMap Map<String, RequestBody> data);

    //좌표 수정
    @FormUrlEncoded
    @PATCH("/api/project/update/coordinate/")
    Call<ResponseMessage> patch_coordinate(@Header("Authorization") String token,
                                           @Field("coordinateid") String coordinateid,
                                           @Field("x") String x,
                                           @Field("y") String y);

    //사용자 정보 조회
    @GET("/api/members/get/info/")
    Call<UserResponse> get_user_info(@Header("Authorization") String token);

    /////////////////////////////////////////////////////////////////////////////////////////////////
}