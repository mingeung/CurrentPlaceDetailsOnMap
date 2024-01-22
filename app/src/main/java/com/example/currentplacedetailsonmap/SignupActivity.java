package com.example.currentplacedetailsonmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 회원가입 화면의 구현 내용을 추가할 수 있습니다.
    }
    public void onSignupButtonClick(View view) {
        // MainActivity로 이동하는 Intent 생성
        Intent intent = new Intent(this, MainActivity.class);
        // 다른 데이터를 전달하려면 여기에 추가 가능
        // 예: intent.putExtra("key", value);
        // ...

        // MainActivity로 이동
        startActivity(intent);
    }
}
