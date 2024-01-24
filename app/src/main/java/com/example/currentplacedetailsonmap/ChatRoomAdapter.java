package com.example.currentplacedetailsonmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {

    private List<ChatRoom> chatRooms;

    public ChatRoomAdapter(List<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ChatRoom chatRoom);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatRoom chatRoom = chatRooms.get(position);
        // 여기에서 실제 데이터를 바인딩하는 코드를 추가할 수 있습니다.
        holder.textViewRoomName.setText(chatRoom.getRoomName());
        holder.textViewRoomId.setText(chatRoom.getChatroomId());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(chatRooms.get(adapterPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRoomName;
        TextView textViewRoomId;
        ViewHolder(View itemView) {
            super(itemView);
            textViewRoomName = itemView.findViewById(R.id.textViewRoomName);
            textViewRoomId = itemView.findViewById(R.id.chatroomId);
        }
    }

    public void addChatRoom(ChatRoom chatRoom) {
        chatRooms.add(chatRoom);
        notifyItemInserted(chatRooms.size() - 1);
    }

    public void addChatRoomFirst(ChatRoom chatRoom) {
        chatRooms.add(0,chatRoom);
        notifyItemInserted(0);
    }

    public void setChatRooms(List<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
        notifyDataSetChanged();
    }
}
