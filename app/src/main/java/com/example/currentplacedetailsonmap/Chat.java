package com.example.currentplacedetailsonmap;

public class Chat {
    private String sender;
    private String content;
    private String sendtime;
    private int itemType;

    public Chat(String sender, String content, String sendtime, int itemType) {
        this.sender = sender;
        this.content = content;
        this.sendtime = sendtime;
        this.itemType = itemType;
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
    public int getItemType() {
        return itemType;
    }

}