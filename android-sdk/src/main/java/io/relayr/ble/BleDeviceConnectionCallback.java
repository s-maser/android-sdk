package io.relayr.ble;

public interface BleDeviceConnectionCallback {

	void onConnect(BleDevice device);
	void onDisconnect(BleDevice device);
	void onError(BleDevice device, String error);
	void onWriteSuccess(BleDevice device, BleDeviceCharacteristic characteristic);
	void onWriteError(BleDevice device, BleDeviceCharacteristic characteristic, int errorStatus);
}
