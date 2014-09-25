package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BaseServiceTest {

    @Test public void connectTest() {
        @SuppressWarnings("unchecked")
        Observer<BaseService> observer = mock(Observer.class);

        BluetoothDevice device = mock(BluetoothDevice.class);
        BluetoothGattReceiver receiver = new BluetoothGattReceiver();

        BaseService
                .connect(device, receiver)
                .subscribe(observer);

        receiver.onConnectionStateChange(mock(BluetoothGatt.class), GATT_SUCCESS, STATE_CONNECTED);
        receiver.onServicesDiscovered(mock(BluetoothGatt.class), GATT_SUCCESS);

        verify(observer, times(1)).onNext(any(BaseService.class));
    }

    @Test public void disconnectTest() {
        @SuppressWarnings("unchecked")
        Observer<BluetoothGatt> observer = mock(Observer.class);

        BluetoothDevice device = mock(BluetoothDevice.class);
        BluetoothGattReceiver receiver = new BluetoothGattReceiver();

        BaseService
                .connect(device, receiver)
                .flatMap(new Func1<BaseService, Observable<? extends BluetoothGatt>>() {
                    @Override
                    public Observable<? extends BluetoothGatt> call(BaseService baseService) {
                        return baseService.disconnect();
                    }

                })
                .subscribe(observer);

        receiver.onConnectionStateChange(mock(BluetoothGatt.class), GATT_SUCCESS, STATE_CONNECTED);
        receiver.onServicesDiscovered(mock(BluetoothGatt.class), GATT_SUCCESS);
        receiver.onConnectionStateChange(mock(BluetoothGatt.class), GATT_SUCCESS, STATE_DISCONNECTED);

        verify(observer, times(1)).onNext(any(BluetoothGatt.class));
    }



}
