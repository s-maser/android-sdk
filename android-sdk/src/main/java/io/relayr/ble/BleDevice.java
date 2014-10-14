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

/**
 * A class representing a relayr BLE Device
 */
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

    /**
     * The name of the Device
     * @return a string containing the name of the device.
     */
    public String getName() {
		return name;
	}

    /**
     * The Id of the Device
     * @return a string containing the Id of the device.
     */
	public String getAddress() {
		return address;
	}

    /**
     * The mode in which a Device is in
     * This can be either ON_BOARDING, CONNECTED_TO_MASTER_MODULE, DIRECT_CONNECTION or UNKNOWN
     * @return mode of type {@link io.relayr.ble.BleDeviceMode}
     */
	public BleDeviceMode getMode() {
		return mode;
	}

    /**
     * The type of the Device
     * Possibe values are: WunderbarHTU, WunderbarGYRO, WunderbarLIGHT, WunderbarMIC, WunderbarBRIDG,
     * WunderbarIR, WunderbarApp, Unknown;
     * @return type of type {@link io.relayr.ble.BleDeviceType}.
     */
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
