package com.example.user.bluetooth_data_transfer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by USER on 05-06-2016.
 */
public class ConnectThread extends Thread {

    private final BluetoothSocket mmSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mmDevice;
    ConnectedThread mConnectedThread;
    Handler mHandler;
    Boolean flag = false;


    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public ConnectThread(BluetoothDevice device,BluetoothAdapter mBluetoothAdapter,Handler mHandler) {
        BluetoothSocket tmp = null;
        this.mBluetoothAdapter = mBluetoothAdapter;
        this.mHandler = mHandler;
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

        //Used for sending and receiving data after connection is made
        if(flag) {

            Log.i("Connection","yes");
            mConnectedThread = new ConnectedThread(mmSocket, mHandler);
            mConnectedThread.start();
        }
    }
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}

