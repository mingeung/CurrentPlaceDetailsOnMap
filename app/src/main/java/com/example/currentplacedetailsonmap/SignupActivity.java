package com.example.currentplacedetailsonmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
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
        String userId = ((EditText) findViewById(R.id.signupIdInput)).getText().toString();
        String email = ((EditText) findViewById(R.id.signupEmailInput)).getText().toString();
        String nickname = ((EditText) findViewById(R.id.signupNicknameInput)).getText().toString();
        String password = ((EditText) findViewById(R.id.signupPasswordInput)).getText().toString();

        // OkHttp를 사용하여 백엔드로 데이터 전송
        OkHttpClient client = new OkHttpClient();

        // Replace "your_server_url" with your server's URL and "/signup" with your signup route
        String url = "https://172.10.7.13:80/signup";

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", userId)
                .add("email", email)
                .add("nickname", nickname)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(url)
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
                // 원하는 처리를 추가하세요
                runOnUiThread(() -> {
                    Toast.makeText(SignupActivity.this, "서버 통신 실패", Toast.LENGTH_SHORT).show();
                });
            }
        });

        // MainActivity로 이동하는 Intent 생성
        Intent intent = new Intent(this, MainActivity.class);
        // 다른 데이터를 전달하려면 여기에 추가 가능
        // 예: intent.putExtra("key", value);
        // ...

        // MainActivity로 이동
        startActivity(intent);
    }
}
