package com.manju23reddy.sightclick.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.manju23reddy.sightclick.receivers.DeviceEventsReceiver;

/**
 * Created by MReddy3 on 2/1/2018.
 */


public class BTRfcommClientService extends Service {

    private final static String TAG = BTRfcommClientService.class.getSimpleName();

    private BTRFcommManager mRFCommManager = null;

    private Messenger mMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TelephonyManager.CALL_STATE_RINGING:
                    //check if the phone number is saved in DB as Friend, if yes then
                    //send command to device
                    if (null != mRFCommManager){
                        //open RFComm
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //probably do nothing
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //stop RFComm Connection
                    break;

            }
        }
    });

    private DeviceEventsReceiver mEventsReceiver = null;

    @Override
    public void onCreate() {
        super.onCreate();
        initRFComm();
        mEventsReceiver = new DeviceEventsReceiver(){
            @Override
            protected void onInCommingCallRinging(Context context, String number) {
                try {
                    Message msg = Message.obtain();
                    msg.what = TelephonyManager.CALL_STATE_RINGING;
                    msg.obj = number;
                    mMessenger.send(msg);
                }
                catch (Exception ee){
                    Log.e(TAG, ee.getMessage());
                }
            }

            @Override
            protected void onInCommingCallAnswered(Context context, String number) {
                try {
                    Message msg = Message.obtain();
                    msg.what = TelephonyManager.CALL_STATE_OFFHOOK;
                    msg.obj = number;
                    mMessenger.send(msg);
                }
                catch (Exception ee){
                    Log.e(TAG, ee.getMessage());
                }
            }

            @Override
            protected void onInCommingCallEnded(Context context, String number) {
                try {
                    Message msg = Message.obtain();
                    msg.what = TelephonyManager.CALL_STATE_IDLE;
                    msg.obj = number;
                    mMessenger.send(msg);
                }
                catch (Exception ee){
                    Log.e(TAG, ee.getMessage());
                }
            }
        };

    }

    private void initRFComm(){
        if (null == mRFCommManager){
            mRFCommManager = BTRFcommManager.getInstance();
            mRFCommManager.setContext(getApplicationContext());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

}
