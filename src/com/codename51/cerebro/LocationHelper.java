// Location Helper class to pinpoint current location of the user. Uses the best available services
// GPS or network to find out current location of the user. Go through this class properly, important
// location fundas here.

package com.codename51.cerebro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationHelper {
	LocationManager locationManager;
    private LocationResult locationResult;
    boolean gpsEnabled = false;
    boolean networkEnabled = false;
    Context cxt;
    Activity act;
    int an = 0;

    public LocationHelper(Context c, Activity a)
    {
    	this.cxt=c;
    	this.act=a;
    }
    
    public boolean getLocation(Context context, LocationResult result)
    {       
        locationResult = result;
		
        if(locationManager == null)
        {
            locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        }
        	
        	//System.out.println("Check PLease 2");
            //exceptions thrown if provider not enabled
            try
            {
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
            catch (Exception ex) {}
            try
            {
                networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
            catch (Exception ex) {}

            //dont start listeners if no provider is enabled
            if(!gpsEnabled && !networkEnabled)
            {
                return false;
            }

            if(gpsEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            }
            if(networkEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
            }

            //System.out.println("Check PLease 1");
            GetLastLocation();
            //System.out.println("Check PLease 2");
            return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location)
        {
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);

        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extra) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location)
        {
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGps);

        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extra) {}

    };

    private void GetLastLocation()
    {
    		

            locationManager.removeUpdates(locationListenerGps);
            locationManager.removeUpdates(locationListenerNetwork);
            
           
            Location gpsLocation = null;
            Location networkLocation = null;
            

            if(gpsEnabled)
            {
                gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if(networkEnabled)
            {
                networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            //if there are both values use the latest one
            if(gpsLocation != null && networkLocation != null)
            {
                if(gpsLocation.getTime() > networkLocation.getTime())
                {
                    locationResult.gotLocation(gpsLocation);
                }
                else
                {
                    locationResult.gotLocation(networkLocation);
                }

                return;
            }
            
            if(gpsLocation != null)
            {
                locationResult.gotLocation(gpsLocation);
                return;
            }

            else if(networkLocation != null)
            {
                locationResult.gotLocation(networkLocation);
                return;
            }
            
            else{
            	//Toast.makeText(this.cxt, "Sorry! Couldn't find location...", Toast.LENGTH_LONG).show();
            	AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
            	
            	builder.setCancelable(true);
            	builder.setTitle("Sorry. Couldn't find your location.");
            	
            	builder.setInverseBackgroundForced(true);
            	AlertDialog alert = builder.create();
            	alert.show();

            	an=1;
            	return;
            	
            }
            
            
        }

    public static abstract class LocationResult
    {
        public abstract void gotLocation(Location location);
    }
}

