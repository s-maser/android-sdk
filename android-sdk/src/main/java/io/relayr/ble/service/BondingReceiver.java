package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import io.relayr.RelayrApp;
import rx.Observable;
import rx.Subscriber;

import static android.bluetooth.BluetoothDevice.*;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BondingReceiver {

    static Observable<BluetoothGatt> subscribeForBondStateChanges(final BluetoothGatt gatt) {
        return Observable.create(new Observable.OnSubscribe<BluetoothGatt>() {
            @Override
            public void call(final Subscriber<? super BluetoothGatt> subscriber) {
                RelayrApp.get().registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
                        int bondState = intent.getIntExtra(EXTRA_BOND_STATE, -1);
                        int previousBondState = intent.getIntExtra(EXTRA_PREVIOUS_BOND_STATE, -1);

                        // skip other devices
                        if (!device.equals(gatt.getDevice()))
                            return;

                        if (bondState == BOND_BONDED) {
                            RelayrApp.get().unregisterReceiver(this);
                            subscriber.onNext(gatt);
                            subscriber.onCompleted();
                        }

                        /*if (previousBondState == BOND_BONDING && bondState == BOND_NONE) {
                            RelayrApp.get().unregisterReceiver(this);
                            subscriber.onError(new Exception("Not bonded"));
                        }*/
                    }
                }, new IntentFilter(ACTION_BOND_STATE_CHANGED));
            }
        });
    }

}
