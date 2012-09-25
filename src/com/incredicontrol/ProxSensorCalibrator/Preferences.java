package com.incredicontrol.ProxSensorCalibrator;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	public static void setPrefs(Context context, String setting, String value) {
		SharedPreferences widgetSettings = context.getSharedPreferences("ProxCalibrationValues", 0);
		SharedPreferences.Editor prefEditor = widgetSettings.edit();
		prefEditor.putString(setting, value);
		prefEditor.commit();
	}
	
	public static String getPrefs(Context context, String setting, String defaultValue) {
		String value;
		SharedPreferences widgetSettings = context.getSharedPreferences("ProxCalibrationValues", 0);
		value = widgetSettings.getString(setting, defaultValue);
		return value;
	}
	
}
