package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BleDevicesScanner implements Runnable, BluetoothAdapter.LeScanCallback {

    private static final String TAG = "BleDevicesScanner";

    public static final long DEFAULT_SCAN_PERIOD = 7000;

    private BluetoothAdapter bluetoothAdapter;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final LeScansPoster leScansPoster;

    private long scanPeriod = DEFAULT_SCAN_PERIOD;
    private Thread scanThread;
    private volatile boolean isScanning = false;

    public BleDevicesScanner(BluetoothAdapter adapter, BluetoothAdapter.LeScanCallback callback) {
        bluetoothAdapter = adapter;
        leScansPoster = new LeScansPoster(callback);
    }

    public synchronized void setScanPeriod(long scanPeriod) {
        this.scanPeriod = scanPeriod < 0 ? DEFAULT_SCAN_PERIOD : scanPeriod;
    }

    public boolean isScanning() {
        return scanThread != null && scanThread.isAlive();
    }

    public synchronized void start() {
        if (isScanning()) return;

        if (scanThread != null) scanThread.interrupt();

        scanThread = new Thread(this);
        scanThread.setName(TAG);
        scanThread.start();
    }

    public synchronized void stop() {
        if (!isScanning()) return;

        isScanning = false;
        bluetoothAdapter.stopLeScan(this);

        if (scanThread != null) {
            scanThread.interrupt();
            scanThread = null;
        }
    }

    @Override
    public void run() {
        try {
            isScanning = true;
            do {
                synchronized (this) {
                    bluetoothAdapter.startLeScan(this);
                }

                Thread.sleep(scanPeriod);

                // although it should never be null sometimes it happens to be
                synchronized (this) {
                    if (bluetoothAdapter != null) bluetoothAdapter.stopLeScan(this);
                }
            } while (isScanning);
        } catch (InterruptedException ignore) {
        } finally {
            synchronized (this) {
                bluetoothAdapter.stopLeScan(this);
            }
        }
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        synchronized (leScansPoster) {
            if(device.getAddress().contains("0A:B6"))
                Log.e("MM", "" + rssi);
            leScansPoster.set(device, rssi, scanRecord);
            mainThreadHandler.post(leScansPoster);
        }
    }

    private static class LeScansPoster implements Runnable {
        private final BluetoothAdapter.LeScanCallback leScanCallback;

        private BluetoothDevice device;
        private int rssi;
        private byte[] scanRecord;

        private LeScansPoster(BluetoothAdapter.LeScanCallback leScanCallback) {
            this.leScanCallback = leScanCallback;
        }

        public void set(BluetoothDevice device, int rssi, byte[] scanRecord) {
            this.device = device;
            this.rssi = rssi;
            this.scanRecord = scanRecord;
        }

        @Override
        public void run() {
            leScanCallback.onLeScan(device, rssi, scanRecord);
        }
    }
}