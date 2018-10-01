package com.example.sannorj.network;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
    ArrayList<Double> latlongList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latlongList = new ArrayList<>();
        latlongList.clear();


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


                  for (DataSnapshot child : dataSnapshot.getChildren()) {
                      Log.i("datasnap Post name :", child.getKey() + " ");
                      Log.i("datasnap c values :",child.child("latitude").getValue()+" "+child.child("longitude").getValue());

                      if(child.child("latitude").exists() || child.child("longitude").exists()) {

                          lati = (Double) child.child("latitude").getValue();
                          longi = (Double) child.child("longitude").getValue();

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
