package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Build;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.relayr.RelayrApp;
import io.relayr.ble.DeviceCompatibilityUtils;
import io.relayr.ble.service.error.DisconnectionException;
import io.relayr.ble.service.error.WriteCharacteristicException;
import rx.Observable;
import rx.Subscriber;

import static android.bluetooth.BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
import static android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothGattReceiver extends BluetoothGattCallback {

    private volatile Subscriber<? super BluetoothGatt> mConnectionChangesSubscriber;
    private volatile Subscriber<? super BluetoothGatt> mDisconnectedSubscriber;
    private volatile Subscriber<? super BluetoothGatt> mBluetoothGattServiceSubscriber;
    private volatile Subscriber<? super BluetoothGattCharacteristic> mValueChangesSubscriber;
    private volatile Subscriber<? super BluetoothGattCharacteristic> mValueChangesUnSubscriber;
    private volatile Map<UUID, Subscriber<? super BluetoothGattCharacteristic>>
            mWriteCharacteristicsSubscriberMap = new ConcurrentHashMap<>();
    private volatile Map<UUID, Subscriber<? super BluetoothGattCharacteristic>>
            mReadCharacteristicsSubscriberMap = new ConcurrentHashMap<>();

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

        if (status != BluetoothGatt.GATT_SUCCESS) return;

        if (newState == STATE_CONNECTED) { // on connected
            if (mConnectionChangesSubscriber != null) mConnectionChangesSubscriber.onNext(gatt);
        } else if (newState == STATE_DISCONNECTED) {
            gatt.close();
            if (mDisconnectedSubscriber != null) { // disconnected voluntarily
                mDisconnectedSubscriber.onNext(gatt);
                mDisconnectedSubscriber.onCompleted();
            } else { // disconnected involuntarily because an error occurred
                if (mConnectionChangesSubscriber != null)
                    mConnectionChangesSubscriber.onError(new DisconnectionException(status + ""));
            }
        } /*else if (BluetoothGattStatus.isFailureStatus(status)) {
            if (mConnectionChangesSubscriber != null)  // TODO: unreachable -propagate error earlier
                mConnectionChangesSubscriber.onError(new GattException(status + ""));
        }*/
    }

    public Observable<BluetoothGatt> discoverDevices(final BluetoothGatt bluetoothGatt) {
        return Observable.create(new Observable.OnSubscribe<BluetoothGatt>() {
            @Override
            public void call(Subscriber<? super BluetoothGatt> subscriber) {
                mBluetoothGattServiceSubscriber = subscriber;
                if (bluetoothGatt.getServices() != null && bluetoothGatt.getServices().size() > 0)
                    mBluetoothGattServiceSubscriber.onNext(bluetoothGatt);
                else
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
                if (bluetoothGatt.getDevice().getBondState() != BluetoothDevice.BOND_NONE) {
                    DeviceCompatibilityUtils.removeBond(bluetoothGatt.getDevice());
                }
                bluetoothGatt.disconnect();
            }
        });
    }

    public Observable<BluetoothGattCharacteristic>
                            writeCharacteristic(final BluetoothGatt bluetoothGatt,
                                                final BluetoothGattCharacteristic characteristic) {
        return Observable.create(new Observable.OnSubscribe<BluetoothGattCharacteristic>() {
            @Override
            public void call(Subscriber<? super BluetoothGattCharacteristic> subscriber) {
                mWriteCharacteristicsSubscriberMap.put(characteristic.getUuid(), subscriber);
                bluetoothGatt.writeCharacteristic(characteristic);
            }
        });
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Subscriber<? super BluetoothGattCharacteristic> subscriber =
                mWriteCharacteristicsSubscriberMap.remove(characteristic.getUuid());
        if (status == BluetoothGatt.GATT_SUCCESS) {
            subscriber.onNext(characteristic);
        } else {
            subscriber.onError(new WriteCharacteristicException(characteristic, status));
        }
    }
    public Observable<BluetoothGattCharacteristic> readCharacteristic(
                                                final BluetoothGatt gatt,
                                                final BluetoothGattCharacteristic characteristic) {
        return Observable.create(new Observable.OnSubscribe<BluetoothGattCharacteristic>() {
            @Override
            public void call(Subscriber<? super BluetoothGattCharacteristic> subscriber) {
                mReadCharacteristicsSubscriberMap.put(characteristic.getUuid(), subscriber);
                gatt.readCharacteristic(characteristic);
            }
        });
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Subscriber<? super BluetoothGattCharacteristic> subscriber =
                mReadCharacteristicsSubscriberMap.remove(characteristic.getUuid());
        if (status == BluetoothGatt.GATT_SUCCESS) {
            subscriber.onNext(characteristic);
        } else {
            subscriber.onError(new WriteCharacteristicException(characteristic, status));
        }
    }

    public Observable<BluetoothGattCharacteristic> subscribeToCharacteristicChanges(
            final BluetoothGatt gatt,
            final BluetoothGattCharacteristic characteristic,
            final BluetoothGattDescriptor descriptor) {
        return Observable.create(new Observable.OnSubscribe<BluetoothGattCharacteristic>() {
            @Override
            public void call(Subscriber<? super BluetoothGattCharacteristic> subscriber) {
                mValueChangesSubscriber = subscriber;
                gatt.setCharacteristicNotification(characteristic, true);
                descriptor.setValue(ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        });
    }

    public Observable<BluetoothGattCharacteristic> unsubscribeToCharacteristicChanges(
            final BluetoothGatt gatt,
            final BluetoothGattCharacteristic characteristic,
            final BluetoothGattDescriptor descriptor) {
        return Observable.create(new Observable.OnSubscribe<BluetoothGattCharacteristic>() {
            @Override
            public void call(Subscriber<? super BluetoothGattCharacteristic> subscriber) {
                mValueChangesUnSubscriber = subscriber;
                gatt.setCharacteristicNotification(characteristic, false);
                descriptor.setValue(DISABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        });
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        mValueChangesSubscriber.onNext(characteristic);
    }

}
