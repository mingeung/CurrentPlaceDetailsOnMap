package com.example.currentplacedetailsonmap;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 로그인 화면의 구현 내용을 추가할 수 있습니다.
    }
    // "로그인" 버튼을 클릭했을 때 호출되는 메서드
    public void onLoginButtonClick(View view) {
        // MainActivity로 이동하는 Intent 생성
        Intent intent = new Intent(this, MainActivity.class);
        // 다른 데이터를 전달하려면 여기에 추가 가능
        // 예: intent.putExtra("key", value);
        // ...

        // MainActivity로 이동
        startActivity(intent);
    }
}
