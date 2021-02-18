package com.hms.demo.itla;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.maps.model.LatLng;

public class GPSTracker extends LocationCallback {
    private Context context;
    private OnLocationEventListener listener;
    // Location interaction object.
    private final FusedLocationProviderClient fusedLocationProviderClient;
    // Location request object.
    private LocationRequest mLocationRequest;

    public void setListener(OnLocationEventListener listener) {
        this.listener = listener;
    }

    public GPSTracker(Context context) {
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

    }

    public void startLocationRequests() {
        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
// Check the device location settings.
        settingsClient.checkLocationSettings(locationSettingsRequest)
                // Define callback for success in checking the device location settings.
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.e("GPS","LocationSettings success");
                        // Initiate location requests when the location settings meet the requirements.
                        fusedLocationProviderClient
                                .requestLocationUpdates(mLocationRequest, GPSTracker.this, Looper.getMainLooper())
                                // Define callback for success in requesting location updates.
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e("GPS", "Started location Requests");
                                    }
                                }).addOnFailureListener((e)->{
                                    Log.e("GPS","onRequestFail "+e.toString());
                        });
                    }
                })
                // Define callback for failure in checking the device location settings.
                .addOnFailureListener((e) -> {
                    Log.e("GPS",e.toString());
                    // Device location settings do not meet the requirements.
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            ResolvableApiException rae = (ResolvableApiException) e;

                            if (listener != null) listener.onResolutionRequired(rae);
                            break;
                    }

                });
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        for(Location location : locationResult.getLocations()){
            Log.e("GPS","Location: lat="
                    +location.getLatitude()+" lon:"+location.getLongitude());
            if(listener!=null){

                listener.onLocationChanged(new LatLng(location.getLatitude(),location.getLongitude()));
            }
        }
    }

    public void stopLocationRequests() {
        // Note: When requesting location updates is stopped, the mLocationCallback object must be the same as LocationCallback in the requestLocationUpdates method.
        fusedLocationProviderClient.removeLocationUpdates(this)
                // Define callback for success in stopping requesting location updates.
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //...
                    }
                })
                // Define callback for failure in stopping requesting location updates.
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("GPS","Failed to stop the service");
                    }
                });
    }

    public interface OnLocationEventListener {
        public void onResolutionRequired(ResolvableApiException e);

        public void onLocationChanged(LatLng location);
    }
}
