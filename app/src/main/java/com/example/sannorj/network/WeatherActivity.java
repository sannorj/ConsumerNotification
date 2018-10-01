package com.example.sannorj.network;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WeatherActivity extends AppCompatActivity {

    TextView t1_temp,t2_city,t3_description,t4_dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        t1_temp=(TextView)findViewById(R.id.textView);
        t2_city=(TextView)findViewById(R.id.textView3);
        t3_description=(TextView)findViewById(R.id.textView4);
        t4_dates=(TextView)findViewById(R.id.textView2);

        find_wether();
    }

    private void find_wether()
    {
        //String url="https://raw.githubusercontent.com/satheeshan07/sample/master/sample.md";
        //String url="http://api.openweathermap.org/data/2.5/weather?q=Jaffna,LK&appid=cdc68e34ad488857803a32a846e77a4e&units=Imperial";
        //String url="https://api.twitter.com/1.1/trends/closest.jsonhttps://api.twitter.com/1.1/trends/closest.json";
        String url="http://api.openweathermap.org/data/2.5/weather?q=Colombo,LK&appid=cdc68e34ad488857803a32a846e77a4e&units=Imperial";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");



                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String description = object.getString("description");
                    String city = response.getString("name");

                    t2_city.setText(city);
                    t3_description.setText(description);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    String formatted_date = sdf.format(calendar.getTime());

                    t4_dates.setText(formatted_date);

                    double temp_int = Double.parseDouble(temp);
                    double centi = (temp_int-32)*0.5556;
                    centi = Math.round(centi);
                    int i= (int)centi;
                    t1_temp.setText(String.valueOf(i));


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);

    }
}
