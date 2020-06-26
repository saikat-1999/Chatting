package com.applex.chatting;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class UserModel {

    private String uid, name, status, image;
    @ServerTimestamp private Timestamp lastSeen;
    private long isOnline;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Timestamp getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Timestamp lastSeen) {
        this.lastSeen = lastSeen;
    }

    public long getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(long isOnline) {
        this.isOnline = isOnline;
    }

}
