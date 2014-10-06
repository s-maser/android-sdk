package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Build;

import java.util.UUID;

import io.relayr.ble.BleDevice;
import io.relayr.ble.DeviceCompatibilityUtils;
import io.relayr.ble.service.error.CharacteristicNotFoundException;
import rx.Observable;
import rx.functions.Func1;

import static io.relayr.ble.parser.BleDataParser.getFormattedValue;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_CONFIGURATION;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_DATA;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_FREQUENCY;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_ID;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_LED_STATE;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_THRESHOLD;
import static io.relayr.ble.service.ShortUUID.DESCRIPTOR_DATA_NOTIFICATIONS;
import static io.relayr.ble.service.ShortUUID.SERVICE_DIRECT_CONNECTION;
import static io.relayr.ble.service.Utils.getCharacteristicInServices;
import static io.relayr.ble.service.Utils.getDescriptorInCharacteristic;
import static rx.Observable.error;
import static rx.Observable.just;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DirectConnectionService extends BaseService {

    DirectConnectionService(BleDevice bleDevice, BluetoothDevice device, BluetoothGatt gatt,
                            BluetoothGattReceiver receiver) {
        super(bleDevice, device, gatt, receiver);
    }

    public static Observable<DirectConnectionService> connect(final BleDevice bleDevice,
                                                              final BluetoothDevice device) {
        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        return doConnect(device, receiver)
                .flatMap(new Func1<BluetoothGatt, Observable<? extends BluetoothGatt>>() {
                    @Override
                    public Observable<? extends BluetoothGatt> call(final BluetoothGatt gatt) {
                        int state = device.getBondState();

                        if (state == BluetoothDevice.BOND_BONDED) {
                            return just(gatt);
                        } else if (state == BluetoothDevice.BOND_BONDING) {
                            return BondingReceiver.subscribeForBondStateChanges(gatt);
                        } //else if (state == BluetoothDevice.BOND_NONE) {

                        Observable<BluetoothGatt> bluetoothGattObservable =
                                BondingReceiver.subscribeForBondStateChanges(gatt);
                        DeviceCompatibilityUtils.createBond(device);
                        return bluetoothGattObservable;
                    }
                })
                .flatMap(new Func1<BluetoothGatt, Observable<DirectConnectionService>>() {
                    @Override
                    public Observable<DirectConnectionService> call(BluetoothGatt gatt) {
                        return just(new DirectConnectionService(bleDevice, device, gatt, receiver));
                    }
                });
    }

    public Observable<String> getReadings() {
        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_DATA);
        BluetoothGattDescriptor descriptor = getDescriptorInCharacteristic(
                characteristic, DESCRIPTOR_DATA_NOTIFICATIONS);
        return mBluetoothGattReceiver
                .subscribeToCharacteristicChanges(mBluetoothGatt, characteristic, descriptor)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<String>>() {
                    @Override
                    public Observable<String> call(BluetoothGattCharacteristic characteristic) {
                        return just(getFormattedValue(mBleDevice.getType(), characteristic.getValue()));
                    }
                });
    }

    /**
     * Return an observable of the Sensor Id characteristic.
     * <p>See {@link BluetoothGatt#readCharacteristic} for details of what it's done internally.
     * @return an observable of the Sensor Id characteristic
     */
    public Observable<UUID> getSensorId() {
        final String text = "Sensor Id";
        return readCharacteristic(SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_ID, text)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<UUID>>() {
                    @Override
                    public Observable<UUID> call(BluetoothGattCharacteristic characteristic) {
                        byte[] value = characteristic.getValue();
                        if (value == null) {
                            return error(new CharacteristicNotFoundException(text));
                        }
                        return just(UUID.nameUUIDFromBytes(value));
                    }
                });
    }

    //public void readBeaconFrequency() {} // 2011

    /**
     * Reads the sensorFrequency characteristic to the associated remote device. This is the time
     * difference between two ble events are sent.
     *
     * <p>See {@link BluetoothGatt#readCharacteristic} for details of what it's done internally.
     *
     * @return Observable<Integer>, an observable of the sensor frequency value
     */
    public Observable<Integer> getSensorFrequency() {
        final String text = "Sensor Frequency";
        return readIntegerCharacteristic(
                SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_FREQUENCY, text);
    }

    /**
     * Reads the SensorThreshold characteristic to the associated remote device. This is the
     * magnitude or intensity that must be exceeded for a sensor to register a change.
     *
     * <p>See {@link BluetoothGatt#readCharacteristic} for details of what it's done internally.
     *
     * @return Observable<Float>, an observable of the sensor threshold value
     */
    /*TODO: public Observable<Float> readSensorThreshold() {
        final String text = "Sensor Threshold";
        return readFloatCharacteristic(
                SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_THRESHOLD, text);
    }*/

    /**
     * Writes the sensorFrequency characteristic to the associated remote device. This is the time
     * difference between two ble events are sent.
     *
     * <p>See {@link BluetoothGatt#writeCharacteristic} for details of what it's done internally.
     *
     * @param sensorFrequency Bytes to write on the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written in the
     * device
     */
    public Observable<BluetoothGattCharacteristic> writeSensorFrequency(byte[] sensorFrequency) {
        return write(sensorFrequency, SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_FREQUENCY);
    }

    /**
     * Writes the sensorLedState characteristic to the associated remote device. It will turn the
     * LED on or off accordingly if the operation is done successfully.
     *
     * <p>See {@link BluetoothGatt#writeCharacteristic} for details of what it's done internally.
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
     * <p>See {@link BluetoothGatt#writeCharacteristic} for details of what it's done internally.
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
     * <p>See {@link BluetoothGatt#writeCharacteristic} for details of what it's done internally.
     *
     * @param configuration Bytes to write on the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written in the
     * device
     */
    public Observable<BluetoothGattCharacteristic> writeSensorConfig(byte[] configuration) {
        return write(configuration, SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_CONFIGURATION);
    }

}
