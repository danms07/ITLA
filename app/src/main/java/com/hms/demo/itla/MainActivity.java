package com.hms.demo.itla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.hms.demo.itla.databinding.MapBinding;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.model.PointOfInterest;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, HuaweiMap.OnPoiClickListener, GPSTracker.OnLocationEventListener {
    private static final int LOCATION_CODE=100;
    private HuaweiMap hMap;
    private MapBinding binding;
    private GPSTracker gps=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=MapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.mapView.onCreate(null);
        binding.mapView.getMapAsync(this);


    }

    private void requestLocationPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_CODE);
    }

    private void setupGPS() {
        if(gps==null){
            gps=new GPSTracker(this);
        }
        gps.setListener(this);
        gps.startLocationRequests();
    }

    private boolean checkLocationPermissions() {
        int acl= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int afl= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return acl==PackageManager.PERMISSION_GRANTED||afl==PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==LOCATION_CODE){
            if(checkLocationPermissions()) setupGPS();
        }
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap=huaweiMap;
        huaweiMap.setOnPoiClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapView.onResume();
        if(checkLocationPermissions()) setupGPS();
        else requestLocationPermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.mapView.onPause();
        if(gps!=null){
            gps.stopLocationRequests();
            gps.setListener(null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {

    }

    @Override
    public void onResolutionRequired(ResolvableApiException e) {
        // Call startResolutionForResult to display a pop-up asking the user to enable related permission.
        try {
            e.startResolutionForResult(MainActivity.this, 0);
        } catch (IntentSender.SendIntentException sendIntentException) {
            sendIntentException.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(LatLng location) {
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(location,20.0f);
        MarkerOptions options=new MarkerOptions();
        options.position(location);

        if(hMap!=null){
            hMap.clear();
            hMap.animateCamera(update);
            hMap.addMarker(options);
        }
    }
}