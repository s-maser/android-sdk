package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static io.relayr.ble.service.ShortUUID.BATTERY_LEVEL_CHARACTERISTIC;
import static io.relayr.ble.service.ShortUUID.BATTERY_LEVEL_SERVICE;
import static io.relayr.ble.service.ShortUUID.DEVICE_INFO_SERVICE;
import static io.relayr.ble.service.ShortUUID.FIRMWARE_VERSION_CHARACTERISTIC;
import static java.util.UUID.fromString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class UtilsTest {

    @Test public void getServiceForUuid() {
        BluetoothGattService service = mock(BluetoothGattService.class);
        when(service.getUuid()).thenReturn(fromString("0000180F-0000-1000-8000-00805f9b34fb"));
        List<BluetoothGattService> services = new ArrayList<>();
        services.add(service);
        assertNotNull(Utils.getServiceForUuid(services, BATTERY_LEVEL_SERVICE));
    }

    @Test public void getCharacteristicForUuid() {
        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        when(characteristic.getUuid()).thenReturn(fromString("00002A19-0000-1000-8000-00805f9b34fb"));
        List<BluetoothGattCharacteristic> characteristics = new ArrayList<>();
        characteristics.add(characteristic);
        assertNotNull(Utils.getCharacteristicForUuid(characteristics, BATTERY_LEVEL_CHARACTERISTIC));
    }

    @Test public void getCharacteristicInServices() {
        BluetoothGattService service = mock(BluetoothGattService.class);
        when(service.getUuid()).thenReturn(fromString("0000180F-0000-1000-8000-00805f9b34fb"));

        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        when(characteristic.getUuid()).thenReturn(fromString("00002A19-0000-1000-8000-00805f9b34fb"));

        List<BluetoothGattCharacteristic> characteristics = new ArrayList<>();
        characteristics.add(characteristic);
        when(service.getCharacteristics()).thenReturn(characteristics);

        List<BluetoothGattService> services = new ArrayList<>();
        services.add(service);

        assertNotNull(Utils.getCharacteristicInServices(services, BATTERY_LEVEL_SERVICE,
                BATTERY_LEVEL_CHARACTERISTIC));
    }

    @Test public void getCharacteristicInServicesAsString() {
        BluetoothGattService service = mock(BluetoothGattService.class);
        when(service.getUuid()).thenReturn(fromString("0000180A-0000-1000-8000-00805f9b34fb"));

        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        String expected = "Relayr";
        when(characteristic.getStringValue(0)).thenReturn(expected);
        when(characteristic.getUuid()).thenReturn(fromString("00002A26-0000-1000-8000-00805f9b34fb"));

        List<BluetoothGattCharacteristic> characteristics = new ArrayList<>();
        characteristics.add(characteristic);
        when(service.getCharacteristics()).thenReturn(characteristics);

        List<BluetoothGattService> services = new ArrayList<>();
        services.add(service);

        assertEquals(expected, Utils.getCharacteristicInServicesAsString(
                services, DEVICE_INFO_SERVICE, FIRMWARE_VERSION_CHARACTERISTIC));
    }

}
