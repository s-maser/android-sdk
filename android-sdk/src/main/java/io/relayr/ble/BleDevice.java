package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import io.relayr.ble.service.BaseService;
import io.relayr.ble.service.DirectConnectionService;
import io.relayr.ble.service.MasterModuleService;
import io.relayr.ble.service.OnBoardingService;
import rx.Observable;

import static io.relayr.ble.BleDeviceMode.DIRECT_CONNECTION;
import static io.relayr.ble.BleDeviceMode.ON_BOARDING;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleDevice {

	private final BleDeviceMode mode;
	private final BluetoothDevice bluetoothDevice;
	private final BleDeviceType type;
    private final String address;
    private final String name;

    BleDevice(BluetoothDevice bluetoothDevice, String address, String name, BleDeviceMode mode) {
		this.bluetoothDevice = bluetoothDevice;
		this.mode = mode;
		this.type = BleDeviceType.getDeviceType(bluetoothDevice.getName());
        this.address = address;
        this.name = name;
	}

    public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public BleDeviceMode getMode() {
		return mode;
	}

	public BleDeviceType getType() {
		return type;
	}

    public Observable<? extends BaseService> connect() {
        if (mode == ON_BOARDING) {
            return OnBoardingService.connect(this, bluetoothDevice).cache();
        } else if (mode == DIRECT_CONNECTION) {
            return DirectConnectionService.connect(this, bluetoothDevice).cache();
        } else {
            return MasterModuleService.connect(this, bluetoothDevice).cache();
        }
    }

	@Override
	public String toString() {
		return name + " - [" + address + "] MODE: " + mode.toString();
	}
}
