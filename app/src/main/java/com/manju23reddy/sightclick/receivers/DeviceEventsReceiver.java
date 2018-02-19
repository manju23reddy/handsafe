package com.manju23reddy.sightclick.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class DeviceEventsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("android.intent.action.NEW_OUTGOING_CALL")){

        }
        else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            int state = 0;

            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }

            onCallStateChanged(context, state, number);

        }

    }

    protected void onInCommingCallRinging(Context context, String number){};
    protected void onInCommingCallAnswered(Context context, String number){};
    protected void onInCommingCallEnded(Context context, String number){};


    public void onCallStateChanged(Context context, int state, String number){
        switch (state){
            case TelephonyManager.CALL_STATE_OFFHOOK:
                onInCommingCallAnswered(context, number);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                onInCommingCallEnded(context, number);
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                onInCommingCallRinging(context, number);
                break;
            default:
                return;
        }
    }
}
