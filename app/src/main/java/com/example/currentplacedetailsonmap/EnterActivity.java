package com.example.currentplacedetailsonmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EnterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인 버튼을 클릭했을 때 로그인 페이지로 이동
                Intent loginIntent = new Intent(EnterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원가입 버튼을 클릭했을 때 회원가입 페이지로 이동
                Intent signupIntent = new Intent(EnterActivity.this, SignupActivity.class);
                startActivity(signupIntent);
            }
        });
    }
}
