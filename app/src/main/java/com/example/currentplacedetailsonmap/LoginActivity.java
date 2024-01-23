package com.example.currentplacedetailsonmap;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 로그인 화면의 구현 내용을 추가할 수 있습니다.
    }

    // "로그인" 버튼을 클릭했을 때 호출되는 메서드
    public void onLoginButtonClick(View view) {
        // 사용자 입력 데이터 가져오기
        String userId = ((EditText) findViewById(R.id.idInput)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordInput)).getText().toString();

        // OkHttp를 사용하여 백엔드로 데이터 전송
        OkHttpClient client = new OkHttpClient();

        String url = "http://172.10.7.13:80/login";
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", userId)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                // 서버 응답 처리
                if (response.isSuccessful()) {
                    // 성공적으로 저장됨
                    // 원하는 처리를 추가하세요
                    Log.d("LoginActivity", "서버 응답: 성공");
                } else {
                    // 실패 시 처리
                    // 원하는 처리를 추가하세요
                    Log.e("LoginActivity", "서버 응답: 실패");
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 통신 실패 시 처리
                // 원하는 처리를 추가하세요
                Log.e("LoginActivity", "서버 통신 실패", e);
                Log.e("LoginActivity", "에러 메시지: " + e.getMessage());
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
