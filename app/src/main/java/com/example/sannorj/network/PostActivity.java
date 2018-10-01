package com.example.sannorj.network;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private ProgressDialog loadingBar;

    private ImageButton SelectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;
    private EditText PostName;
    private EditText PostEmail;
    private EditText outcome;
    private EditText category;
    private Spinner typeTex;
    String textspin;


    //s-implementation
    EditText location;
    private Button btn_location;
    //1 means true
    int PLACE_PICKER_REQUEST = 1;
    //Double[] latlong;
    ArrayList<Double> latlong;



    private String Description,name,email,out,selection;

    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;

    private String saveCurrentDate, saveCurrentTime, postRandomName, current_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);






        //s-implementation
       location = (EditText) findViewById(R.id.location);
        btn_location = (Button) findViewById(R.id.btn_location);
        //latlong = new Double[2];
        latlong = new ArrayList<Double>();

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();


        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");


       // SelectPostImage = (ImageButton) findViewById(R.id.select_post_image);
        UpdatePostButton = (Button) findViewById(R.id.update_post_button);
        PostDescription =(EditText) findViewById(R.id.post_description);
         PostName=(EditText) findViewById(R.id.post_name);
         PostEmail=(EditText) findViewById(R.id.post_email);
         outcome=(EditText) findViewById(R.id.post_outcome);
         category=(EditText) findViewById(R.id.post_category);
         typeTex=(Spinner) findViewById(R.id.spinner2);

      /* ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeTex.setAdapter(adapter);
        typeTex.setOnItemSelectedListener(this);*/

       // textspin = typeTex.getSelectedItem().toString();





        loadingBar = new ProgressDialog(this);


        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");




        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidatePostInfo();
            }
        });

        //s-implementation
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLocationClicked();
            }
        });

    }


    //s-implementation
    private void btnLocationClicked(){
// place picker intent
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {

            startActivityForResult(builder.build(this),PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, " ok  ", Toast.LENGTH_LONG).show();

        Log.i("OnAdressClicked","Button Clicked");



    }


    private void ValidatePostInfo()
    {
        Description = PostDescription.getText().toString();
        name = PostName.getText().toString();
        email = PostEmail.getText().toString();
        out = outcome.getText().toString();
        selection = category.getText().toString();


        if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please say something about your image...", Toast.LENGTH_LONG).show();

        }
        else if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please say something about your image...", Toast.LENGTH_LONG).show();

        }
        else if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please say something about your image...", Toast.LENGTH_LONG).show();

        }
        else if(TextUtils.isEmpty(out))
        {
            Toast.makeText(this, "Please say something about your image...", Toast.LENGTH_LONG).show();

        }
        else if(TextUtils.isEmpty(selection))
        {
            Toast.makeText(this, "Please say something about your image...", Toast.LENGTH_LONG).show();

        }
        else {

            loadingBar.setTitle("Add New Post");
            loadingBar.setMessage("Please wait, while we are updating your new post...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringDate();
        }
    }



    private void StoringDate()
    {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        SavingPostInformationToDatabase();

    }





    private void SavingPostInformationToDatabase()
    {
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", Description);
                    postsMap.put("fullname", userFullName);
                    postsMap.put("name",name);
                    postsMap.put("email",email);
                    postsMap.put("out",out);
                    postsMap.put("category",selection);
                    postsMap.put("latitude",latlong.get(0));
                    postsMap.put("longitude",latlong.get(1));
                    postsMap.put("type",typeTex.getSelectedItem().toString());

                    PostsRef.child(current_user_id + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        SendUserToMainActivity();
                                        Toast.makeText(PostActivity.this, "New Post is updated successfully.", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(PostActivity.this, "Error Occured while updating your post.", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }






    @Override
    //start activity result come
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


       //s-implementation
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                latlong.clear(); // clear array list old come


                //place a store object
                Place place = PlacePicker.getPlace(data, this);
                String address = place.getAddress() + "";
                LatLng PlaceLatLong = place.getLatLng();
//arrary
                latlong.add(PlaceLatLong.latitude);
                latlong.add(PlaceLatLong.longitude);

                Toast.makeText(this, address, Toast.LENGTH_LONG).show();
                //store address in textview
                location.setText(address);
                Log.i("Location result ", address);
                Log.i("Location result 1 ", PlaceLatLong + "");
                Log.i("Location result 2 ", latlong.get(0) + " " + latlong.get(1));
            }

        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            SendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }



    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}
