package com.sensorreader;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	private SensorEventListener mSensorListener;
	private SensorManager mSensorManager;
	private Sensor mSensor;	
	private LocationManager locManager;
	private LocationListener locListener;
	private Location mobileLocation;	
	private double pressure_MBAR,baroaltitude,gpsaltitude;	
	private TextView BaroText,GpsText;
	private boolean barometer = false,gps=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//ANA GÖRÜNTÜ EKRANI YUKLENIYOR
		setContentView(R.layout.activity_main);
		
		//EKRANDAKI TEXT ALANLAR TANIMLANIYOR
		BaroText = (TextView) findViewById(R.id.textBarometer);
		GpsText = (TextView) findViewById(R.id.textGps);
		
		//barometre ve gps cihazlarýnýn desteklenip desteklenmediðini kontol edeceðiz
		PackageManager PM= this.getPackageManager();
		gps=PM.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        barometer = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);   		
		
		//Sensör servisi baþlatýlýyor, eðer cihazda barometre varsa
        if(barometer)
		GetCurrentSensors();
        else
        	BaroText.setText("Barometre Desteklenmiyor");	
        
		//Gps servisi baþlatýlýyor, eðer cihazda gps varsa
        if(gps)
		GetCurrentLocation();
        else
        	GpsText.setText("Gps Desteklenmiyor");	
        
        //not: androidmanifest.xml dosyasýna gps ve sdcarda ayrýca internete baðlanmak için 
        //gerekli izinler eklendi oradan check edebilirsin
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
private void GetCurrentSensors() {			
	mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);		
	   mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
	    mSensorListener = new SensorEventListener() {
	    	@Override
	        public void onSensorChanged(SensorEvent event) {	
	    		 	// Update current measured pressure.
			    		if (event.sensor.getType() != Sensor.TYPE_PRESSURE) return;  // Should not occur.
			    		pressure_MBAR = event.values[0];
			    		//basýncý yüksekliðe çevirme formulu
			    		float altitude = (float) (44330 * (1 - Math.pow((pressure_MBAR/SensorManager.PRESSURE_STANDARD_ATMOSPHERE), 0.190295)));	
		            	BaroText.setText(String.format("Barometer: %.1f m",altitude) + " / " + String.format("%.1f mBar",pressure_MBAR));					            
	        }
	        public void onAccuracyChanged(Sensor sensor, int accuracy) {
	        }				   
	    };			  
	    if (mSensor != null){
	    	mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_GAME);		
	     }	
	    }

private void GetCurrentLocation() { 	
	locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);	
	locListener = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			mobileLocation = location;
			if (mobileLocation != null) {	
				
				gpsaltitude=mobileLocation.getAltitude();	
				GpsText.setText(String.format("GPS: %.1f m",gpsaltitude) + 
						String.format(" LAT: %.6f",mobileLocation.getLatitude())  + 
						  String.format(" LON: %.6f",mobileLocation.getLongitude()));
			} 
		}			
	};
	locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);	 
}
}
