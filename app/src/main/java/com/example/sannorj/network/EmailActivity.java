package com.example.sannorj.network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;


public class EmailActivity extends AppCompatActivity {

    Spinner mySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        mySpinner = (Spinner) findViewById(R.id.spinner1);
        /*ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
               android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.names) );
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);*/

        Button btn = findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mail();



            }
        });
    }
    public  void  mail(){
        Intent intent,chooser;
        if(mySpinner.getSelectedItem().toString().equals(("Food")))
        {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto"));
            String [] to = {"www.sannorj@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL,to);
            intent.putExtra(Intent.EXTRA_SUBJECT,"Food ");
            intent.putExtra(Intent.EXTRA_TEXT,"The food ordered on this restaurant on this date is undesirable.. / Food stinks on this restaurant / Food or beverages are sold at exorbitant pricesg");
            intent.setType("message/rfc822");

            chooser = intent.createChooser(intent,"Send EMail");
            startActivity(chooser);




        }
        else if(mySpinner.getSelectedItem().toString().equals(("Road")))
        {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto"));
            String [] to = {"www.sannorj@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL,to);
            intent.putExtra(Intent.EXTRA_SUBJECT,"Road ");
            intent.putExtra(Intent.EXTRA_TEXT,"Road is unfavourable for local transport /Road is severely damaged /Alas! This isn't a road but just potholes.. Needs serious concern asap / Long pending repair on road!! Frustrating travelling on");
            intent.setType("message/rfc822");

            chooser = intent.createChooser(intent,"Send EMail");
            startActivity(chooser);




        }
        else if(mySpinner.getSelectedItem().toString().equals(("service_issues")))
        {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto"));
            String [] to = {"www.sannorj@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL,to);
            intent.putExtra(Intent.EXTRA_SUBJECT,"service ");
            intent.putExtra(Intent.EXTRA_TEXT,"Frustrating service provided on this restaurant;No proper response given / Arbitrary charging by three wheelers;No regulation?! / Unfair and bad conduct to a proper issue by this network provider.. Need a look on!!");
            intent.setType("message/rfc822");

            chooser = intent.createChooser(intent,"Send EMail");
            startActivity(chooser);




        }
        else{

            intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto"));
            String [] to = {"www.sannorj@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL,to);
            intent.putExtra(Intent.EXTRA_SUBJECT,"Other ");
            intent.putExtra(Intent.EXTRA_TEXT,"Specify the issue in concised format with proper citations.. \uD83D\uDE0A\uD83E\uDD17");
            intent.setType("message/rfc822");

            chooser = intent.createChooser(intent,"Send EMail");
            startActivity(chooser);
//sa





        }
    }
}
