package com.example.currentplacedetailsonmap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    // SharedPreferences의 파일명
    public static final String PREF_NAME = "MyPrefs";
    // SharedPreferences에 저장할 키 값
    public static final String KEY_USER_ID = "userId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    // "로그인" 버튼을 클릭했을 때 호출되는 메서드
    public void onLoginButtonClick(View view) {
        // 사용자 입력 데이터 가져오기
        String user_id = ((EditText) findViewById(R.id.idInput)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordInput)).getText().toString();

        // OkHttp를 사용하여 백엔드로 데이터 전송
        OkHttpClient client = new OkHttpClient();

        String url = "http://172.10.5.162:80/login";

        String jsonData = String.format("{\"user_id\":\"%s\", \"password\": \"%s\"}", user_id, password);

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), jsonData);
        Request request = new Request.Builder()
                .url("http://172.10.5.162:80/login")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 서버 응답 처리
                if (response.isSuccessful()) {
                    // 성공적으로 저장됨
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String message = jsonResponse.getString("message");
                        if (response.isSuccessful()) {
                            // 로그인 성공 시
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                // 사용자 ID를 SharedPreferences에 저장
                                saveUserIdToSharedPreferences(user_id);

                                // MainActivity로 이동하는 Intent 생성
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                // MainActivity로 이동
                                startActivity(intent);
                                // LoginActivity 종료 (뒤로 가기 시 LoginActivity로 가지 않도록)
                                finish();
                            });
                        } else {
                            // 로그인 실패 시
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 실패 시 처리
                    Log.e("LoginActivity", "서버 응답: 실패");
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 통신 실패 시 처리
                // 원하는 처리를 추가하세요
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "서버 통신 실패", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // SharedPreferences에 사용자 ID 저장하는 메서드
    private void saveUserIdToSharedPreferences(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }
}