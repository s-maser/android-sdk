package com.relayr.core.ble.sensor;

import java.util.UUID;

import com.relayr.core.ble.Relayr_BleGattExecutor;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

/**
 * Created by steven on 9/3/13.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class TiSensor<T> {
    private final static String TAG = TiSensor.class.getSimpleName();

    private static String CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    private T data;

    protected TiSensor() {
    }

    public abstract String getName();

    public String getCharacteristicName(String uuid) {
        if (getDataUUID().equals(uuid))
            return getName() + " Data";
        else if (getConfigUUID().equals(uuid))
            return getName() + " Config";
        return "Unknown";
    }

    public abstract String getServiceUUID();
    public abstract String getDataUUID();
    public abstract String getConfigUUID();

    public boolean isConfigUUID(String uuid) {
        return false;
    }

    public T getData() {
        return data;
    }

    public abstract String getDataString();

    public void onCharacteristicChanged(BluetoothGattCharacteristic c) {
        data = parse(c);
    }

    public boolean onCharacteristicRead(BluetoothGattCharacteristic c) {
        return false;
    }

    protected byte[] getConfigValues(boolean enable) {
        return new byte[] { (byte)(enable ? 1 : 0) };
    }

    protected abstract T parse(BluetoothGattCharacteristic c);
/*
    public Relayr_BleGattExecutor.ServiceAction[] enable(final boolean enable) {
        return new Relayr_BleGattExecutor.ServiceAction[] {
                write(getConfigUUID(), getConfigValues(enable)),
                notify(enable)
        };
    }

    public Relayr_BleGattExecutor.ServiceAction update() {
        return Relayr_BleGattExecutor.ServiceAction.NULL;
    }

    public Relayr_BleGattExecutor.ServiceAction read(final String uuid) {
        return new Relayr_BleGattExecutor.ServiceAction() {
            @Override
            public boolean execute(BluetoothGatt bluetoothGatt) {
                final BluetoothGattCharacteristic characteristic = getCharacteristic(bluetoothGatt, uuid);
                bluetoothGatt.readCharacteristic(characteristic);
                return false;
            }
        };
    }

    public Relayr_BleGattExecutor.ServiceAction write(final String uuid, final byte[] value) {
        return new Relayr_BleGattExecutor.ServiceAction() {
            @Override
            public boolean execute(BluetoothGatt bluetoothGatt) {
                final BluetoothGattCharacteristic characteristic = getCharacteristic(bluetoothGatt, uuid);
                characteristic.setValue(value);
                bluetoothGatt.writeCharacteristic(characteristic);
                return false;
            }
        };
    }

    public Relayr_BleGattExecutor.ServiceAction notify(final boolean start) {
        return new Relayr_BleGattExecutor.ServiceAction() {
            @Override
            public boolean execute(BluetoothGatt bluetoothGatt) {
                final UUID CCC = UUID.fromString(CHARACTERISTIC_CONFIG);

                final BluetoothGattCharacteristic dataCharacteristic = getCharacteristic(bluetoothGatt, getDataUUID());
                final BluetoothGattDescriptor config = dataCharacteristic.getDescriptor(CCC);
                if (config == null)
                    return true;

                // enable/disable locally
                bluetoothGatt.setCharacteristicNotification(dataCharacteristic, start);
                // enable/disable remotely
                config.setValue(start ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(config);
                return false;
            }
        };
    }
*/
    private BluetoothGattCharacteristic getCharacteristic(BluetoothGatt bluetoothGatt, String uuid) {
        final UUID serviceUuid = UUID.fromString(getServiceUUID());
        final UUID characteristicUuid = UUID.fromString(uuid);

        final BluetoothGattService service = bluetoothGatt.getService(serviceUuid);
        return service.getCharacteristic(characteristicUuid);
    }
}