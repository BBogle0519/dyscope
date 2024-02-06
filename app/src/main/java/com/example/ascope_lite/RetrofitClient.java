package com.example.ascope_lite;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // localhost(에뮬레이터 10.0.2.2)
//    private final static String BASE_URL = "http://10.0.2.2:8000";

    //cafe24_1 (개발)
    private static String BASE_URL = "http://ascopelite.com";

    //cafe24_2 (배포)
//    private static String BASE_URL2 = "https://ascopetm.com";

    //분석 서버(개발)
    private static String inspect_URL = "http://ascopelite.com:12000";

//    //분석 서버(배포)
//    private static String inspect_URL2 = "http://211.41.73.28:12000";

//    //cafe24_1 (개발)
//    private static String BASE_URL = "http://dyscopekorea.com";
//
//    //cafe24_2 (배포)
//    private static String BASE_URL2 = "https://ascopetm.com";
//
//    //분석 서버(개발)
//    private static String inspect_URL = "http://116.39.158.135:12000";
//
//    //분석 서버(배포)
//    private static String inspect_URL2 = "http://211.41.73.28:12000";

    // 안전하지 않음으로 HTTPS를 통과합니다.
    //logging 위한 interceptor
    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            //logging 위한 interceptor
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(interceptor);
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder.build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private RetrofitClient() {
    }

    public static Retrofit getClient(int URL_flag) {
        String URL = null;
        if (URL_flag == 0) {
            URL = BASE_URL;
        } else if (URL_flag == 2) {
            URL = inspect_URL;
        }
//        if (URL_flag == 0) {
//            URL = BASE_URL;
//        } else if (URL_flag == 1) {
//            URL = BASE_URL2;
//        } else if (URL_flag == 2) {
//            URL = inspect_URL;
//        } else if (URL_flag == 3) {
//            URL = inspect_URL2;
//        }
        Log.e("URL::: ", URL);
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)  // 요청을 보낼 base url 설정.
                .client(getUnsafeOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson)) // JSON 파싱용 컨버터 GsonConverterFactory 추가
                .build();
        return retrofit;
    }

//    public static Retrofit getClient2() {
//        Gson gson = new GsonBuilder().setLenient().create();
//
//        //logging 위한 interceptor
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL2)  // 요청을 보낼 base url 설정.
//                .client(client)
//                .addConverterFactory(GsonConverterFactory.create(gson)) // JSON 파싱용 컨버터 GsonConverterFactory 추가
//                .build();
//        return retrofit;
//    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

//    public static String getBaseUrl2() {
//        return BASE_URL2;
//    }

//    public static void setBaseUrl2(String baseUrl2) {
//        BASE_URL2 = baseUrl2;
//    }
}