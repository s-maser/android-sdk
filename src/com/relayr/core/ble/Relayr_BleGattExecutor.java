package com.relayr.core.ble;

import java.util.ArrayList;
import java.util.UUID;

import com.relayr.core.ble.device.Relayr_BLEDevice;
import com.relayr.core.ble.device.Relayr_BLEDeviceStatus;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Relayr_BleGattExecutor extends BluetoothGattCallback {

	Relayr_BLEDevice device;
	private UUID RELAYR_NOTIFICATION_CHARACTERISTIC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

	public Relayr_BleGattExecutor(Relayr_BLEDevice device) {
		super();
		this.device = device;
	}

/*
    public interface ServiceAction {
        public static final ServiceAction NULL = new ServiceAction() {
            @Override
            public boolean execute(BluetoothGatt bluetoothGatt) {
                // it is null action. do nothing.
                return true;
            }
        };

        public boolean execute(BluetoothGatt bluetoothGatt);
    }

    private final LinkedList<Relayr_BleGattExecutor.ServiceAction> queue = new LinkedList<ServiceAction>();
    private volatile ServiceAction currentAction;

    public void update(final TiSensor sensor) {
        queue.add(sensor.update());
    }

    public void enable(TiSensor sensor, boolean enable) {
        final ServiceAction[] actions = sensor.enable(enable);
        for ( ServiceAction action : actions ) {
            this.queue.add(action);
        }
    }

    public void execute(BluetoothGatt gatt) {
        if (currentAction != null)
            return;

        boolean next = !queue.isEmpty();
        while (next) {
            final Relayr_BleGattExecutor.ServiceAction action = queue.pop();
            currentAction = action;
            if (!action.execute(gatt))
                break;

            currentAction = null;
            next = !queue.isEmpty();
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);

        currentAction = null;
        execute(gatt);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);

        currentAction = null;
        execute(gatt);
    }*/

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
    	if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
    		Log.d(Relayr_BleGattExecutor.class.toString(), "Device connected");
    		this.device.setStatus(Relayr_BLEDeviceStatus.CONNECTED);
    		this.device.triggerObservers();
    		gatt.discoverServices();
    	} else {
    		if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
    			Log.d(Relayr_BleGattExecutor.class.toString(), "Device disconnected");
    			this.device.setStatus(Relayr_BLEDeviceStatus.DISCONNECTED);
    			this.device.triggerObservers();
    		}
    	}
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
    	ArrayList<BluetoothGattService> services = (ArrayList<BluetoothGattService>) gatt.getServices();
    	for (BluetoothGattService service:services) {
    		String serviceUUID = service.getUuid().toString();
    		Log.d(Relayr_BleGattExecutor.class.toString(), "Discovered service: " + serviceUUID);
    		if (serviceUUID.equals(this.device.getType().serviceUUID)) {
    			ArrayList<BluetoothGattCharacteristic> characteristics = (ArrayList<BluetoothGattCharacteristic>) service.getCharacteristics();
    			for (BluetoothGattCharacteristic characteristic:characteristics) {
    				String characteristicUUID = characteristic.getUuid().toString();
    				Log.d(Relayr_BleGattExecutor.class.toString(), "Discovered characteristic: " + characteristicUUID);
    				if (characteristicUUID.equals(this.device.getType().readCharacteristicUUID)) {
    					gatt.setCharacteristicNotification(characteristic, true);
    					BluetoothGattDescriptor descriptor = characteristic.getDescriptor(RELAYR_NOTIFICATION_CHARACTERISTIC);
    				    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    				    gatt.writeDescriptor(descriptor);
    				    break;
    				}
    			}
    			break;
    		}
    	}
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
    	Log.d(Relayr_BleGattExecutor.class.toString(), "Characteristic readed: " + characteristic.getUuid());
    	Log.d(Relayr_BleGattExecutor.class.toString(), "Characteristic readed value: " + characteristic.getValue());
        this.device.setValue(characteristic.getValue());
    }
}
