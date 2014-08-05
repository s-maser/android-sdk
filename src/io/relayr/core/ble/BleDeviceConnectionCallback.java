package io.relayr.core.ble;

public interface BleDeviceConnectionCallback {

	public void onConnect(BleDevice device);
	public void onDisconnect(BleDevice device);
	public void onError(BleDevice device, String error);
	public void onWriteSuccess(BleDevice device, BleDeviceCharacteristic characteristic);
	public void onWriteError(BleDevice device, BleDeviceCharacteristic characteristic, int errorStatus);
}
