package com.example.currentplacedetailsonmap;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import io.socket.client.IO;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = MapsFragment.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int M_MAX_ENTRIES = 5;
    private String[] likelyPlaceNames;
    private String[] likelyPlaceAddresses;
    private List[] likelyPlaceAttributions;
    private LatLng[] likelyPlaceLatLngs;
    private Socket socket;
    private io.socket.client.Socket mSocket;
    private Timer locationUpdateTimer;
    private MainActivity.LocationUpdateListener locationUpdateListener;
    //지도에 표시할 마커 목록
    private List<Marker> otherUsersMarkers = new ArrayList<>();




    private void sendLocationUpdate(LatLng location) {
        // socket이 초기화되었고 연결된 상태인지 확인
        if (mSocket != null && mSocket.connected()) {
            JSONObject locationData = new JSONObject();
            try {
                // 위치 데이터를 JSONObject에 추가
                locationData.put("latitude", location.latitude);
                locationData.put("longitude", location.longitude);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("websocketLocation", "JSON Exception while creating location data: " + e.getMessage());
            }
            // 서버로 위치 업데이트 이벤트('updateLocation')를 발송

//            mSocket.emit("askLocationUpdate", locationData, user_id, locationroomId); // user_id, locationroomId 받아와야 함!!!!!!!
            mSocket.emit("askLocationUpdate", locationData, "jmk2229", 1); // 하드코딩
            // 위치 업데이트 로그
            Log.d("websocketLocation", "Location update sent to server - Latitude: " + location.latitude + ", Longitude: " + location.longitude);
        }
        else {
            // 연결되지 않은 경우 에러 메시지 출력
            Log.e("websocketLocation", "Socket is not initialized or not connected");
        }
    }

// 해당 Fragment가 처음으로 생성될 때 호출되며, 지도와 관련된 초기화 작업을 수행
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        Places.initialize(requireContext(), BuildConfig.PLACES_API_KEY);
        placesClient = Places.createClient(requireContext());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());// 현재 위치 정보에 액세스하기 위한 클라이언트

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        return rootView;
    }

// Fragment의 상태를 저장
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }
//Google Map이 준비되었을 때 호출
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, (FrameLayout) getView().findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        getLocationPermission();
        getDeviceLocation();
        updateLocationUI();

        // 소켓 초기화 및 연결
        initSocketConnection();

        // 서버에서 위치 업데이트를 받아 처리
        startLocationUpdateTimer();
    }

    //소켓 연결
    private void initSocketConnection() {
        try {
            URI uri = new URI("http://172.10.7.13:80");
            mSocket = IO.socket(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        mSocket.on(mSocket.EVENT_CONNECT, args -> {
            Log.d("Socket.IO", "Connected");
        });

        //다른 사람 위치 정보 불러오기
        mSocket.on("getOtherlocation", args -> {
            Log.d("Socket.IO", "다른 사용자 위치 불러오기 시도");
            JSONObject data = (JSONObject) args[0];
            try {
                double latitude = data.getDouble("latitude");
                double longitude = data.getDouble("longitude");

                LatLng userLocation = new LatLng(latitude, longitude);
                updateOtherUsersMarker(userLocation);if (locationUpdateListener != null) {
                    requireActivity().runOnUiThread(() -> {
                        locationUpdateListener.onLocationUpdate(latitude, longitude);
                        Log.d("Socket.IO", "위치 소켓 LocationUpdateListener called - Latitude: " + latitude + ", Longitude: " + longitude);
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Socket.IO", "다른 사용자 위치 불러오기 실패 Error parsing locationUpdate data: " + e.getMessage());
            }
        });

        mSocket.on(mSocket.EVENT_DISCONNECT, args -> {
            Log.d("Socket.IO", "Disconnected");
        });

        mSocket.connect();
    }

    private void updateOtherUsersMarker(LatLng location) {
        // 마커가 이미 추가되어 있다면 업데이트, 없다면 새로 추가
        if (!otherUsersMarkers.isEmpty()) {
            Marker marker = otherUsersMarkers.get(0);  // 여러 마커가 있는 경우에 대한 처리를 추가해야 할 수 있습니다.
            marker.setPosition(location);
        } else {
            Marker newMarker = map.addMarker(new MarkerOptions().position(location).title("다른 사용자"));
            otherUsersMarkers.add(newMarker);
        }
    }

    private void removeOtherUsersMarkers() {
        // 모든 다른 사용자 마커 제거
        for (Marker marker : otherUsersMarkers) {
            marker.remove();
        }
        otherUsersMarkers.clear();
    }

    private void startLocationUpdateTimer() {
        locationUpdateTimer = new Timer();
        locationUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (lastKnownLocation != null) {
                    LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    sendLocationUpdate(currentLocation);
                }
            }
        }, 0, 3000); // 3초마다 위치 업데이트
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Fragment가 소멸되면 타이머 중지
        stopLocationUpdateTimer();
    }

    private void stopLocationUpdateTimer() {
        if (locationUpdateTimer != null) {
            locationUpdateTimer.cancel();
            locationUpdateTimer.purge();
        }
    }
//현재 기기의 위치를 가져와 지도를 해당 위치로 이동
    private void getDeviceLocation() {
        try {
            // 위치 권한이 허용된 경우
            if (locationPermissionGranted) {
                // FusedLocationProviderClient를 사용하여 마지막으로 알려진 위치를 가져옵니다.
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        // 작업이 성공적으로 완료된 경우
                        if (task.isSuccessful()) {
                            // 가져온 위치를 lastKnownLocation에 저장합니다.
                            lastKnownLocation = task.getResult();
                            // 위치가 null이 아닌 경우
                            if (lastKnownLocation != null) {
                                // 맵을 해당 위치로 이동시킵니다.
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            // 위치를 가져오는 데 실패한 경우
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            // 기본 위치로 맵을 이동시킵니다.
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            // 현재 위치 버튼 비활성화
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }
    private void showCurrentPlace() {
        // Implement your logic for showing current place here
    }

    private void openPlacesDialog() {
        // Implement your logic for opening places dialog here
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
