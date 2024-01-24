package com.example.currentplacedetailsonmap;

public class ChatRoom {
    private String roomName;
    private String chatroomId;

    public ChatRoom(String roomName,String chatroomId) {
        this.roomName = roomName;
        this.chatroomId = chatroomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getChatroomId() {
        return chatroomId;
    }

}