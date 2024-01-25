//ChattingFragment
package com.example.currentplacedetailsonmap;
import static com.example.currentplacedetailsonmap.LoginActivity.KEY_USER_ID;
import static com.example.currentplacedetailsonmap.LoginActivity.PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.cardview.widget.CardView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ChattingFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatRoomAdapter chatRoomAdapter;
    //private Button addButton;
    private List<ChatRoom> chatRooms = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        chatRoomAdapter = new ChatRoomAdapter(chatRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(chatRoomAdapter);

        ((MainActivity) requireActivity()).getmSocket().on("joinRoom", onJoinRoom);
        //((MainActivity) requireActivity()).getmSocket().on("receivemessagee", onReceiveMessegee);

        Button addButton = view.findViewById(R.id.createBtn);
        CardView centeredCardView = view.findViewById(R.id.centeredCardView);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button submitButton = view.findViewById(R.id.submitButton);
        EditText editText = view.findViewById(R.id.editText);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centeredCardView.setVisibility(View.VISIBLE);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centeredCardView.setVisibility(View.GONE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centeredCardView.setVisibility(View.GONE);

                try {
                    SharedPreferences preferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                    String userId = preferences.getString(KEY_USER_ID, "");
                    String chatroomId = RandomStringUtil.generateRandomString(36);
                    String locationroomId = RandomStringUtil.generateRandomString(36);
                    String roomName = editText.getText().toString();

                    JSONObject eventData = new JSONObject();
                    eventData.put("userId", userId);
                    eventData.put("chatroomId", chatroomId);
                    eventData.put("locationroomId", locationroomId);
                    eventData.put("roomName", roomName);

                    ((MainActivity) requireActivity()).getmSocket().emit("createRoom", eventData);


                    Log.d("Socket createRoom", "createRoom event emitted successfully");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Socket createRoom", "JSONException: " + e.getMessage());
                }
                editText.setText("");
            }
        });

        chatRoomAdapter.setOnItemClickListener(chatRoom -> {
            String clickedChatroomId = chatRoom.getChatroomId();
            String clickedChatroomName = chatRoom.getRoomName();


            try {
                SharedPreferences preferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                String userId = preferences.getString(KEY_USER_ID, "");

                JSONObject eventData = new JSONObject();
                eventData.put("userId", userId);
                eventData.put("roomId", clickedChatroomId);
                ((MainActivity) requireActivity()).getmSocket().emit("resetchecked", eventData);


                //Log.d("Socket createRoom", "createRoom event emitted successfully");
            } catch (JSONException e) {
                e.printStackTrace();
                //Log.e("Socket createRoom", "JSONException: " + e.getMessage());
            }
            switchToNextFragment(clickedChatroomId,clickedChatroomName);

        });

        OkHttpClient client = new OkHttpClient();
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String user_id = preferences.getString(KEY_USER_ID, "");
        String jsonData = String.format("{\"user_id\": \"%s\"}",user_id);
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), jsonData);
        Request request = new Request.Builder()
                .url("http://172.10.5.162:80/ChattingRoomList") // Replace with your actual Flask server endpoint
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // Handle failure
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                // Handle the response from the server
                if (response.isSuccessful()) {
                    // Handle successful response

                    // Convert the response body to a string
                    String responseBody = response.body().string();

                    try {
                        // Parse the JSON response
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        if (jsonResponse.has("data")) {
                            // Get the array of data
                            JSONArray dataArray = jsonResponse.getJSONArray("data");

                            // Process each item in the data array
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject roomObject = dataArray.getJSONObject(i);

                                String chatroom_id = roomObject.getString("chatroom_id");
                                String roomName = roomObject.getString("roomname");
                                String timeStamp = roomObject.getString("last_time");
                                String previewText = roomObject.getString("last_message");
                                //String uncheckedChat = roomObject.getString("roomname");

                                String lasttime=timeStamp.substring(11,16);


                                // Create a ChatRoom object or use your existing model class
                                ChatRoom chatRoom = new ChatRoom(roomName,chatroom_id,lasttime,previewText);

                                // Assuming you have a method to update the RecyclerView on the main thread
                                getActivity().runOnUiThread(() -> {
                                    // Add the ChatRoom to your RecyclerView adapter
                                    chatRoomAdapter.addChatRoom(chatRoom);
                                });
                            }
                        } else {
                            Log.d("Chatting_fragmentERR","error");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        return view;
    }

    private void switchToNextFragment(String chatroomId, String roomName) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ChattingRoomFragment nextFragment = new ChattingRoomFragment();
        Bundle args = new Bundle();
        args.putString("chatroomId", chatroomId);
        args.putString("roomName", roomName);
        nextFragment.setArguments(args);

        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private final Emitter.Listener onJoinRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                JSONObject data = (JSONObject) args[0];
                String roomName=data.getString("roomname");
                String roomId=data.getString("room_id");
                String timeStamp = data.getString("last_time");
                String previewText = data.getString("last_message");
                //String uncheckedChat = data.getString("roomname");

                ChatRoom newChatRoom = new ChatRoom(roomName,roomId,timeStamp,previewText);
                Log.d("joinRoom","tttttt");

                // Update the RecyclerView on the main thread
                getActivity().runOnUiThread(() -> {
                    // Add the ChatRoom to your RecyclerView adapter
                    chatRoomAdapter.addChatRoomFirst(newChatRoom);
                    recyclerView.scrollToPosition(0);
                });
            } catch(JSONException e) {
                e.printStackTrace();
                Log.e("ChattingFragment", "JSONException: " + e.getMessage());
            }
        }
    };

    /*private final Emitter.Listener onReceiveMessegee = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof ChattingFragment) {
                getActivity().runOnUiThread(() -> {
                // Add the ChatRoom to your RecyclerView adapter
                    ChattingFragment fragment = (ChattingFragment) currentFragment;
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(fragment).attach(fragment).commit();
                    Log.d("EEEE","새로고침");
                } );
            }
            else {
                // 적절한 처리를 수행하거나 오류를 로깅합니다.
                Log.d("Error", "Current fragment is not an instance of ChattingFragment");
            }
        }
    };*/
}