 package com.relayr.core.ble;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.relayr.Relayr_Application;
import com.relayr.Relayr_Commons;
import com.relayr.core.ble.device.Relayr_BLEDevice;
import com.relayr.core.ble.device.Relayr_BLEDeviceStatus;
import com.relayr.core.ble.device.Relayr_BLEDeviceType;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Relayr_BleListener {

	private static BluetoothAdapter bluetoothAdapter;
	private static Relayr_BleDevicesScanner scanner;
	public static Relayr_DeviceManager discoveredDevices;

	private final static int REQUEST_ENABLE_BT = 1;

	static {
		scanner = null;
		discoveredDevices = new Relayr_DeviceManager();
	}

	public static boolean init() {
		Log.d(Relayr_BleListener.class.toString(), "BleListener init called");
		if (Relayr_Commons.isSDK18()) {
			Log.d(Relayr_BleListener.class.toString(), "BleListener init starting");
			final int bleStatus = Relayr_BleUtils.getBleStatus(Relayr_Application.currentActivity());
			switch (bleStatus) {
			case Relayr_BleUtils.STATUS_BLE_NOT_AVAILABLE:
			case Relayr_BleUtils.STATUS_BLUETOOTH_NOT_AVAILABLE:
				Log.d(Relayr_BleListener.class.toString(), "Bluethooth not available");
				return false;
			default:
				Activity currentActivity = Relayr_Application.currentActivity();
				bluetoothAdapter = Relayr_BleUtils.getBluetoothAdapter(currentActivity);
				if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
				    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				    currentActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				    return false;
				}
				scanner = new Relayr_BleDevicesScanner(bluetoothAdapter, new BluetoothAdapter.LeScanCallback() {
					@Override
					public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
						if (!discoveredDevices.isDeviceDiscovered(device.getAddress()) && (Relayr_BLEDeviceType.getDeviceType(device.getName()) != Relayr_BLEDeviceType.Unknown)) {
							Log.d(Relayr_BleListener.class.toString(), "New device: "+ device.getName() + " [" + device.getAddress() + "]");
							Relayr_BLEDevice relayrDevice = new Relayr_BLEDevice(device);
							discoveredDevices.addNewDevice(relayrDevice.getAddress(), null);
							relayrDevice.setStatus(Relayr_BLEDeviceStatus.CONFIGURING);
							Log.d(Relayr_BleListener.class.toString(), "Device cofiguration start: "+ relayrDevice.toString());
							relayrDevice.connect();
						}
					}
				});
				scanner.setScanPeriod(-1);
				return true;
			}
		}
		return false;
	}

	public static void start() {
		if (Relayr_Commons.isSDK18()) {
			if (scanner != null) {
				if (!scanner.isScanning()) {
					discoveredDevices.clearDiscoveredDevices();
					scanner.start();
					Log.d(Relayr_BleListener.class.toString(), "Scanner start: " + scanner.toString());
				}
			} else {
				if (init()) {
					scanner.start();
					Log.d(Relayr_BleListener.class.toString(), "New scanner start: " + scanner.toString());
				}

			}
		}
	}

	public static void refresh() {
		if (Relayr_Commons.isSDK18()) {
			if (scanner != null) {
				discoveredDevices.refreshDiscoveredDevices();
				if (!scanner.isScanning()) {
					scanner.start();
					Log.d(Relayr_BleListener.class.toString(), "Scanner start: " + scanner.toString());
				}
			} else {
				if (init()) {
					scanner.start();
					Log.d(Relayr_BleListener.class.toString(), "New scanner start: " + scanner.toString());
				}
			}
		}
	}

	public static void stop() {
		if (Relayr_Commons.isSDK18()) {
			scanner.stop();
			Log.d(Relayr_BleListener.class.toString(), "Scanner stop: " + scanner.toString());
		}
	}

	public static boolean isScanning() {
		if (Relayr_Commons.isSDK18()) {
			return scanner == null? false : scanner.isScanning();
		} else {
			return false;
		}
	}

	public static Relayr_DeviceManager getDeviceManager() {
		return discoveredDevices;
	}
}
