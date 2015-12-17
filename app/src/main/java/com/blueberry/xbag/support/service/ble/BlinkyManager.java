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

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.blueberry.xbag.support.profile.BleManager;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class BlinkyManager extends BleManager<BlinkyManagerCallbacks> {
    /**
     * 报警 Service UUID
     */
    private final static UUID ALERT_SERVICE = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
    /**
     * 报警 characteristic UUID
     */
    private final static UUID ALERT_LEVEL_CHARACTERISTIC = UUID.fromString("00002A06-0000-1000-8000-00805f9b34fb");

    /**
     * X-BAG Service UUID
     */
    private final static UUID BAG_UUID_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    /**
     * X-BAG notify characteristic UUID
     */
    private final static UUID BAG_SUB_NOTIFY_CHARACTERISTIC = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

    private BluetoothGattCharacteristic mAlertCharacteristic, mNotifyCharacteristic;

    public BlinkyManager(final Context context) {
        super(context);
    }

    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }

    /**
     * BluetoothGatt callbacks for connection/disconnection, service discovery, receiving indication, etc
     */
    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        @Override
        protected Queue<Request> initGatt(final BluetoothGatt gatt) {
            final LinkedList<Request> requests = new LinkedList<>();
            requests.push(Request.newEnableNotificationsRequest(mNotifyCharacteristic));
            return requests;
        }

        @Override
        public boolean isRequiredServiceSupported(final BluetoothGatt gatt) {
            final BluetoothGattService bagService = gatt.getService(BAG_UUID_SERVICE);
            if (bagService != null) {
                mNotifyCharacteristic = bagService.getCharacteristic(BAG_SUB_NOTIFY_CHARACTERISTIC);
            }

            final BluetoothGattService alertService = gatt.getService(ALERT_SERVICE);
            if (alertService != null) {
                mAlertCharacteristic = alertService.getCharacteristic(ALERT_LEVEL_CHARACTERISTIC);
            }

            return mNotifyCharacteristic != null && mAlertCharacteristic != null;
        }

        @Override
        protected void onDeviceDisconnected() {
            mNotifyCharacteristic = null;
            mAlertCharacteristic = null;
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            mCallbacks.onDataSent(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0) == 0x02);
        }

        @Override
        public void onCharacteristicNotified(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            int data = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            Log.i("xixi", "Data : " + data);
            mCallbacks.onDataReceived(data == 0x01);
        }
    };

    public void send(final boolean onOff) {
        // Are we connected?
        if (mAlertCharacteristic == null)
            return;

        byte[] command;
        if (onOff) {
            command = new byte[]{2};
        } else {
            command = new byte[]{0};
        }
        mAlertCharacteristic.setValue(command);
        writeCharacteristic(mAlertCharacteristic);
    }
}
