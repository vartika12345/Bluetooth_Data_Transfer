package com.example.user.bluetooth_data_transfer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU =1;
    private BluetoothAdapter mBluetoothAdapter;
    private  BluetoothDevice mDevice = null;
    private TextView tvData;
    ConnectThread mConnectThread;
    Handler mHandler;
    private  Boolean flag = false;
    BluetoothAdapter.LeScanCallback leScanCallback;
    BluetoothGattCallback mGattCallback;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private Button btnTransfer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvData = (TextView)findViewById(R.id.tvData);
        btnTransfer = (Button)findViewById(R.id.btnTransfer);

        // Use this check to determine whether BLE is supported on the device. Then
// you can selectively disable BLE-related features.
        checkBLESupported();

        // Initializes Bluetooth adapter.
        setUpBluetoothAdapter();

        //enableBluetooth and option to enable bluetooth services
        enableBluetooth();
        
        //retrieve actual bluetooth devices
        getPairedDevices();
        
        //establish connection between bluetooth devices on separate thread
        getConnection();

        //for ble connection
       // mDevice.connectGatt(getApplicationContext(), true, mGattCallback);


      /*  leScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                // your implementation here
            }
        };
*/

       /*  mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

                //Connection established
                if (status == BluetoothGatt.GATT_SUCCESS
                        && newState == BluetoothProfile.STATE_CONNECTED) {

                    //Discover services
                    gatt.discoverServices();

                } else if (status == BluetoothGatt.GATT_SUCCESS
                        && newState == BluetoothProfile.STATE_DISCONNECTED) {

                    //Handle a disconnect event

                }
            }

             @Override
             public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                 // this will get called anytime you perform a read or write characteristic operation
             }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {

                //Now we can start reading/writing characteristics
            }

             @Override
             public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                 super.onCharacteristicRead(gatt, characteristic, status);
             }

             @Override
             public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                 super.onCharacteristicWrite(gatt, characteristic, status);
             }

             @Override
             public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                 super.onDescriptorRead(gatt, descriptor, status);
             }

             @Override
             public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                 super.onDescriptorWrite(gatt, descriptor, status);
             }
         };

*/

        //send data from connected thread to UI thread
       /*  mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                byte[] writeBuf = (byte[]) msg.obj;
                int begin = (int)msg.arg1;
                int end = (int)msg.arg2;
                switch(msg.what) {
                    case 1:
                        String writeMessage = new String(writeBuf);
                        writeMessage = writeMessage.substring(begin, end);
                        tvData.setText(writeMessage);
                        break;
                }
            }
        };*/




    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode == DISCOVER_DURATION && resultCode == REQUEST_ENABLE_BT){
            Intent intent =  new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            File f  = new File(Environment.getExternalStorageDirectory(),"Bluetooth Low Energy Device.txt");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

            PackageManager pm = getPackageManager();
            List<ResolveInfo> appsList = pm.queryIntentActivities(intent,0);

            if(appsList.size()>0)
            {
                String packageName = null;
                String className = null;
                boolean found = false;

                for(ResolveInfo info: appsList)
                {
                    packageName = info.activityInfo.packageName;
                    if(packageName.equals("com.android.bluetooth"))
                    {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }

                if(!found)
                {
                    Toast.makeText(getApplicationContext(),"Bluetooth haven't been found",Toast.LENGTH_SHORT).show();
                }
                else {
                    intent.setClassName(packageName,className);
                    startActivity(intent);
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Bluetooth is Cancelled",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void getConnection() {
        if(flag) {
            mConnectThread = new ConnectThread(mDevice, mBluetoothAdapter, mHandler);
            mConnectThread.start();
        }
        else {
            Toast.makeText(getApplicationContext(),"There is no bluetooth device available",Toast.LENGTH_SHORT).show();
        }
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mDevice = device;
                flag = true;
                Toast.makeText(getApplicationContext(),mDevice.getName(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableBluetooth() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.putExtra(mBluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,DISCOVER_DURATION);
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
