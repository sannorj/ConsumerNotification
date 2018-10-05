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

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;



    FirebaseDatabase database;
    DatabaseReference ref,PostRef;
    //authendication
    private FirebaseAuth mAuth;
    Double lati,longi;
    String cat;
    ArrayList<LatLng> latlongList;


    private static final int PENDINGINTENT_REQUEST_CODE = 6;

    //geoFence declarations
    private static final int REQUEST_PERMISSIONS_GEOFENCE_REQUEST_CODE = 5;
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;


    LocationManager locMan;
    LocationListener locLin;
    LatLng currentLocation;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED&&requestCode ==3){

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



        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),12,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notiMan = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            notiMan.createNotificationChannel(new NotificationChannel("12","channel_Name",NotificationManager.IMPORTANCE_LOW));

        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
        notificationBuilder
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("asdas")
                .setContentText("asdas")
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(false);



        notiMan.notify((int)(System.currentTimeMillis()/1000),notificationBuilder.build());



        //createNotification("asdasd","asdads");


        //location
        locMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locLin = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
                Log.i("GeoFence", "current location : "+ currentLocation.toString());


                float[] results =new float[10];
                if (!latlongList.isEmpty()) {
                    LatLng lataa =latlongList.get(0);
                    Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,lataa.latitude , lataa.longitude, results);
                    Log.i("location distance :", "" + String.valueOf(results[0]));

                    try{
                        createNotification("GEO Fence","message");
                    }
                    catch (Exception e){
                        Log.i("location distance :", "aahaan");
                    }


                }
                //createGeofence(location.);
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

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
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
                          System.out.println("food is"+cat);

                        //  mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Marer").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                         // mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Food Issues").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                           if(cat.equals("Road"))
                            {
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Road Issues").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));


                            }else if (cat.equals("Other"))
                            {
                          mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Other Issues").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                            }else if (cat.equals("service_issues"))
                           {
                               mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Service Issues").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

                           }
                           else                           {
                              mMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title("Food Issues").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

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






    // Create notification
    public Notification createNotification(String title,String msg) {

        Intent intent = new Intent(this,MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,PENDINGINTENT_REQUEST_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setColor(Color.RED)
                .setContentTitle(title)
                .setContentText(msg)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(false);

        return notificationBuilder.build();
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
