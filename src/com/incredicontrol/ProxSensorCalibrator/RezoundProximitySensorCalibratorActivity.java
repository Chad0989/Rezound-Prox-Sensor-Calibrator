package com.incredicontrol.ProxSensorCalibrator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class RezoundProximitySensorCalibratorActivity extends Activity implements SensorEventListener {
    /** Called when the activity is first created. */
	private SensorManager sensorManager;
	private Sensor proxSensor;
	private Integer ps1;
	private Integer ps2;
	final static String ps_canc = "/sys/devices/virtual/optical_sensors/proximity/ps_canc";
    static TextView ps1TextView = null;
    static TextView ps2TextView = null;
    CheckBox checkBoxSetOnBoot;
	
	static final String TAG = "Rezound Proximity Sensor Calibrator";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        
        final SeekBar ps1SeekBar = (SeekBar) findViewById(R.id.ps1SeekBar);
        Button ps1ButtonMinus = (Button) findViewById(R.id.ps1ButtonMinus);
        Button ps1ButtonPlus = (Button) findViewById(R.id.ps1ButtonPlus);

        final SeekBar ps2SeekBar = (SeekBar) findViewById(R.id.ps2SeekBar);
        Button ps2ButtonMinus = (Button) findViewById(R.id.ps2ButtonMinus);
        Button ps2ButtonPlus = (Button) findViewById(R.id.ps2ButtonPlus);
        
        ps1TextView = (TextView) findViewById(R.id.ps1TextView);
        ps2TextView = (TextView) findViewById(R.id.ps2TextView);
        
        checkBoxSetOnBoot = (CheckBox) findViewById(R.id.checkBoxSetOnBoot);
        String boot = Preferences.getPrefs(getApplicationContext(), "Boot", "0");
        if (Integer.parseInt(boot) == 1) {
        	checkBoxSetOnBoot.setChecked(true);
        }
        
        checkBoxSetOnBoot.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (checkBoxSetOnBoot.isChecked()) {
					Preferences.setPrefs(getApplicationContext(), "Boot", "1");
					save();
				} else {
					Preferences.setPrefs(getApplicationContext(), "Boot", "0");
				}
				
			}
        	
        });
        
        readValues();
        
        ps1TextView.setText(ps1.toString());
        ps1SeekBar.setProgress(ps1);
        ButtonListener(ps1ButtonMinus, ps1SeekBar, ps1TextView, false);
        ButtonListener(ps1ButtonPlus, ps1SeekBar, ps1TextView, true);
        ps1SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				ps1TextView.setText(Integer.toString(progress));
				String values = "0x"+Integer.toHexString(Integer.parseInt((String) ps1TextView.getText())) + " " + "0x"+Integer.toHexString(Integer.parseInt((String) ps2TextView.getText()));
				try {
					writeFile(ps_canc, values);
				} catch (IOException e) {
					// TODO Auto-generated catch block
				      String Message = "Problem writing proximity calibration values";
				      Log.e(TAG, Message);
				}
			}
		});

        
        ps2TextView.setText(ps2.toString());
        ps2SeekBar.setProgress(ps2);
        ButtonListener(ps2ButtonMinus, ps2SeekBar, ps2TextView, false);
        ButtonListener(ps2ButtonPlus, ps2SeekBar, ps2TextView, true);
        ps2SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				ps2TextView.setText(Integer.toString(progress));
				String values = "0x"+Integer.toHexString(Integer.parseInt((String) ps1TextView.getText())) + " " + "0x"+Integer.toHexString(Integer.parseInt((String) ps2TextView.getText()));
				try {
					writeFile(ps_canc, values);
				} catch (IOException e) {
					// TODO Auto-generated catch block
				      String Message = "Problem writing proximity calibration values";
				      Log.e(TAG, Message);
				}
				if (checkBoxSetOnBoot.isChecked()) {
					save();
				}
			}
		});
        
    }
    
    @Override
   protected void onResume() {
      super.onResume();
      sensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	sensorManager.unregisterListener(this);
    }


	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
	      TextView proxState = (TextView) findViewById(R.id.proxState);
	      String distance;
	      
	      float state = event.values[0]; 
	      if (state == 0.0) {
	    	  distance = "NEAR";
	      } else if(state > 0.0) {
	    	  distance = "FAR";
	      } else {
	    	  distance = "ERROR";
	      }
	      proxState.setText(distance);
	}
	
	public void readValues() {
	    try
	    {
	      String str = readFile(ps_canc);
	      Log.i(TAG, "Reading from ps_canc: " + str);
	      String int1="";
	      String int2="";

	      String re1=".*?";	// Non-greedy match on filler
	      String re2="(?:[a-z][a-z]*[0-9]+[a-z0-9]*)";	// Uninteresting: alphanum
	      String re3=".*?";	// Non-greedy match on filler
	      String re4="((?:[a-z][a-z]*[0-9]+[a-z0-9]*))";	// Alphanum 1
	      String re5=".*?";	// Non-greedy match on filler
	      String re6="(?:[a-z][a-z]*[0-9]+[a-z0-9]*)";	// Uninteresting: alphanum
	      String re7=".*?";	// Non-greedy match on filler
	      String re8="((?:[a-z][a-z]*[0-9]+[a-z0-9]*))";	// Alphanum 2

	      Pattern p = Pattern.compile(re1+re2+re3+re4+re5+re6+re7+re8,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	      Matcher m = p.matcher(str);
	      if (m.find())
	      {
	          int1=m.group(1);
	          int2=m.group(2);
	      }
	      
	      ps1 = Integer.parseInt(int1.substring(1), 16);
	      ps2 = Integer.parseInt(int2.substring(1), 16);
	    }
	    
	    catch (IOException e)
	    {
	      ps1 = -1; 
	      ps2 = -1;
	      String Message = "Proximity sensor calibration file not found";
	      Log.e(TAG, Message);
	    }
	}
	
	static String readFile(String path) throws IOException {
		Process p;
		String value = "";
		p = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(p.getOutputStream());
		DataInputStream is = new DataInputStream(p.getInputStream());
		InputStreamReader ir = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(ir);
		os.writeBytes("cat " + path + "\n");
		value = br.readLine();
		os.writeBytes("chmod 0666 " + ps_canc + "\n");
		os.writeBytes("exit\n");
		os.flush();
		os.close();
			
		return value;
	}
	
	static void writeFile(String path, String values) throws IOException {
		Process p;
		p = Runtime.getRuntime().exec("sh");
		DataOutputStream os = new DataOutputStream(p
					.getOutputStream());

		String command = "echo " + values + " > "
					+ path + "\n";
		os.writeBytes(command);
		os.writeBytes("exit\n");
		os.flush();
		os.close();
	}
	
	static void writeFileRoot(String path, String values) throws IOException {
		Process p;
		p = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(p
					.getOutputStream());

		String command = "echo " + values + " > "
					+ path + "\n";
		os.writeBytes(command);
		os.writeBytes("exit\n");
		os.flush();
		os.close();
	}
	
	void save() {
		String values = "0x"+Integer.toHexString(Integer.parseInt((String) ps1TextView.getText())) + " " + "0x"+Integer.toHexString(Integer.parseInt((String) ps2TextView.getText()));
		Preferences.setPrefs(this, "Values", values);
	}
	
	public void ButtonListener (final Button button, final SeekBar seekBar, final TextView textView, final Boolean plus) {
        button.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				Integer value = Integer.parseInt((String) textView.getText());
				if (plus)
					value++;
				else
					value--;
				seekBar.setProgress(value);
				if (checkBoxSetOnBoot.isChecked()) {
					save();
				}
			}
        	
        });
	}

	
}