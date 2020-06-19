package com.applex.chatting;

public class ChatRoomModel {
    private String LastMessage, Receiver, ReceiverDP, roomID, ReceiverUid;

    public ChatRoomModel() {
    }

    public String getReceiverUid() {
        return ReceiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        ReceiverUid = receiverUid;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getReceiverDP() {
        return ReceiverDP;
    }

    public void setReceiverDP(String receiverDP) {
        ReceiverDP = receiverDP;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}
