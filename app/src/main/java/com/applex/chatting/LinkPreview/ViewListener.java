package com.applex.chatting.LinkPreview;

/**
 * Created by ponna on 16-01-2018.
 */

public interface ViewListener {

    void onSuccess(boolean status);

    void onError(Exception e);
}
