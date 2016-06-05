package com.example.user.bluetooth_data_transfer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use this check to determine whether BLE is supported on the device. Then
// you can selectively disable BLE-related features.
        checkBLESupported();

        // Initializes Bluetooth adapter.
        setUpBluetoothAdapter();

        //enableBluetooth and option to enable bluetooth services
        enableBluetooth();



    }

    private void enableBluetooth() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(),"Turned on",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Already on", Toast.LENGTH_SHORT).show();
        }
        }


    private void setUpBluetoothAdapter() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    private void checkBLESupported() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            Toast.makeText(this, R.string.ble_supported, Toast.LENGTH_SHORT).show();
        }
    }
}
