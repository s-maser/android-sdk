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
import java.util.List;
import java.util.UUID;

import io.relayr.ble.BleDevice;
import io.relayr.ble.BleDeviceType;
import rx.Observer;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT16;
import static java.util.UUID.fromString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DirectConnectionServiceTest {

    private static final int EXPECTED_FREQUENCY = 1000; // in milliseconds
    private static final byte[] EXPECTED_SENSOR_ID_AS_BYTE_ARRAY = new byte[] {100, 56, 98, 48, 56,
            51, 55, 55, 45, 98, 99, 102, 99, 45, 52, 57, 98, 100, 45, 57, 97, 50, 102, 45, 50, 99,
            97, 48, 98, 49, 98, 48, 48, 54, 98, 50};
    private static final UUID EXPECTED_SENSOR_ID =
            UUID.nameUUIDFromBytes(EXPECTED_SENSOR_ID_AS_BYTE_ARRAY);

    private BluetoothGattCharacteristic frequencyCharacteristic;
    private BluetoothGattCharacteristic sensorIdCharacteristic;
    private BluetoothGatt gatt;
    private BluetoothGattReceiver receiver;
    private DirectConnectionService service;

    @Before public void initialise() {
        BleDevice bleDevice = mock(BleDevice.class);
        BluetoothDevice device = mock(BluetoothDevice.class);
        when(bleDevice.getType()).thenReturn(BleDeviceType.WunderbarMIC);

        BluetoothGattService batteryService = mock(BluetoothGattService.class);
        when(batteryService.getUuid()).thenReturn(fromString("00002002-0000-1000-8000-00805f9b34fb"));

        frequencyCharacteristic = mock(BluetoothGattCharacteristic.class);
        when(frequencyCharacteristic.getIntValue(FORMAT_UINT16, 0)).thenReturn(EXPECTED_FREQUENCY);
        when(frequencyCharacteristic.getValue()).thenReturn(new byte[]{0x38, 0x03, 0x00, 0x00});
        when(frequencyCharacteristic.getUuid()).thenReturn(fromString("00002012-0000-1000-8000-00805f9b34fb"));

        sensorIdCharacteristic = mock(BluetoothGattCharacteristic.class);
        when(sensorIdCharacteristic.getValue()).thenReturn(EXPECTED_SENSOR_ID_AS_BYTE_ARRAY);
        when(sensorIdCharacteristic.getUuid()).thenReturn(fromString("00002010-0000-1000-8000-00805f9b34fb"));

        List<BluetoothGattCharacteristic> batteryCharacteristics =
                Arrays.asList(frequencyCharacteristic, sensorIdCharacteristic);
        when(batteryService.getCharacteristics()).thenReturn(batteryCharacteristics);

        List<BluetoothGattService> services = Arrays.asList(batteryService);

        gatt = mock(BluetoothGatt.class);
        when(gatt.getServices()).thenReturn(services);
        receiver = new BluetoothGattReceiver();
        service = new DirectConnectionService(bleDevice, device, gatt, receiver);
    }

    @Test public void getSensorFrequencyTest() {
        @SuppressWarnings("unchecked")
        Observer<? super Integer> observer = mock(Observer.class);
        service.getSensorFrequency()
               .subscribe(observer);
        receiver.onCharacteristicRead(gatt, frequencyCharacteristic, GATT_SUCCESS);
        verify(observer).onNext(EXPECTED_FREQUENCY);
    }

    @Test public void getSensorIdTest() {
        System.out.println(Arrays.toString(EXPECTED_SENSOR_ID.toString().getBytes()));
        @SuppressWarnings("unchecked")
        Observer<? super UUID> observer = mock(Observer.class);
        service.getSensorId()
               .subscribe(observer);
        receiver.onCharacteristicRead(gatt, sensorIdCharacteristic, GATT_SUCCESS);
        verify(observer).onNext(EXPECTED_SENSOR_ID);
    }

}
