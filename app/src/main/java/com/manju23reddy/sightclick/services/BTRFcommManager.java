package com.manju23reddy.sightclick.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.database.Cursor;
import android.renderscript.ScriptGroup;
import android.util.Log;

import com.manju23reddy.sightclick.BuildConfig;
import com.manju23reddy.sightclick.db.BTClientDBContract;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Manjunath Reddy on 2/2/2018.
 */

public class BTRFcommManager {

    final static String TAG = BTRFcommManager.class.getSimpleName();
    final static String MY_UUID = BuildConfig.UUID;

    private ConnectThread mRFCommThread = null;

    private BluetoothAdapter mBtAdapter;
    private Context mContext = null;

    private final static class BTRFcommInstance{
        public final static BTRFcommManager INSTANCE = new BTRFcommManager();
    }

    private BTRFcommManager(){
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BTRFcommManager getInstance(){
        return BTRFcommInstance.INSTANCE;
    }

    public void setContext(Context context){
        if (null == mContext){
            mContext = context;
        }
    }

    public void openRFCommConnection(){
        if (null == mRFCommThread){
            //start device
            if (!mBtAdapter.isEnabled()){
                return;
            }
            else{
                //get Paired Devices
                //get Saved BT Device address from DB
                Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
                if (pairedDevices.size() > 0){
                    Cursor result = mContext.getContentResolver().query(
                            BTClientDBContract.DEVICE_CONTENT_URI,
                            null,
                            null,
                            null,
                            null
                            );

                    String MAC_ID = null;
                    if (result != null){
                        while (result.moveToNext()){
                            MAC_ID = result.getString(result.getColumnIndex
                                    (BTClientDBContract.BTDeviceTable.COLUMN_DEVICE_BT_ADDRESS));
                            break;
                        }
                    }
                    else{
                        Log.e(TAG, "No BT Device paired with Application");
                        return;
                    }
                    for (BluetoothDevice device : pairedDevices){
                        if ( null != MAC_ID && device.getAddress().equals(MAC_ID)){
                            mRFCommThread = new ConnectThread(device);
                            break;
                        }
                    }
                    if (null != mRFCommThread) {
                        mRFCommThread.start();
                    }
                }
            }
        }

    }

    public void closeRFCommConnection(){
        if (null != mRFCommThread){
            mRFCommThread.disconnect();
        }
        mRFCommThread = null;
    }

    public void sendCommandToConnectedServer(String message){
        if (null != mRFCommThread){
            mRFCommThread.sendCmdToDevice(message);
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final byte delimiter = 10; //this is ASCII code for a newline character.
        private OutputStream mToDevice;
        private InputStream mFromDevice;

        public ConnectThread(BluetoothDevice device){
            BluetoothSocket temp = null;
            mmDevice = device;
            try{
                temp = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));

            }
            catch (Exception ee){
                ee.printStackTrace();
                Log.e(TAG, "run :"+ee.getMessage());
            }
            mmSocket = temp;
        }

        @Override
        public void run() {
            ByteBuffer tempByteBuffer = ByteBuffer.allocate(1024);
            mBtAdapter.cancelDiscovery();

            try{
                mmSocket.connect();
                mToDevice = mmSocket.getOutputStream();
                mFromDevice = mmSocket.getInputStream();

                //until device is connected with phone
                //keep reading incoming commands from device
                while ( mmSocket != null && mmSocket.isConnected()){
                    try{
                        int bytesAvailable = mFromDevice.available();
                        if (bytesAvailable > 0){
                            byte[] incomingPackets = new byte[bytesAvailable];
                            mFromDevice.read(incomingPackets);
                            for(int i = 0; i < bytesAvailable; i++){
                                byte currentByte = incomingPackets[i];
                                //check if the current byte is \n then send the command to command
                                //Handler
                                if (delimiter == currentByte){
                                    byte[] encodedBytes = new byte[tempByteBuffer.remaining()];
                                    tempByteBuffer.get(encodedBytes);
                                    tempByteBuffer.clear();

                                    //send the byte
                                }
                                else{//copy bytes till we encounter delimiter

                                    tempByteBuffer.put(currentByte);
                                }
                            }

                        }
                    }
                    catch (Exception ee){
                        Log.e(TAG, "run :"+ee.getMessage());
                    }
                }



            }
            catch (Exception ee){
                ee.printStackTrace();
                Log.e(TAG, ee.getMessage());

                try{
                    mmSocket.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "run :"+e.getMessage());
                }
            }
        }

        public void disconnect(){
            try{
                if (null != mmSocket && mmSocket.isConnected()){
                    if (null != mFromDevice){
                        mFromDevice.close();
                    }
                    if (null != mToDevice){
                        mToDevice.close();
                    }
                    mmSocket.close();
                }
            }
            catch (Exception ee){
                ee.printStackTrace();
                Log.e(TAG, "disconnect :"+ee.getMessage());
            }
        }

        public void sendCmdToDevice(String cmd){
            try{
                if ( mmSocket != null && mmSocket.isConnected()){
                    if (mToDevice != null){
                        mToDevice.write(cmd.getBytes());
                        mToDevice.flush();
                    }
                }
            }
            catch (Exception ee){
                Log.e(TAG, "sendCmdToDevice :"+ee.getMessage());
            }
        }


    }


}
