package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

import rx.Observer;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static java.util.UUID.fromString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class OnBoardingServiceTest {

    private BluetoothDevice device;
    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic characteristic;

    @Before public void initialise() {
        device = mock(BluetoothDevice.class);
        gatt = mock(BluetoothGatt.class);
        BluetoothGattService service = mock(BluetoothGattService.class);
        when(gatt.getServices()).thenReturn(Arrays.asList(service));
        when(service.getUuid()).thenReturn(fromString("00002001-0000-1000-8000-00805f9b34fb"));
        characteristic = mock(BluetoothGattCharacteristic.class);
        when(service.getCharacteristics()).thenReturn(Arrays.asList(characteristic));
    }

    @Test public void writeSensorIdTest() {
        when(characteristic.getUuid()).thenReturn(fromString("00002010-0000-1000-8000-00805f9b34fb"));

        BluetoothGattReceiver receiver = new BluetoothGattReceiver();

        OnBoardingService onBoardingService = new OnBoardingService(device, gatt, receiver);
        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);

        onBoardingService
                .writeSensorId(new byte[0])
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);

        verify(observer, times(1)).onNext(characteristic);
    }

    @Test public void writeSensorPassKeyTest() {
        when(characteristic.getUuid()).thenReturn(fromString("00002018-0000-1000-8000-00805f9b34fb"));

        BluetoothGattReceiver receiver = new BluetoothGattReceiver();

        OnBoardingService onBoardingService = new OnBoardingService(device, gatt, receiver);
        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);

        onBoardingService
                .writeSensorPassKey(new byte[0])
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);

        verify(observer, times(1)).onNext(characteristic);
    }

    @Test public void writeOnBoardingFlagTest() {
        when(characteristic.getUuid()).thenReturn(fromString("00002019-0000-1000-8000-00805f9b34fb"));

        BluetoothGattReceiver receiver = new BluetoothGattReceiver();

        OnBoardingService onBoardingService = new OnBoardingService(device, gatt, receiver);
        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);

        onBoardingService
                .writeOnBoardingFlag(new byte[0])
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);

        verify(observer, times(1)).onNext(characteristic);
    }

}
