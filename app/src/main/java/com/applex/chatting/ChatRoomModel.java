package com.applex.chatting;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class ChatRoomModel {
    private String lastMessage, receiver, receiverDP, roomID, receiverUid;

    @ServerTimestamp private Timestamp timestamp;//Last Message Timestamp

    private long blocked;

    public ChatRoomModel() {
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public long getBlocked() {
        return blocked;
    }

    public void setBlocked(long blocked) {
        this.blocked = blocked;
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
