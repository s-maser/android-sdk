package com.relayr.core.ble;

import com.relayr.Relayr_Event;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Created by steven on 9/3/13.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Relayr_BleGattExecutor extends BluetoothGattCallback {

	Relayr_BLEDevice device;

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
    		Intent intent = new Intent();
    		intent.setAction(Relayr_Event.DEVICE_CONNECTED);
    		intent.putExtra("device", device);
    	} else {
    		if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
    			Log.d(Relayr_BleGattExecutor.class.toString(), "Device disconnected");
    			Intent intent = new Intent();
        		intent.setAction(Relayr_Event.DEVICE_DISCONNECTED);
        		intent.putExtra("device", device);
    		}
    	}
    }
/*
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic,
                                     int status) {
        currentAction = null;
        execute(gatt);
    }*/
}
