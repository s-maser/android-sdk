package io.relayr.ble.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public class MasterModuleService extends BaseService {
    protected MasterModuleService(BluetoothDevice device, BluetoothGatt gatt, BluetoothGattReceiver receiver) {
        super(device, gatt, receiver);
    }
}
