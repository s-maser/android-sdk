package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.os.Build;

import io.relayr.RelayrApp;
import io.relayr.ble.BluetoothGattStatus;
import rx.Observable;
import rx.Subscriber;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothGattReceiver extends BluetoothGattCallback {

    private volatile Subscriber<? super BluetoothGatt> mConnectionChangesSubscriber;
    private volatile Subscriber<? super BluetoothGatt> mDisconnectedSubscriber;
    private volatile Subscriber<? super BluetoothGatt> mBluetoothGattServiceSubscriber;

    public Observable<BluetoothGatt> connect(final BluetoothDevice bluetoothDevice) {
        return Observable.create(new Observable.OnSubscribe<BluetoothGatt>() {
            @Override
            public void call(Subscriber<? super BluetoothGatt> subscriber) {
                mConnectionChangesSubscriber = subscriber;
                bluetoothDevice.connectGatt(RelayrApp.get(), true, BluetoothGattReceiver.this);
            }
        });
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

        if (status != BluetoothGatt.GATT_SUCCESS || mConnectionChangesSubscriber == null) return;

        if (newState == STATE_CONNECTED) { // on connected
            mConnectionChangesSubscriber.onNext(gatt);
        } else if (newState == STATE_DISCONNECTED) {
            if (mDisconnectedSubscriber != null) { // disconnected voluntarily
                mDisconnectedSubscriber.onNext(gatt);
                mDisconnectedSubscriber.onCompleted();
            } else { // disconnected involuntarily because an error occurred
                mConnectionChangesSubscriber.onError(new DisconnectionException(status + ""));
            }
        } else if (BluetoothGattStatus.isFailureStatus(status)) {
            mConnectionChangesSubscriber.onError(new GattException(status + ""));
        }
    }

    public Observable<BluetoothGatt> discoverDevices(final BluetoothGatt bluetoothGatt) {
        return Observable.create(new Observable.OnSubscribe<BluetoothGatt>() {
            @Override
            public void call(Subscriber<? super BluetoothGatt> subscriber) {
                mBluetoothGattServiceSubscriber = subscriber;
                bluetoothGatt.discoverServices();
            }
        });
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (mBluetoothGattServiceSubscriber == null) return;
        mBluetoothGattServiceSubscriber.onNext(gatt);
    }

    public Observable<BluetoothGatt> disconnect(final BluetoothGatt bluetoothGatt) {
        return Observable.create(new Observable.OnSubscribe<BluetoothGatt>() {
            @Override
            public void call(Subscriber<? super BluetoothGatt> subscriber) {
                mDisconnectedSubscriber = subscriber;
                bluetoothGatt.disconnect();
            }
        });
    }
}
