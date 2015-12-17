/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.blueberry.xbag.support.service.ble;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import com.blueberry.xbag.support.profile.BleManager;
import com.blueberry.xbag.support.profile.BleProfileService;


public class BlinkyService extends BleProfileService implements BlinkyManagerCallbacks {
    private static final String TAG = "BlinkyService";

    public static final String BROADCAST_RING_STATE_CHANGED = "com.blueberry.xbag.BROADCAST_RING_STATE_CHANGED";
    public static final String EXTRA_DATA = "com.blueberry.xbag.EXTRA_DATA";
    public static final String BROADCAST_CLICK = "com.blueberry.xbag.BROADCAST_CLICK";

    private BlinkyManager mManager;

    private int pendingCount;

    private final BlinkyBinder mBinder = new BlinkyBinder();

    public class BlinkyBinder extends BleProfileService.LocalBinder implements BlinkyInterface {
        private boolean mRingState;

        @Override

        public void send(final boolean onOff) {
            mManager.send(onOff);
        }

        public boolean isRing() {
            return mRingState;
        }
    }

    @Override
    protected LocalBinder getBinder() {
        return mBinder;
    }

    @Override
    protected BleManager<BlinkyManagerCallbacks> initializeManager() {
        return mManager = new BlinkyManager(this);
    }

    @Override
    public void onDataReceived(final boolean state) {
        if (!state) {
            return;
        }

        final Intent broadcast = new Intent(BROADCAST_CLICK);
        broadcast.putExtra(EXTRA_DATA, state);
        LocalBroadcastManager.getInstance(BlinkyService.this).sendBroadcast(broadcast);

    }

    @Override
    public void onDataSent(boolean state) {
        mBinder.mRingState = state;

        final Intent broadcast = new Intent(BROADCAST_RING_STATE_CHANGED);
        broadcast.putExtra(EXTRA_DATA, state);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }
}
