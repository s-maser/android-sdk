package io.relayr.ble.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import io.relayr.ble.BleDevice;
import rx.Observable;
import rx.functions.Func1;

import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_MQTT_PASSWORD;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_MQTT_TOPIC;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_MQTT_USER;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_TRANSMITTER;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_WIFI_PASSWORD;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_WIFI_SSID;
import static io.relayr.ble.service.ShortUUID.SERVICE_BLE_ON_BOARDING;

/**
 * A class representing the service associated with the MASTER_MODULE_BLE mode
 * @see {@link io.relayr.ble.BleDeviceMode}
 */
public class NewOnBoardingService extends BaseService {
    protected NewOnBoardingService(BleDevice device, BluetoothGatt gatt,
                                   BluetoothGattReceiver receiver) {
        super(device, gatt, receiver);
    }

    public static Observable<NewOnBoardingService> connect(final BleDevice bleDevice,
                                                          final BluetoothDevice device) {
        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        return doConnect(device, receiver, false)
                .map(new Func1<BluetoothGatt, NewOnBoardingService>() {
                    @Override
                    public NewOnBoardingService call(BluetoothGatt gatt) {
                        return new NewOnBoardingService(bleDevice, gatt, receiver);
                    }
                });
    }

    /**
     * Writes the WiFi SSID characteristic to the associated remote device.
     *
     * <p>See {@link android.bluetooth.BluetoothGatt#writeCharacteristic} for details as to the actions performed in
     * the background.
     *
     * @param ssid A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGattCharacteristic> writeWiFiSSID(byte[] ssid) {
        return write(ssid, SERVICE_BLE_ON_BOARDING, CHARACTERISTIC_WIFI_SSID);
    }

    /**
     * Writes the WiFi password characteristic to the associated remote device.
     *
     * <p>See {@link android.bluetooth.BluetoothGatt#writeCharacteristic} for details as to the actions performed in
     * the background.
     *
     * @param password A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGatt> writeWiFiPassword(byte[] password) {
        return longWrite(password, SERVICE_BLE_ON_BOARDING, CHARACTERISTIC_WIFI_PASSWORD);
    }

    /**
     * Writes the MQTT user characteristic to the associated remote device.
     *
     * <p>See {@link android.bluetooth.BluetoothGatt#writeCharacteristic} for details as to the actions performed in
     * the background.
     *
     * @param user A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGattCharacteristic> writeMqttUser(byte[] user) {
        return write(user, SERVICE_BLE_ON_BOARDING, CHARACTERISTIC_MQTT_USER);
    }

    /**
     * Writes the MQTT password characteristic to the associated remote device.
     *
     * <p>See {@link android.bluetooth.BluetoothGatt#writeCharacteristic} for details as to the actions performed in
     * the background.
     *
     * @param password A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGattCharacteristic> writeMqttPassword(byte[] password) {
        return write(password, SERVICE_BLE_ON_BOARDING, CHARACTERISTIC_MQTT_PASSWORD);
    }

    /**
     * Writes the MQTT topic characteristic to the associated remote device.
     *
     * <p>See {@link android.bluetooth.BluetoothGatt#writeCharacteristic} for details as to the actions performed in
     * the background.
     *
     * @param topic A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGattCharacteristic> writeMqttTopic(byte[] topic) {
        return write(topic, SERVICE_BLE_ON_BOARDING, CHARACTERISTIC_MQTT_TOPIC);
    }

    /**
     * Writes the transmitter characteristic to the associated remote device.
     *
     * <p>See {@link android.bluetooth.BluetoothGatt#writeCharacteristic} for details as to the actions performed in
     * the background.
     *
     * @param transmitter A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGattCharacteristic> writeTransmitter(byte[] transmitter) {
        return write(transmitter, SERVICE_BLE_ON_BOARDING, CHARACTERISTIC_TRANSMITTER);
    }
}
