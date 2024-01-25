package com.example.currentplacedetailsonmap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.textclassifier.TextSelection;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class SignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 회원가입 화면의 구현 내용을 추가할 수 있습니다.
    }

    public void onSignupButtonClick(View view) {
        // 사용자 입력 데이터 가져오기
        String user_id = ((EditText) findViewById(R.id.signupIdInput)).getText().toString();
        String email = ((EditText) findViewById(R.id.signupEmailInput)).getText().toString();
        String nickname = ((EditText) findViewById(R.id.signupNicknameInput)).getText().toString();
        String password = ((EditText) findViewById(R.id.signupPasswordInput)).getText().toString();


        // 이메일 유효성 검사
        if (!isValidEmail(email)) {
            Toast.makeText(this, "올바른 이메일 주소를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // OkHttp를 사용하여 백엔드로 데이터 전송
        OkHttpClient client = new OkHttpClient();

        // Replace "your_server_url" with your server's URL and "/signup" with your signup route
        String url = "http://172.10.5.162:80/signup";

        String jsonData = String.format("{\"user_id\":\"%s\", \"email\": \"%s\", \"nickname\":\"%s\", \"password\":\"%s\"}", user_id, email, nickname, password);
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), jsonData);
        Request request = new Request.Builder()
                .url("http://172.10.5.162:80/signup")
                .post(formBody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 서버 응답 처리
                if (response.isSuccessful()) {
                    // 성공적으로 저장됨
                    // 서버 응답이 성공한 경우 추가 처리
                    runOnUiThread(() -> {
                        Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                    });
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);

                    startActivity(intent);
                } else {
                    // 실패 시 처리
                    // 서버 응답이 실패한 경우 추가 처리
                    runOnUiThread(() -> {
                        Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                    });
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                // 통신 실패 시 처리
                runOnUiThread(() -> {
                    Toast.makeText(SignupActivity.this, "서버 통신 실패", Toast.LENGTH_SHORT).show();
                    Log.e("SignupActivity", "서버 통신 실패: " + e.getMessage());
                });
            }
        });

    }
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
