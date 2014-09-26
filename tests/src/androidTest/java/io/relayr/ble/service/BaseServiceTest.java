package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static java.util.UUID.fromString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BaseServiceTest {

    @Test public void connectTest() {
        @SuppressWarnings("unchecked")
        Observer<BaseService> observer = mock(Observer.class);

        final BluetoothDevice device = mock(BluetoothDevice.class);
        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();

        BaseService
                .doConnect(device, receiver)
                .flatMap(new Func1<BluetoothGatt, Observable<BaseService>>() {
                    @Override
                    public Observable<BaseService> call(BluetoothGatt gatt) {
                        return Observable.just(new BaseService(device, gatt, receiver));
                    }
                })
                .subscribe(observer);

        receiver.onConnectionStateChange(mock(BluetoothGatt.class), GATT_SUCCESS, STATE_CONNECTED);
        receiver.onServicesDiscovered(mock(BluetoothGatt.class), GATT_SUCCESS);

        verify(observer, times(1)).onNext(any(BaseService.class));
    }

    @Test public void disconnectTest() {
        @SuppressWarnings("unchecked")
        Observer<BluetoothGatt> observer = mock(Observer.class);

        final BluetoothDevice device = mock(BluetoothDevice.class);
        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();

        BaseService
                .doConnect(device, receiver)
                .flatMap(new Func1<BluetoothGatt, Observable<BaseService>>() {
                    @Override
                    public Observable<BaseService> call(BluetoothGatt gatt) {
                        return Observable.just(new BaseService(device, gatt, receiver));
                    }
                })
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

    @Test public void getBatteryLevelTest() {
        BluetoothDevice device = mock(BluetoothDevice.class);

        BluetoothGattService service = mock(BluetoothGattService.class);
        when(service.getUuid()).thenReturn(fromString("0000180f-0000-1000-8000-00805f9b34fb"));

        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        when(characteristic.getValue()).thenReturn(new byte[] {0x64});
        when(characteristic.getUuid()).thenReturn(fromString("00002a19-0000-1000-8000-00805f9b34fb"));

        List<BluetoothGattCharacteristic> characteristics = Arrays.asList(characteristic);
        when(service.getCharacteristics()).thenReturn(characteristics);

        List<BluetoothGattService> services = Arrays.asList(service);

        BluetoothGatt gatt = mock(BluetoothGatt.class);
        when(gatt.getServices()).thenReturn(services);

        BaseService baseService = new BaseService(device, gatt, new BluetoothGattReceiver());
        assertEquals(100, baseService.getBatteryLevel());
    }

    @Test public void getFirmwareVersionTest() {
        BluetoothDevice device = mock(BluetoothDevice.class);

        BluetoothGattService service = mock(BluetoothGattService.class);
        when(service.getUuid()).thenReturn(fromString("0000180A-0000-1000-8000-00805f9b34fb"));

        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        String expected = "Relayr";
        when(characteristic.getStringValue(0)).thenReturn(expected);
        when(characteristic.getUuid()).thenReturn(fromString("00002A26-0000-1000-8000-00805f9b34fb"));

        List<BluetoothGattCharacteristic> characteristics = Arrays.asList(characteristic);
        when(service.getCharacteristics()).thenReturn(characteristics);

        List<BluetoothGattService> services = Arrays.asList(service);

        BluetoothGatt gatt = mock(BluetoothGatt.class);
        when(gatt.getServices()).thenReturn(services);

        BaseService baseService = new BaseService(device, gatt, new BluetoothGattReceiver());
        assertEquals(expected, baseService.getFirmwareVersion());
    }

    @Test public void getHardwareVersionTest() {
        BluetoothDevice device = mock(BluetoothDevice.class);

        BluetoothGattService service = mock(BluetoothGattService.class);
        when(service.getUuid()).thenReturn(fromString("0000180A-0000-1000-8000-00805f9b34fb"));

        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        String expected = "Relayr";
        when(characteristic.getStringValue(0)).thenReturn(expected);
        when(characteristic.getUuid()).thenReturn(fromString("00002a27-0000-1000-8000-00805f9b34fb"));

        List<BluetoothGattCharacteristic> characteristics = Arrays.asList(characteristic);
        when(service.getCharacteristics()).thenReturn(characteristics);

        List<BluetoothGattService> services = Arrays.asList(service);

        BluetoothGatt gatt = mock(BluetoothGatt.class);
        when(gatt.getServices()).thenReturn(services);

        BaseService baseService = new BaseService(device, gatt, new BluetoothGattReceiver());
        assertEquals(expected, baseService.getHardwareVersion());
    }

    @Test public void getManufacturerTest() {
        BluetoothDevice device = mock(BluetoothDevice.class);

        BluetoothGattService service = mock(BluetoothGattService.class);
        when(service.getUuid()).thenReturn(fromString("0000180A-0000-1000-8000-00805f9b34fb"));

        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        String expected = "Relayr";
        when(characteristic.getStringValue(0)).thenReturn(expected);
        when(characteristic.getUuid()).thenReturn(fromString("00002a29-0000-1000-8000-00805f9b34fb"));

        List<BluetoothGattCharacteristic> characteristics = Arrays.asList(characteristic);
        when(service.getCharacteristics()).thenReturn(characteristics);

        List<BluetoothGattService> services = Arrays.asList(service);

        BluetoothGatt gatt = mock(BluetoothGatt.class);
        when(gatt.getServices()).thenReturn(services);

        BaseService baseService = new BaseService(device, gatt, new BluetoothGattReceiver());
        assertEquals(expected, baseService.getManufacturer());
    }

}
