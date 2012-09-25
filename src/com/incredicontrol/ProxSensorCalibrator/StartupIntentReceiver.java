package com.incredicontrol.ProxSensorCalibrator;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class StartupIntentReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			String ps_canc = RezoundProximitySensorCalibratorActivity.ps_canc;
			String values = Preferences.getPrefs(context, "Values", "");
			try {
				RezoundProximitySensorCalibratorActivity.writeFileRoot(ps_canc, values);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			      String Message = "Problem writing proximity calibration boot values";
			      Log.e(RezoundProximitySensorCalibratorActivity.TAG, Message);
			}
			   // Toast.makeText(context, values + " set", Toast.LENGTH_SHORT).show();
		}
	}
}
