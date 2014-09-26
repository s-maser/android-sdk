package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.relayr.ble.service.error.DisconnectionException;
import io.relayr.ble.service.error.WriteCharacteristicException;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

import static android.bluetooth.BluetoothGatt.GATT_FAILURE;
import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothGattReceiverTest {

    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice bluetoothDevice;

    @Before public void setUp() {
        bluetoothGatt = mock(BluetoothGatt.class);
        bluetoothDevice = mock(BluetoothDevice.class);
    }

    @Test public void connect_shouldSuccessfullyConnect() {

        @SuppressWarnings("unchecked")
        Observer<BluetoothGatt> observer = mock(Observer.class);

        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        receiver.connect(bluetoothDevice)
                .subscribe(observer);

        receiver.onConnectionStateChange(bluetoothGatt, GATT_SUCCESS, STATE_CONNECTED);

        verify(observer, times(1)).onNext(bluetoothGatt);
    }

    @Test public void connect_shouldSuccessfullyConnectAndThenThrowADisconnectError() {

        @SuppressWarnings("unchecked")
        Observer<BluetoothGatt> observer = mock(Observer.class);

        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        receiver.connect(bluetoothDevice)
                .subscribe(observer);

        receiver.onConnectionStateChange(bluetoothGatt, GATT_SUCCESS, STATE_CONNECTED);
        receiver.onConnectionStateChange(bluetoothGatt, GATT_SUCCESS, STATE_DISCONNECTED);

        verify(observer, times(1)).onNext(bluetoothGatt);
        verify(observer, times(1)).onError(any(DisconnectionException.class));
    }

    @Test public void discoverServices_shouldCallOnNext() {

        @SuppressWarnings("unchecked")
        Observer<BluetoothGatt> observer = mock(Observer.class);

        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        receiver.connect(bluetoothDevice)
                .flatMap(new Func1<BluetoothGatt, Observable<BluetoothGatt>>() {
                    @Override
                    public Observable<BluetoothGatt> call(BluetoothGatt gatt) {
                        return receiver.discoverDevices(gatt);
                    }
                })
                .subscribe(observer);

        receiver.onConnectionStateChange(bluetoothGatt, GATT_SUCCESS, STATE_CONNECTED);
        receiver.onServicesDiscovered(bluetoothGatt, GATT_SUCCESS);

        verify(observer, times(1)).onNext(bluetoothGatt);
    }

    @Test public void disconnect_shouldCallOnComplete() {
        @SuppressWarnings("unchecked")
        Observer<BluetoothGatt> observer = mock(Observer.class);

        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        receiver.connect(bluetoothDevice)
                .flatMap(new Func1<BluetoothGatt, Observable<BluetoothGatt>>() {
                    @Override
                    public Observable<BluetoothGatt> call(BluetoothGatt gatt) {
                        return receiver.disconnect(gatt);
                    }
                })
                .subscribe(observer);


        receiver.onConnectionStateChange(bluetoothGatt, GATT_SUCCESS, STATE_CONNECTED);
        receiver.onConnectionStateChange(bluetoothGatt, GATT_SUCCESS, STATE_DISCONNECTED);

        verify(observer, times(1)).onNext(bluetoothGatt);
    }

    @Test public void writeCharacteristic_shouldWork() {
        BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);
        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        receiver.writeCharacteristic(mock(BluetoothGatt.class), characteristic)
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);

        verify(observer, times(1)).onNext(characteristic);
    }

    @Test public void writeCharacteristic_shouldThrowAnError() {
        BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);
        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        receiver.writeCharacteristic(mock(BluetoothGatt.class), characteristic)
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_FAILURE);

        verify(observer, times(1)).onError(any(WriteCharacteristicException.class));
    }

}
