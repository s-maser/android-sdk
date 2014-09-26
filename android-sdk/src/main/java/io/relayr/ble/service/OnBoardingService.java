package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import rx.Observable;
import rx.functions.Func1;

import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_ON_BOARDING_FLAG;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_PASS_KEY;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_ID;
import static io.relayr.ble.service.ShortUUID.SERVICE_ON_BOARDING;
import static io.relayr.ble.service.Utils.getCharacteristicInServices;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class OnBoardingService extends BaseService {

    protected OnBoardingService(BluetoothDevice device, BluetoothGatt gatt, BluetoothGattReceiver receiver) {
        super(device, gatt, receiver);
    }

    public static Observable<OnBoardingService> connect(final BluetoothDevice bluetoothDevice) {
        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        return doConnect(bluetoothDevice, new BluetoothGattReceiver())
                .flatMap(new Func1<BluetoothGatt, Observable<OnBoardingService>>() {
                    @Override
                    public Observable<OnBoardingService> call(BluetoothGatt gatt) {
                        return Observable.just(new OnBoardingService(bluetoothDevice, gatt, receiver));
                    }
                });
    }

    public Observable<BluetoothGattCharacteristic> writeSensorId(byte[] bytes) {
        return write(bytes, SERVICE_ON_BOARDING, CHARACTERISTIC_SENSOR_ID);
    }

    private Observable<BluetoothGattCharacteristic> write(byte[] bytes,
                                                          String serviceUuid,
                                                          String characteristicUuid) {
        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), serviceUuid, characteristicUuid);
        characteristic.setValue(bytes);
        return mBluetoothGattReceiver.writeCharacteristic(mBluetoothGatt, characteristic);
    }

    public Observable<BluetoothGattCharacteristic> writeSensorPassKey(byte[] bytes) {
        return write(bytes, SERVICE_ON_BOARDING, CHARACTERISTIC_PASS_KEY);
    }
    public Observable<BluetoothGattCharacteristic> writeOnBoardingFlag(byte[] bytes) {
        return write(bytes, SERVICE_ON_BOARDING, CHARACTERISTIC_ON_BOARDING_FLAG);
    }

    public void readSensorId() {}
    public void readSensorPassKey() {}
    public void readOnBoardingFlag() {}

}
