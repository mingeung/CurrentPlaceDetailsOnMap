package com.example.currentplacedetailsonmap;
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
import java.util.ArrayList;
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
public class ChattingRoomFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChattingAdapter chattingAdapter;
    private List<Chat> chattings = new ArrayList<>();
    private String chatroomId;
    private String roomName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting_room, container, false);

        Bundle args = getArguments();
        if (args != null) {
            chatroomId = args.getString("chatroomId", "");
            roomName = args.getString("roomName", "");
        }

        recyclerView = view.findViewById(R.id.messageView);
        chattingAdapter = new ChattingAdapter(chattings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(chattingAdapter);

        ((MainActivity) requireActivity()).getmSocket().on("receivemessege", onReceiveMessege);

        Button sendButton = view.findViewById(R.id.sendButton);
        CardView centeredCardView = view.findViewById(R.id.centeredCardView);
        Button invitebutton = view.findViewById(R.id.invitebutton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button submitButton = view.findViewById(R.id.submitButton);
        EditText messageBox = view.findViewById(R.id.messageBox);
        EditText editText = view.findViewById(R.id.editText);
        invitebutton.setOnClickListener(new View.OnClickListener() {
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
                    String invitedId = editText.getText().toString();

                    JSONObject eventData = new JSONObject();
                    eventData.put("invitedId", invitedId);
                    eventData.put("roomId", chatroomId);
                    eventData.put("roomName", roomName);

                    ((MainActivity) requireActivity()).getmSocket().emit("invitation", eventData);

                    Log.d("Socket invitation", "createRoom event emitted successfully");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Socket invitation", "JSONException: " + e.getMessage());
                }
                editText.setText("");
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String userId = "test"; //SharedPreference로 바꾸기
                    String category = "text"; //+image
                    String content = messageBox.getText().toString();

                    JSONObject eventData = new JSONObject();
                    eventData.put("userId", userId);
                    eventData.put("roomId", chatroomId);
                    eventData.put("content", content);
                    eventData.put("category", category);

                    ((MainActivity) requireActivity()).getmSocket().emit("message", eventData);

                    Log.d("Socket message", "message event emitted successfully");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Socket message", "JSONException: " + e.getMessage());
                }
                messageBox.setText("");
            }
        });

        OkHttpClient client = new OkHttpClient();
        //SharedPreferences preferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String jsonData = String.format("{\"room_id\": \"%s\"}",chatroomId);
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), jsonData);
        Request request = new Request.Builder()
                .url("http://172.10.5.162:80/Chats") // Replace with your actual Flask server endpoint
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
                                JSONObject chatObject = dataArray.getJSONObject(i);

                                String sender = chatObject.getString("nickname");
                                String content = chatObject.getString("contents");
                                String sendtime = chatObject.getString("send_time");

                                Chat chat = new Chat(sender,content,sendtime);

                                // Assuming you have a method to update the RecyclerView on the main thread
                                getActivity().runOnUiThread(() -> {
                                    // Add the ChatRoom to your RecyclerView adapter
                                    chattingAdapter.addChat(chat);
                                });


                                getActivity().runOnUiThread(() -> {

                                });
                            }
                        } else {
                            Log.d("Chattingss_fragmentERR","error");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        return view;
    }

    private final Emitter.Listener onReceiveMessege = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                JSONObject data = (JSONObject) args[0];
                String chatroom_id=data.getString("chatroom_id");
                String sender_id=data.getString("sender_id");
                String contents=data.getString("contents");
                String sender_name=data.getString("nickname");
                Chat newChat = new Chat(sender_name,chatroom_id,contents);
                Log.d("receivemessage","tttttt");
                // Update the RecyclerView on the main thread
                getActivity().runOnUiThread(() -> chattingAdapter.addChat(newChat));
            } catch(JSONException e) {
                e.printStackTrace();
                Log.e("ChattingFragment", "JSONException: " + e.getMessage());
            }
        }
    };
}