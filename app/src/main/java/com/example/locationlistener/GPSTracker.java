package com.example.locationlistener;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class GPSTracker extends FragmentActivity implements LocationListener, OnMapReadyCallback, SensorEventListener {

    private GoogleMap map;
    LocationManager locationManager;
    String locationProvider;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private boolean mapready;
    private Marker m;
    private boolean firstMarkerPlaced;
    private LatLng pos;
    private LatLng old_pos;
    double x,y,z;
    double mag;
    ArrayList<Double> acc = new ArrayList<Double>();
    static final int SAMPLE_RATE = 25;
    // LIMITS
    private double DOWNLIMIT = -3;
    private double UPLIMIT = 4;

    private long old_time;

    private SensorManager sensorManager;
    Sensor accelerometer;
    TextView cValues, MAC;

    EditText UP, DAWN;
    Button submit, clear, START;
    String mac;




    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //set the layout
        setContentView(R.layout.activity_maps);
        mac = getRandomId();

        //Accelerometer view
        cValues  = findViewById(R.id.cValues);
        //MAC =findViewById(R.id.MAC);

        UP = findViewById(R.id.UP);
        DAWN = findViewById(R.id.DAWN);
        submit = findViewById(R.id.submit);
        clear = findViewById(R.id.clear);

        old_time = System.currentTimeMillis();
        /**START = findViewById(R.id.START);

        START.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent start  =new Intent(GPSTracker.this, SET THE NEW VIEW AND PAGE)
            }
        });*/



        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(GPSTracker.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //initialize the locaiton manager
        this.initializeLocationManager();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String up = UP.getText().toString();
                String down = DAWN.getText().toString();
                try {
                    double upValue = Double.parseDouble(up);
                    double downValue = Double.parseDouble(down);

                    UPLIMIT = upValue;
                    DOWNLIMIT = downValue;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Log.d(TAG, "onSensorChanged: X " + sensorEvent.values[0] + "| Y: " + sensorEvent.values[1] + "| Z: "+sensorEvent.values[2] );
        //cValues.setText("X : " + sensorEvent.values[0] + " | Y : " + sensorEvent.values[1] + " | Z : " + sensorEvent.values[2]);
        x = sensorEvent.values[0];
        y = sensorEvent.values[1];
        z = sensorEvent.values[2];
        // calculate the magnitude
        mag = Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
        // add the magnitude to the running average
        acc.add(mag);
        String type = null;

        if(acc.size() >= SAMPLE_RATE) {
            double output = applyFiltering(acc);
            //System.out.println(UPLIMIT);
            //System.out.println(DOWNLIMIT);
            double anomaly = output - GRAVITY;
            if (isNotAnomaly(output)){
                   }
            else if (anomaly > UPLIMIT && System.currentTimeMillis() - old_time > 1000){
                map.addMarker(new MarkerOptions()
                        .position(pos)
                        .title("Anomaly UP")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                old_time = System.currentTimeMillis();
                type = "up anomaly";
            } else if (anomaly < DOWNLIMIT && System.currentTimeMillis() - old_time > 1000){
                map.addMarker(new MarkerOptions()
                        .position(pos)
                        .title("Anomaly DOWN")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                old_time = System.currentTimeMillis();
                type = "down anomaly";
            }

            if(type != null){
                new SendData().execute(mac, Double.toString(pos.latitude),Double.toString(pos.longitude) ,type, String.valueOf(anomaly));
            }
            clearArray(acc);
        }
    }

    private static void clearArray(ArrayList<Double> acc){
        acc.remove(0);
    }

    // running average
    private static double applyFiltering(ArrayList<Double> acc){
        double avg = 0;
        for(Double mag : acc){
            avg += mag;
        }
        return avg / acc.size();
    }

    private static final double GRAVITY = 9.8;

    // returns true if the magnitude shows there is an anomaly
    private static boolean isNotAnomaly(double magnitude){
        return (magnitude >= 9.81 && magnitude < 10);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public String getMAC(){
        String idMac;
        try {
            List<NetworkInterface> netList = Collections.list(NetworkInterface.getNetworkInterfaces());

            String stringMac = "";

            for (NetworkInterface networkInterface:netList){
                if (networkInterface.getName().equalsIgnoreCase("wlan0")){
                    for (int i=0; i<networkInterface.getHardwareAddress().length;i++){
                        String stringMacByte = Integer.toHexString(networkInterface.getHardwareAddress()[i] & 0xFF);

                        if (stringMacByte.length() == 1){
                            stringMacByte = "0" + stringMacByte;
                        }

                        stringMac = stringMac + stringMacByte.toUpperCase() + ":";
                    }
                    break;
                }
            }
            idMac =stringMac.substring(0,stringMac.length()-1);
            //MAC.setText(idMac);
            return idMac;


        } catch (SocketException e) {
            Toast.makeText(this, "NOT MAC FOUNDED", Toast.LENGTH_SHORT).show();
            MAC.setText("No MAC founded");
            return getRandomId();
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        mapready = true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to get location - requesting it");
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
    }
    @Override
    protected void onResume() {

        super.onResume();

        Log.i("called", "Activity --> onResume");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to get location - requesting it");
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
        this.locationManager.requestLocationUpdates(this.locationProvider, 400, 1, this);
    }

    @Override
    protected void onPause() {

        super.onPause();

        Log.i("called", "Activity --> onPause");

        this.locationManager.removeUpdates(this);
    }


    //----------------------------------------
    //	Summary: For initializing the map
    //----------------------------------------
    private void initializeMap() {

        //set map type
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //TODO: other map initialization as needed
    }

    //-------------------------------------------
    //	Summary: initialize location manager
    //-------------------------------------------
    private void initializeLocationManager() {

        //get the location manager
        this.locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        //define the location manager criteria
        Criteria criteria = new Criteria();

        this.locationProvider = locationManager.getBestProvider(criteria, false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to get location - requesting it");
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);


        //initialize the location
        if(location != null) {

            onLocationChanged(location);
        }
    }
    //------------------------------------------
    //	Summary: Location Listener  methods
    //------------------------------------------
    @Override
    public void onLocationChanged(Location location) {

        Log.i("called", "onLocationChanged");

        if (location != null && mapready) {
            //when the location changes, update the map by zooming to the location
            pos = new LatLng(location.getLatitude(), location.getLongitude());
            for (int i = 0; i<10; i++){
                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                map.moveCamera(center);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);
                map.animateCamera(zoom);
            }
            if (firstMarkerPlaced){
                PolylineOptions line=
                        new PolylineOptions().add(old_pos, pos)
                                .width(10).color(Color.RED);
                map.addPolyline(line);
            }
            firstMarkerPlaced = true;
            old_pos = pos;
        }
    }

    protected String getRandomId(){
        String NUM = "0123456789";
        StringBuilder id = new StringBuilder();
        Random random = new Random();
        while (id.length() < 15){
            int index = (int) (random.nextFloat() * NUM.length());
            id.append(NUM.charAt(index));
        }
        String String = id.toString();
        return String;
    }

    @Override
    public void onProviderDisabled(String arg0) {

        Log.i("called", "onProviderDisabled");
    }

    @Override
    public void onProviderEnabled(String arg0) {

        Log.i("called", "onProviderEnabled");
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

        Log.i("called", "onStatusChanged");
    }

    /**
     *
     * Use this method to find the exact address using the altitude and latitude
    Geocoder geocoder = new Geocoder(MainActivity.this);
        try{
        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
        Address.setText(addressList.get(0).getAddressLine(0));

    } catch (
    IOException e) {
        Address.setText("ERROR");
    }*/


}