package com.example.currentplacedetailsonmap;

public class Chat {
    private String sender;
    private String content;
    private String sendtime;

    public Chat(String sender, String content, String sendtime) {
        this.sender = sender;
        this.content = content;
        this.sendtime = sendtime;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getSendtime() {
        return sendtime;
    }

}