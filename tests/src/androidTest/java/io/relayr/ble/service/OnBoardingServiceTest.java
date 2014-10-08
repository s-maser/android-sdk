package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

import io.relayr.ble.BleDevice;
import io.relayr.ble.BleDeviceType;
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

    private BluetoothGattCharacteristic characteristic;
    private OnBoardingService service;
    private BluetoothGattReceiver receiver = new BluetoothGattReceiver();

    @Before public void initialise() {
        BleDevice bleDevice = mock(BleDevice.class);
        when(bleDevice.getType()).thenReturn(BleDeviceType.WunderbarMIC);
        BluetoothGatt gatt = mock(BluetoothGatt.class);
        BluetoothGattService gattService = mock(BluetoothGattService.class);
        when(gatt.getServices()).thenReturn(Arrays.asList(gattService));
        when(gattService.getUuid()).thenReturn(fromString("00002001-0000-1000-8000-00805f9b34fb"));
        characteristic = mock(BluetoothGattCharacteristic.class);
        when(gattService.getCharacteristics()).thenReturn(Arrays.asList(characteristic));

        service = new OnBoardingService(bleDevice, gatt, receiver);
    }

    @Test public void writeSensorIdTest() {
        when(characteristic.getUuid()).thenReturn(fromString("00002010-0000-1000-8000-00805f9b34fb"));

        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);

        service
                .writeSensorId(new byte[0])
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);

        verify(observer, times(1)).onNext(characteristic);
    }

    @Test public void writeSensorPassKeyTest() {
        when(characteristic.getUuid()).thenReturn(fromString("00002018-0000-1000-8000-00805f9b34fb"));

        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);

        service
                .writeSensorPassKey(new byte[0])
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);

        verify(observer, times(1)).onNext(characteristic);
    }

    @Test public void writeOnBoardingFlagForDirectConnectionTest() {
        when(characteristic.getUuid()).thenReturn(fromString("00002019-0000-1000-8000-00805f9b34fb"));

        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);

        service
                .writeOnBoardingFlagForDirectConnection()
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);

        verify(observer, times(1)).onNext(characteristic);
    }

    @Test public void writeOnBoardingFlagToConnectToMasterModuleTest() {
        when(characteristic.getUuid()).thenReturn(fromString("00002019-0000-1000-8000-00805f9b34fb"));

        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);

        service
                .writeOnBoardingFlagToConnectToMasterModule()
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);

        verify(observer, times(1)).onNext(characteristic);
    }

}
