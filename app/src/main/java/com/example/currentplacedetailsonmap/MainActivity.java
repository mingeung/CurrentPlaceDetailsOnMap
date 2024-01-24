package com.example.currentplacedetailsonmap;
import static com.example.currentplacedetailsonmap.LoginActivity.KEY_USER_ID;
import static com.example.currentplacedetailsonmap.LoginActivity.PREF_NAME;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import java.net.URI;
import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;
import android.os.AsyncTask;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONException;
import org.json.JSONObject;
public class MainActivity extends AppCompatActivity {
    private  static Socket mSocket;
    private LocationUpdateListener locationUpdateListener;


    //소켓을 반환하는 메소드
    public static Socket getmSocket() {
        return mSocket;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences에서 사용자 ID 읽어오기
        String userId = getUserIdFromSharedPreferences();
        Log.d("MainActivity", "사용자 ID: " + userId);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_maps) {
                selectedFragment = new MapsFragment();
            } else if (itemId == R.id.navigation_chat) {
                selectedFragment = new ChattingFragment();
            } else if (itemId == R.id.navigation_mypage) {
                selectedFragment = new MypageFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
        // Set the default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MapsFragment())
                .commit();
        new ConnectSocketIOAsyncTask(userId).execute();
    }
    // SharedPreferences에서 사용자 ID 읽어오는 메서드
    private String getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_ID, "");
    }
    // 내부 인터페이스로 LocationUpdateListener 선언
    public interface LocationUpdateListener {
        void onLocationUpdate(double latitude, double longitude);
    }
    private class ConnectSocketIOAsyncTask extends AsyncTask<Void, Void, Void> {
        private String userId;
        // 생성자를 통해 userId를 전달받도록 변경
        public ConnectSocketIOAsyncTask(String userId) {
            this.userId = userId;
        }
        @Override
        protected Void doInBackground(Void... params) {
            // 백그라운드 작업 수행
            connectSocketIO();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // UI 스레드에서 호출되는 메서드
            connectSocketIO();
        }
        private void connectSocketIO() {
            try {
                URI uri = new URI("http://172.10.7.13:80");
                mSocket = IO.socket(uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }
            mSocket.on(Socket.EVENT_CONNECT, args -> {
                Log.d("Socket.IO", "Connected");
            });
            mSocket.on("locationUpdate", args -> {
                Log.d("Socket.IO", "사용자 위치 불러오기 시도");
                JSONObject data = (JSONObject) args[0];
                try {
                    double latitude = data.getDouble("latitude");
                    double longitude = data.getDouble("longitude");
                    if (locationUpdateListener != null) {
                        runOnUiThread(() -> {
                            locationUpdateListener.onLocationUpdate(latitude, longitude);
                            Log.d("Socket.IO", "위치 소켓 LocationUpdateListener called - Latitude: " + latitude + ", Longitude: " + longitude);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Socket.IO", "위치 소켓 Error parsing locationUpdate data: " + e.getMessage());
                }
            });
            mSocket.on("message", args -> {
                String message = (String) args[0];
                Log.d("Socket.IO", "Received message: " + message);
                runOnUiThread(() -> showToast("Received message: " + message));
            });
            //여기 userid가 필요함
            mSocket.emit("setUser", userId);


            mSocket.on(Socket.EVENT_DISCONNECT, args -> {
                Log.d("Socket.IO", "Disconnected");
            });
            mSocket.connect();
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off();
        }
    }
}