package com.example.currentplacedetailsonmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ViewHolder> {

    private List<Chat> chattings;

    public ChattingAdapter(List<Chat> chattings) {
        this.chattings = chattings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chattings.get(position);
        // 여기에서 실제 데이터를 바인딩하는 코드를 추가할 수 있습니다.
        holder.textViewSender.setText(chat.getSender());
        holder.textViewContent.setText(chat.getContent());
        holder.textViewSendtime.setText(chat.getSender());
    }

    @Override
    public int getItemCount() {
        return chattings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSender;
        TextView textViewContent;
        TextView textViewSendtime;
        ViewHolder(View itemView) {
            super(itemView);
            textViewSender = itemView.findViewById(R.id.sender);
            textViewContent = itemView.findViewById(R.id.content);
            textViewSendtime = itemView.findViewById(R.id.sendtime);
        }
    }

    public void addChat(Chat chat) {
        chattings.add(chat);
        notifyItemInserted(chattings.size() - 1);
    }


    public void setChat(List<Chat> chatRooms) {
        this.chattings = chatRooms;
        notifyDataSetChanged();
    }
}
