package com.example.user.bluetooth_data_transfer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by USER on 05-06-2016.
 */
public class ConnectThread extends Thread {

    private final BluetoothSocket mmSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private final BluetoothDevice mmDevice;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public ConnectThread(BluetoothDevice device,BluetoothAdapter mBluetoothAdapter) {
        BluetoothSocket tmp = null;
        this.mBluetoothAdapter = mBluetoothAdapter;
        mmDevice = device;
        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }
    public void run() {
        mBluetoothAdapter.cancelDiscovery();
        try {
            mmSocket.connect();
        } catch (IOException connectException) {
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
    }
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}

