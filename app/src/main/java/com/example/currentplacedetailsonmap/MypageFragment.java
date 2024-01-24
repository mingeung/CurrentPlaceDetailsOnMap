package com.example.currentplacedetailsonmap;

import static com.example.currentplacedetailsonmap.LoginActivity.KEY_USER_ID;
import static com.example.currentplacedetailsonmap.LoginActivity.PREF_NAME;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;



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

    private String getUserIdFromSharedPreferences() {
        // getActivity() 메서드를 사용하여 SharedPreferences 인스턴스 획득
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_ID, "");
    }


    // 갤러리 열기
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    private void uploadProfileImage(Bitmap bitmap) {
        OkHttpClient client = new OkHttpClient();
        // SharedPreferences에서 사용자 ID 읽어오기
        String userId = getUserIdFromSharedPreferences();


        //이미지를 Base64로 인코딩
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // 이미지 데이터를 담을 MultipartBody.Part 생성
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), byteArray);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "profile_image.png", requestBody);

        // 사용자 ID 데이터를 담을 RequestBody 생성
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), userId);
        Log.d("MypageFragment", "사용자 ID: " + userId);

        // 업로드할 데이터를 담은 MultipartBody 생성
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(filePart)
                .addFormDataPart("user_id", (userId))
                .build();


        // HTTP 요청 설정
        Request request = new Request.Builder()
                .url("http://172.10.5.162:80/uploadProfileImage")
                .post(multipartBody)
                .build();

        //http 요청 설정

//        String jsonData = String.format("{\"user_id\":\"%s\", \"file\": \"%s\"}", userId, encodedImage);
//        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), jsonData);
//        Request request = new Request.Builder()
//                .url("http://172.10.7.13:80/uploadProfileImage")
//                .post(formBody)
//                .build();
//        // JSON 형식으로 데이터 구성
//        JSONObject jsonBody = new JSONObject();
//        try {
//            jsonBody.put("user_id", userId);
//            jsonBody.put("file", encodedImage);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        // HTTP 요청 설정
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());
//        Request request = new Request.Builder()
//                .url("http://172.10.7.13:80/uploadProfileImage")
//                .post(requestBody)
//                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 업로드 성공 시 서버 응답 처리
                    String responseData = response.body().string();
                    Log.d("Upload Response", responseData);
                    // 서버에서 받은 응답을 파싱하여 처리할 수 있음
                }
            }
        });
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
                // 이미지 업로드
                uploadProfileImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
