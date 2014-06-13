package com.relayr.core.event_listeners;

import java.util.Collection;
import java.util.List;

import com.relayr.core.ble.Relayr_BLEDevice;

public interface Relayr_BLEScanningEventListener {

	public void onDeviceListModified(Collection<Relayr_BLEDevice> devices);

}
