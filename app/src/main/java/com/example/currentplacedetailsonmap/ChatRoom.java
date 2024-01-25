//ChatRoom.java
package com.example.currentplacedetailsonmap;

public class ChatRoom {
    private String roomName;
    private String chatroomId;
    private String timeStamp;
    private String previewText;
    //private int uncheckedChat;

    public ChatRoom(String roomName,String chatroomId,String timeStamp,String previewText) {
        this.roomName = roomName;
        this.chatroomId = chatroomId;
        this.timeStamp = timeStamp;
        this.previewText = previewText;
        //this.uncheckedChat = uncheckedChat;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getChatroomId() {
        return chatroomId;
    }
    public String getTimeStamp() {
        return timeStamp;
    }
    public String getPreviewText() {
        return previewText;
    }
    /*public int getUncheckedChat() {
        return uncheckedChat;
    }*/


}