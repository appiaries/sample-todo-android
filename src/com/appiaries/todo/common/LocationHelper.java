package com.appiaries.todo.common;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

/**
 * @author nmcuong
 *
 */
public class LocationHelper {
	private final static LocationHelper instance = new LocationHelper();
	
	Timer timer;
	LocationManager lm;
	LocationResult locationResult;
	boolean gpsEnabled = false;
	boolean networkEnabled = false;
	
	public static LocationHelper getInstance()
	{
		return instance;		
	}
	

	/**
	 * Obtain current location by GPS Providers and return via a callback interface
	 * 
	 * @param context
	 * @param result callback
	 * @return true/false
	 */
	public boolean getLocation(Context context, LocationResult result) {
		// I use LocationResult callback class to pass location value from
		// MyLocation to user code.
		locationResult = result;
		if (lm == null)
			lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

		// exceptions will be thrown if provider is not permitted.
		try {
			gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			networkEnabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		// don't start listeners if no provider is enabled
		if (!gpsEnabled && !networkEnabled)
			return false;

		// gps only
		if (gpsEnabled)
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
					locationListenerGps);
		
		// network only
		if (networkEnabled)
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
					locationListenerNetwork);
		timer = new Timer();
		timer.schedule(new GetLastLocation(), 10000);
		return true;
	}
	
	/**
	 * Get the last saved location.
	 * @param context
	 * @return Location
	 */
	public Location getLastLocation(Context context)
	{
		if (lm == null)
			lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

		Location netLoc = null, gpsLoc = null;
		
		// we choose one available GPS provider (GPS, Network)
		if (gpsEnabled)
			gpsLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (networkEnabled)
			netLoc = lm
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		// if there are both values use the latest one
		if (gpsLoc != null && netLoc != null) {
			if (gpsLoc.getTime() > netLoc.getTime())
				return gpsLoc;
			else
				return netLoc;
		}
		
		// gps only
		if (gpsLoc != null) {			
			return gpsLoc;
		}
		
		// network only
		if (netLoc != null) {			
			return netLoc;
		}
		
		return null;
		
	}
	
	
	/**
	 * Show a dialog to ask user enable GPS settings.
	 * @param context
	 */
	public void showSettingsAlert(final Activity activity) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

		// Setting Dialog Title
		alertDialog.setTitle("GPS settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");	

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						activity.startActivityForResult(intent, 1);						
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	// Listen for Location value from GPS provider
	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			locationResult.gotLocation(location);
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerNetwork);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	// Listen for Location value from Network provider
	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			locationResult.gotLocation(location);
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerGps);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			lm.removeUpdates(locationListenerGps);
			lm.removeUpdates(locationListenerNetwork);

			Location netLoc = null, gpsLoc = null;
			if (gpsEnabled)
				gpsLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (networkEnabled)
				netLoc = lm
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			// if there are both values use the latest one
			if (gpsLoc != null && netLoc != null) {
				if (gpsLoc.getTime() > netLoc.getTime())
					locationResult.gotLocation(gpsLoc);
				else
					locationResult.gotLocation(netLoc);
				return;
			}

			if (gpsLoc != null) {
				locationResult.gotLocation(gpsLoc);
				return;
			}
			if (netLoc != null) {
				locationResult.gotLocation(netLoc);
				return;
			}
			locationResult.gotLocation(null);
		}
	}

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}
}
