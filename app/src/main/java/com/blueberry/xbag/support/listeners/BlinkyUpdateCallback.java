package com.blueberry.xbag.support.listeners;

/**
 * Created by Hanqing on 2015/12/7.
 */
public interface BlinkyUpdateCallback {
    void onRing();

    void onRingCancel();

    void onConnect();

    void onDisconnect();

    void onOneClick();

    void onDoubleClick();
}
