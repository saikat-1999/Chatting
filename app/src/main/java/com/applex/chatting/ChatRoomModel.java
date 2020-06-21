package com.applex.chatting;

public class ChatRoomModel {
    private String lastMessage, receiver, receiverDP, roomID, receiverUid;

    public ChatRoomModel() {
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverDP() {
        return receiverDP;
    }

    public void setReceiverDP(String receiverDP) {
        this.receiverDP = receiverDP;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }
}
