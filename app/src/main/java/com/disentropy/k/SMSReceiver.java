package com.disentropy.k;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;


public class SMSReceiver extends BroadcastReceiver {
    public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(ACTION)){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++){
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                for (SmsMessage message : messages){

                    String messageFrom = message.getDisplayOriginatingAddress();
                    String messageBody = message.getDisplayMessageBody();
                    String messageNumber = message.getOriginatingAddress();

                    SmsManager manager = SmsManager.getDefault();
                    /*
                    PendingIntent sentPI;
                    String SENT = "SMS_SENT";
                    sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
                    manager.sendTextMessage(messageNumber, null, context.getString(R.string.k_message), sentPI, null);
                    */
                    manager.sendTextMessage(messageNumber, null, context.getString(R.string.k_message), null, null);
                    Toast.makeText(context, "Replying: TO: " + messageNumber + " Message: " + context.getString(R.string.k_message), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

