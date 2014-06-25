 package com.relayr.core.ble;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.relayr.Relayr_Application;
import com.relayr.Relayr_Commons;
import com.relayr.Relayr_Event;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Relayr_BleListener {

	private static BluetoothAdapter bluetoothAdapter;
	private static Relayr_BleDevicesScanner scanner;
	public static HashMap<String,BluetoothDevice> discoveredDevices;

	private final static int REQUEST_ENABLE_BT = 1;

	static {
		scanner = null;
		discoveredDevices = new HashMap<String, BluetoothDevice>();
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
				}
				scanner = new Relayr_BleDevicesScanner(bluetoothAdapter, new BluetoothAdapter.LeScanCallback() {
					@Override
					public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
						if (!discoveredDevices.containsKey(device.getAddress())) {
							Log.d(Relayr_BleListener.class.toString(), "New device: "+ device.getName() + " [" + device.getAddress() + "]");
							Relayr_BLEDevice relayrDevice = new Relayr_BLEDevice(device.getName(), device.getAddress());
							discoveredDevices.put(device.getAddress(), device);
							Intent intent = new Intent();
							intent.setAction(Relayr_Event.DEVICE_DETECTED);
							intent.putExtra("device", relayrDevice);
							intent.putExtra("device_list", publishDetectedDevicesList());
							Relayr_Application.currentActivity().sendBroadcast(intent);
						}
					}
				});
				scanner.setScanPeriod(1000);
				return true;
			}
		}
		return false;
	}

	public static void start() {
		if (Relayr_Commons.isSDK18()) {
			if (scanner != null) {
				if (!scanner.isScanning()) {
					discoveredDevices.clear();
					scanner.start();
					Log.d(Relayr_BleListener.class.toString(), "Scanner start: " + scanner.toString());
				}
			} else {
				if (init()) {
					scanner.start();
					Log.d(Relayr_BleListener.class.toString(), "Scanner start: " + scanner.toString());
				}

			}
		}
	}

	public static void stop() {
		if (Relayr_Commons.isSDK18()) {
			scanner.stop();
		}
	}

	public static boolean isScanning() {
		if (Relayr_Commons.isSDK18()) {
			return scanner.isScanning();
		} else {
			return false;
		}
	}

	private static ArrayList<Relayr_BLEDevice> publishDetectedDevicesList() {
		ArrayList<Relayr_BLEDevice> devices = new ArrayList<Relayr_BLEDevice>();
		for(BluetoothDevice device:discoveredDevices.values()) {
			devices.add(new Relayr_BLEDevice(device.getName(), device.getAddress()));
		}
		return devices;
	}
}
