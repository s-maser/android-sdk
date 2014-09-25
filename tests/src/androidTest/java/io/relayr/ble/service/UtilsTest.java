package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_BATTERY_LEVEL;
import static io.relayr.ble.service.ShortUUID.SERVICE_BATTERY_LEVEL;
import static io.relayr.ble.service.ShortUUID.SERVICE_DEVICE_INFO;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_FIRMWARE_VERSION;
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
        List<BluetoothGattService> services = Arrays.asList(service);
        assertNotNull(Utils.getServiceForUuid(services, SERVICE_BATTERY_LEVEL));
    }

    @Test public void getCharacteristicForUuid() {
        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        when(characteristic.getUuid()).thenReturn(fromString("00002A19-0000-1000-8000-00805f9b34fb"));
        List<BluetoothGattCharacteristic> characteristics = Arrays.asList(characteristic);
        assertNotNull(Utils.getCharacteristicForUuid(characteristics, CHARACTERISTIC_BATTERY_LEVEL));
    }

    @Test public void getCharacteristicInServices() {
        BluetoothGattService service = mock(BluetoothGattService.class);
        when(service.getUuid()).thenReturn(fromString("0000180F-0000-1000-8000-00805f9b34fb"));

        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        when(characteristic.getUuid()).thenReturn(fromString("00002A19-0000-1000-8000-00805f9b34fb"));

        List<BluetoothGattCharacteristic> characteristics = Arrays.asList(characteristic);
        when(service.getCharacteristics()).thenReturn(characteristics);

        List<BluetoothGattService> services = Arrays.asList(service);

        assertNotNull(Utils.getCharacteristicInServices(services, SERVICE_BATTERY_LEVEL,
                CHARACTERISTIC_BATTERY_LEVEL));
    }

    @Test public void getCharacteristicInServicesAsString() {
        BluetoothGattService service = mock(BluetoothGattService.class);
        when(service.getUuid()).thenReturn(fromString("0000180A-0000-1000-8000-00805f9b34fb"));

        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        String expected = "Relayr";
        when(characteristic.getStringValue(0)).thenReturn(expected);
        when(characteristic.getUuid()).thenReturn(fromString("00002A26-0000-1000-8000-00805f9b34fb"));

        List<BluetoothGattCharacteristic> characteristics = Arrays.asList(characteristic);
        when(service.getCharacteristics()).thenReturn(characteristics);

        List<BluetoothGattService> services = Arrays.asList(service);

        assertEquals(expected, Utils.getCharacteristicInServicesAsString(
                services, SERVICE_DEVICE_INFO, CHARACTERISTIC_FIRMWARE_VERSION));
    }

}
