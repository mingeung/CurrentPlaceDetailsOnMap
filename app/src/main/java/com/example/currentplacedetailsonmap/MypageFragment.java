package com.example.currentplacedetailsonmap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class MypageFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView profileImageView;
    private TextView profileTitleTextView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private Button editButton;
    private Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mypage, container, false);

        // 위젯 초기화
        profileImageView = rootView.findViewById(R.id.profileImageView);
        profileTitleTextView = rootView.findViewById(R.id.profileTitleTextView);
        usernameTextView = rootView.findViewById(R.id.usernameTextView);
        emailTextView = rootView.findViewById(R.id.emailTextView);
        editButton = rootView.findViewById(R.id.editButton);
        logoutButton = rootView.findViewById(R.id.logoutButton);

        // 이미지뷰에 기본 프로필 이미지 설정
        profileImageView.setImageResource(R.drawable.default_profile_image);

        // 텍스트뷰에 가상의 사용자 정보 표시
        usernameTextView.setText("JohnDoe");
        emailTextView.setText("johndoe@example.com");

        // 이미지뷰 클릭 이벤트 처리 - 갤러리 열기
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // 수정 버튼 클릭 이벤트 처리
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정 버튼 클릭 시 동작
                Toast.makeText(getContext(), "수정 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        // 로그아웃 버튼 클릭 이벤트 처리
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그아웃 버튼 클릭 시 동작
                Toast.makeText(getContext(), "로그아웃 버튼 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    // 갤러리 열기
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // 갤러리에서 선택한 이미지 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            // 선택한 이미지를 이미지뷰에 설정
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                profileImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
