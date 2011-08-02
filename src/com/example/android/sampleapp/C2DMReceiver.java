package com.example.android.sampleapp;

import android.content.Context;
import android.content.Intent;
import com.google.android.c2dm.C2DMBaseReceiver;

import java.io.IOException;

/**
 * NOTE: C2DM is supported in API v.2.2 and above but is safely ignored in lower versions.
 *
 * C2DMBaseReceiver.runIntentInService demands that this class exist at the same package
 * defined in the AndroidManifest.package; ex: com.example.android.sampleapp.C2DMReceiver
 *
 * Read and understand http://code.google.com/android/c2dm/, but note that the XML listed in the Manifest section
 * is wrong!  Use the pattern you see in AndroidManifest.xml of this project instead.
 */
public class C2DMReceiver extends C2DMBaseReceiver {

    /**
     * The senderId is a google email address, such as a gmail address or a apps-for-your-domain gmail address.
     * Use the same address when registering a device with C2DM.
     */
    public C2DMReceiver() {
        super("example@a_google_acount.com");
    }

    /**
     * Called when a C2DM message is received.
     * context: a Context
     * intent: this is the Intent broadcast by Android.  This Intent has extras with the format described
     * at http://code.google.com/android/c2dm/#push.
     *
     * Getting data from the intent: if a C2DM message sent by a server with params "data.myInfo"
     * retrieve that data by calling intent.getExtras().getString("myInfo") -- *not* "data.myInfo"
     *
     * Setting Notifications in the Notification Bar: this is done by passing PendingIntents to a NotificationManager.
     * See http://developer.android.com/reference/android/app/PendingIntent.html
     *
     * Handling the "Clear All" button in the Notification Bar: set PendingIntent.deleteIntent. This will be called
     * automatically by Android. You will need to handle the broadcast of that Intent yourself.
     */
    @Override
    protected void onMessage(Context context, Intent intent) {
    }

    /**
     * @param context
     * @param registrationId - key Google uses to identify this device.
     * @throws IOException
     *
     * See http://code.google.com/android/c2dm/#handling_reg
     *
     * Overview: You will need to broadcast an Intent to register with the C2DM service; for example, when a
     * user logs in to the application -- see http://code.google.com/android/c2dm/#registering.  This method is
     * called if that registration succeeds.
     *
     * Warning: Failure to unregister a device before re-registering might result in devices with multiple
     * registration_ids and might receiving duplicate notifications.
     */
    @Override
    public void onRegistered(Context context, String registrationId) throws IOException {
        super.onRegistered(context, registrationId);
    }

    /**
     * @param context
     * @param errorId one of C2DMBaseReceiver.ERR_SERVICE_NOT_AVAILABLE, etc.
     *
     * Called on Registration error. See http://code.google.com/android/c2dm/#handling_reg
     */
    @Override
    public void onError(Context context, String errorId) {
    }

    /**
     * @param context
     *
     * Overview: You will need to broadcast an Intent to unregister with the C2DM service; for example, when a
     * user logs out to the application -- see http://code.google.com/android/c2dm/#unregistering.  This method is
     * called if that un-registration succeeds.
     *
     * Warning: Failure to unregister a device before re-registering might result in devices with multiple
     * registration_ids and might receiving duplicate notifications.
     */
    @Override
    public void onUnregistered(Context context) {
        super.onUnregistered(context);
    }
}
