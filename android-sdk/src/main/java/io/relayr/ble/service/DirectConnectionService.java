package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import rx.Observable;
import rx.functions.Func1;

import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_CONFIGURATION;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_FREQUENCY;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_ID;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_LED_STATE;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_THRESHOLD;
import static io.relayr.ble.service.ShortUUID.SERVICE_DIRECT_CONNECTION;
import static io.relayr.ble.service.Utils.getCharacteristicInServicesAsString;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DirectConnectionService extends BaseService {

    private DirectConnectionService(BluetoothDevice device, BluetoothGatt gatt, BluetoothGattReceiver receiver) {
        super(device, gatt, receiver);
    }

    public static Observable<DirectConnectionService> connect(final BluetoothDevice bluetoothDevice) {
        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        return doConnect(bluetoothDevice, new BluetoothGattReceiver())
                .flatMap(new Func1<BluetoothGatt, Observable<DirectConnectionService>>() {
                    @Override
                    public Observable<DirectConnectionService> call(BluetoothGatt gatt) {
                        return Observable.just(new DirectConnectionService(bluetoothDevice, gatt, receiver));
                    }
                });
    }

    /**
     * Return the stored value of the Battery Level characteristic. It doesn't query it again
     * because in this mode the value cannot be changed therefore it doesn't need to be requested
     * again.
     * <p>See {@link android.bluetooth.BluetoothGattCharacteristic#getValue} for details.
     * @return Cached value of the characteristic
     */
    public String readSensorId() {
        return getCharacteristicInServicesAsString(
                mBluetoothGatt.getServices(), SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_ID);
    }

    //public void readBeaconFrequency() {} // 2011

    public void readSensorFrequency() {} // 2012

    /**
     * Writes the sensorFrequency characteristic to the associated remote device. This is the time
     * difference between two ble events are sent.
     *
     * @param sensorFrequency Bytes to write on the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written in the
     * device
     */
    public Observable<BluetoothGattCharacteristic> writeSensorFrequency(byte[] sensorFrequency) {
        return write(sensorFrequency, SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_FREQUENCY);
    }

    public void readSensorLedState() {}// 2013

    /**
     * Writes the sensorLedState characteristic to the associated remote device. It will turn the
     * LED on or off accordingly if the operation is done successfully.
     *
     * @param sensorLedState Bytes to write on the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written in the
     * device
     */
    public Observable<BluetoothGattCharacteristic> writeSensorLedState(byte[] sensorLedState) {
        return write(sensorLedState, SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_LED_STATE);
    }

    /**
     * Writes the sensorThreshold characteristic to the associated remote device. This is the
     * magnitude or intensity that must be exceeded for a sensor to register a change.
     *
     * @param sensorThreshold Bytes to write on the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written in the
     * device
     */
    public Observable<BluetoothGattCharacteristic> writeSensorThreshold(byte[] sensorThreshold) {
        return write(sensorThreshold, SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_THRESHOLD);
    }

    /**
     * Writes the sensorConfig characteristic to the associated remote device.
     *
     * @param configuration Bytes to write on the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written in the
     * device
     */
    public Observable<BluetoothGattCharacteristic> writeSensorConfig(byte[] configuration) {
        return write(configuration, SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_CONFIGURATION);
    }

}
