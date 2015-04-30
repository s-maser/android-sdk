package io.relayr.ble.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import java.nio.ByteBuffer;

import io.relayr.ble.BleDevice;
import io.relayr.ble.service.error.CharacteristicNotFoundException;
import rx.Observable;
import rx.functions.Func1;

import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_COMMIT;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_MQTT_CLIENT_ID;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_MQTT_HOST;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_MQTT_PASSWORD;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_MQTT_TOPIC;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_MQTT_USER;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_DATA;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_STATUS;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_WIFI_PASSWORD;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_WIFI_SSID;
import static io.relayr.ble.service.ShortUUID.DESCRIPTOR_DATA_NOTIFICATIONS;
import static io.relayr.ble.service.ShortUUID.SERVICE_NEW_ON_BOARDING;
import static io.relayr.ble.service.Utils.getCharacteristicInServices;
import static io.relayr.ble.service.Utils.getDescriptorInCharacteristic;
import static rx.Observable.error;

/**
 * A class representing the service associated with the NEW_ON_BOARDING mode
 * @see {@link io.relayr.ble.BleDeviceMode}
 */
public class OnBoardingV2Service extends BaseService {

    public enum OnBoardingStatus {
        STATUS_SUCCESS, STATUS_UN_CONFIGURED, STATUS_WIFI_ERROR, STATUS_TCP_ERROR, UNKNOWN
    }

    protected OnBoardingV2Service(BleDevice device, BluetoothGatt gatt,
                                  BluetoothGattReceiver receiver) {
        super(device, gatt, receiver);
    }

    public static Observable<OnBoardingV2Service> connect(final BleDevice bleDevice,
                                                           final BluetoothDevice device) {
        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        return doConnect(device, receiver, true)
                .map(new Func1<BluetoothGatt, OnBoardingV2Service>() {
                    @Override
                    public OnBoardingV2Service call(BluetoothGatt gatt) {
                        return new OnBoardingV2Service(bleDevice, gatt, receiver);
                    }
                });
    }

    /**
     * Writes the WiFi SSID characteristic to the associated remote device.
     * <p/>
     * <p>See {@link BluetoothGatt#beginReliableWrite()} for details as to the actions performed in
     * the background.
     * @param ssid A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGatt> writeWiFiSSID(byte[] ssid) {
        return longWrite(ssid, SERVICE_NEW_ON_BOARDING, CHARACTERISTIC_WIFI_SSID);
    }

    /**
     * Writes the WiFi password characteristic to the associated remote device.
     * <p/>
     * <p>See {@link BluetoothGatt#beginReliableWrite()} for details as to the actions performed in
     * the background.
     * @param password A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGatt> writeWiFiPassword(byte[] password) {
        return longWrite(password, SERVICE_NEW_ON_BOARDING, CHARACTERISTIC_WIFI_PASSWORD);
    }

    /**
     * Writes the MQTT user characteristic to the associated remote device.
     * <p/>
     * <p>See {@link BluetoothGatt#writeCharacteristic} for details as to the actions performed in
     * the background.
     * @param user A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGatt> writeMqttUser(byte[] user) {
        return longWrite(user, SERVICE_NEW_ON_BOARDING, CHARACTERISTIC_MQTT_USER);
    }

    /**
     * Writes the MQTT password characteristic to the associated remote device.
     * <p/>
     * <p>See {@link BluetoothGatt#beginReliableWrite()} for details as to the actions performed in
     * the background.
     * @param password A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGatt> writeMqttPassword(byte[] password) {
        return longWrite(password, SERVICE_NEW_ON_BOARDING, CHARACTERISTIC_MQTT_PASSWORD);
    }

    /**
     * Writes the MQTT topic characteristic to the associated remote device.
     * <p/>
     * <p>See {@link BluetoothGatt#beginReliableWrite()} for details as to the actions performed in
     * the background.
     * @param topic A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGatt> writeMqttTopic(byte[] topic) {
        return longWrite(topic, SERVICE_NEW_ON_BOARDING, CHARACTERISTIC_MQTT_TOPIC);
    }

    /**
     * Writes the transmitter characteristic to the associated remote device.
     * <p/>
     * <p>See {@link BluetoothGatt#beginReliableWrite()} for details as to the actions performed in
     * the background.
     * @param transmitter A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGatt> writeMqttHost(byte[] transmitter) {
        return longWrite(transmitter, SERVICE_NEW_ON_BOARDING, CHARACTERISTIC_MQTT_HOST);
    }

    /**
     * Writes the transmitter characteristic to the associated remote device.
     * <p/>
     * <p>See {@link BluetoothGatt#beginReliableWrite()} for details as to the actions performed in
     * the background.
     * @param transmitter A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGatt> writeMqttClientId(byte[] transmitter) {
        return longWrite(transmitter, SERVICE_NEW_ON_BOARDING, CHARACTERISTIC_MQTT_CLIENT_ID);
    }

    /**
     * Writes the transmitter characteristic to the associated remote device.
     * <p/>
     * <p>See {@link BluetoothGatt#writeCharacteristic} for details as to the actions performed in
     * the background.
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGatt> writeCommit() {
        return longWrite(new byte[1], SERVICE_NEW_ON_BOARDING, CHARACTERISTIC_COMMIT);
    }

    /**
     * Writes the transmitter characteristic to the associated remote device.
     * <p/>
     * <p>See {@link BluetoothGatt#writeCharacteristic} for details as to the actions performed in
     * the background.
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<OnBoardingStatus> getNotifications() {
        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), SERVICE_NEW_ON_BOARDING, CHARACTERISTIC_STATUS);
        if (characteristic == null) {
            return error(new CharacteristicNotFoundException(CHARACTERISTIC_SENSOR_DATA));
        }
        BluetoothGattDescriptor descriptor = getDescriptorInCharacteristic(
                characteristic, DESCRIPTOR_DATA_NOTIFICATIONS);

        return mBluetoothGattReceiver
                .subscribeToCharacteristicChanges(mBluetoothGatt, characteristic, descriptor)
                .map(new Func1<BluetoothGattCharacteristic, OnBoardingStatus>() {
                    @Override
                    public OnBoardingStatus call(BluetoothGattCharacteristic characteristic) {
                        ByteBuffer wrapped = ByteBuffer.wrap(characteristic.getValue());

                        try {
                            int status = wrapped.get(0);
                            return OnBoardingStatus.values()[status];
                        } catch (Exception e) {
                            Log.d("OnBoardingV2Service", "Failed to parse OnBoardingStatus.");
                            return OnBoardingStatus.UNKNOWN;
                        }
                    }
                });
    }
}
