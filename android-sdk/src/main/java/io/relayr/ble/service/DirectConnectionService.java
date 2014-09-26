package io.relayr.ble.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public class DirectConnectionService extends BaseService {

    private DirectConnectionService(BluetoothDevice device, BluetoothGatt gatt, BluetoothGattReceiver receiver) {
        super(device, gatt, receiver);
    }

    public void readSensorId() {} // 2010

    //public void readBeaconFrequency() {} // 2011

    public void readSensorFrequency() {} // 2012
    public void writeSensorFrequency() {} // 2012

    public void readSensorLedState() {}// 2013
    public void writeSensorLedState() {}// 2013

    //public void writeBeaconFrequency() {} // 2014

    public void writeSensorConfig() {} // 2015

}
