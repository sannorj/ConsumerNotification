package com.example.sannorj.network;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SignedObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;



    FirebaseDatabase database;
    DatabaseReference ref,PostRef;
    //authendication
    private FirebaseAuth mAuth;
    Double lati,longi;
    String cat;
    ArrayList<LatLng> latlongList;
    ArrayList<String> categoryList;


    private static final int PENDINGINTENT_REQUEST_CODE = 6;

    //geoFence declarations
    private static final int REQUEST_PERMISSIONS_GEOFENCE_REQUEST_CODE = 5;
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    int NotificationID =0;
    LocationManager locMan;
    LocationListener locLin;
    LatLng currentLocation;
    boolean mapBool =true;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {

                locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locLin);
            }


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latlongList = new ArrayList<LatLng>();
        categoryList = new ArrayList<String>();

        //showNotification("title","here you go",intent);

        setMap();

        //location
        locMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locLin = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
                //currentLocation = new LatLng(6.913785, 79.860882);


                if (mapBool){
                   //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,16f));
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(16f), 4000, null);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,16f),4000,null);


                    float[] results =new float[20];

                    if (!latlongList.isEmpty() && latlongList.size()==categoryList.size()) {

                        for (int i=0;i<latlongList.size();i++){

                            LatLng lataa =latlongList.get(i);
                            Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,lataa.latitude , lataa.longitude, results);

                            if (results[i] <= 2 && results[i]==0){
                                    showNotification("WE APP Notification", "A " + categoryList.get(i) + " post nearby your area (" + results[i] + " km)", latlongList.get(i));

                                    Log.i("location distance :", "" + String.valueOf(results[i]));

                            }


                        }


                    }


                    mapBool =false;
                }




            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };






        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locLin);
        }





        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = database.getReference("server/saving-data/fireblog/Posts");
        //PostRef = ref.getRef().child("latitude");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");

//every post check

        PostRef.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i("datasnapshot",""+dataSnapshot.getKey() + " " +dataSnapshot.getChildren() + dataSnapshot.hasChildren());

                latlongList.clear();
                  for (DataSnapshot child : dataSnapshot.getChildren()) {
                      Log.i("datasnap Post name :", child.getKey() + " ");
                      Log.i("datasnap c values :",child.child("latitude").getValue()+" "+child.child("longitude").getValue());

                      if(child.child("latitude").exists() || child.child("longitude").exists()) {

                          lati = (Double) child.child("latitude").getValue();
                          longi = (Double) child.child("longitude").getValue();

                          latlongList.add(new LatLng(lati,longi));


                          cat = (String)child.child("type").getValue();
                          categoryList.add(cat);
                          System.out.println("food is"+cat);
                          String description = (String) child.child("description").getValue();

                        //  mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Marer").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                         // mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Food Issues").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                           if(cat.equals("Road"))
                            {
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Road Issues").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).snippet(description));


                            }else if (cat.equals("Other"))
                            {
                          mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Other Issues").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).snippet(description));

                            }else if (cat.equals("service_issues"))
                           {
                               mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Service Issues").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).snippet(description));

                           }
                           else                           {
                              mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Food Issues").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet(description));

                          }

                      }

                      //mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Marker in Sydney"));
                  }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                Log.i("datasnapshot error :",databaseError.getCode()+"");
            }
        });

    }
 //  mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Food Issues").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


//setMap
    public void setMap(){


        Intent intent = getIntent();

        double latus = intent.getDoubleExtra("map_intent_lat",999.999) ;
        double lonus =intent.getDoubleExtra("map_intent_long",999.999) ;

        Log.i("location",""+latus + " , "+lonus);


    }


    // Create notification
    public void showNotification(String title, String content,LatLng latlng) {

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "WE APP",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setAutoCancel(true); // clear notification after click


        Intent map_intent = new Intent(getApplicationContext(), MapsActivity.class);
        map_intent.putExtra("map_intent_lat",latlng.latitude);
        map_intent.putExtra("map_intent_long",latlng.longitude);
        PendingIntent pi = PendingIntent.getActivity(this, 0, map_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(++NotificationID, mBuilder.build());


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        LatLngBounds SL = new LatLngBounds(
                new LatLng(5.8, 79), new LatLng(9.8, 82.5));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SL.getCenter(),7.5f));




    }






}
