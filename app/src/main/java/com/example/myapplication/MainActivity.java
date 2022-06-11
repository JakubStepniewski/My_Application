package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.*;
import java.io.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.common.api.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mysql.cj.xdevapi.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class markerList{
    public static Vector<MarkerOptions> markerOptions;
    public static int liczba=10;
}


/**
 * klasa, w której jest mapa
 */
public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {


    private static final String TAG = "MAPACTIVITY";
    GoogleMap map;
    LatLng center;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;


    /**
     * metoda uruchamiająca się przy starcie strony. Inicjalizuje elementu głównej sceny
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FloatingActionButton changeMap = (FloatingActionButton) findViewById(R.id.changeMap);
        FloatingActionButton changeMarkers = (FloatingActionButton) findViewById(R.id.changeMarkers);
        FloatingActionButton mojeMarkery = (FloatingActionButton) findViewById(R.id.mojeMarkery);

        changeMap.setOnClickListener(new View.OnClickListener() {
            /**
             * metoda uruchamiająca się przy kliknięciu w przycisk do zmiany wyglądu mapy. Po jego kliknięciu zmienia się sposób wyświetlania mapy
             */
            @Override
            public void onClick(View v) {
                Log.d("main","cos");
                if(Dane.map == 2){
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    Dane.map = 1;
                }else{
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    Dane.map = 2;
                }
            }
        });

        changeMarkers.setOnClickListener(new View.OnClickListener() {
            /**
             * metoda uruchamiająca się przy kliknięciu w guzik filtrowania punktów na mapie. Po jego kliknięciu zmieniają się typy wyświetlanych punktów
             */
            @Override
            public void onClick(View v) {
                map.clear();
                Dane.markers ++;
                if(Dane.markers%4==0){
                    Dane.markers = 0;
                }else if(Dane.markers%4==1){
                    Dane.markers = 1;
                }else if(Dane.markers%4==2){
                    Dane.markers = 2;
                }else if(Dane.markers%4==3){
                    Dane.markers = 3;
                }
                try {
                    fetch();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        mojeMarkery.setOnClickListener(new View.OnClickListener() {
            /**
             * metoda uruchamiająca się przy kliknięciu w guzik listy markerów. Po jego kliknięciu wyświetla się scena z listą punktów utworzonych przez użytkowniak
             */
            @Override
            public void onClick(View v) {
                if(Dane.Id == 0){
                    Toast toast = Toast.makeText(getApplicationContext(),"Musisz się zalogować aby korzystać z tej funkcji",Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),markers.class);
                    startActivity(intent);
                }
            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        center = new LatLng(50.87033, 20.62752);


    }

    /**
     * metoda uruchamiająca się przy załadowaniu sie mapy. Inicjaluziję mapę i jej opcje, punkty
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        try {
            fetch();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        map.clear();

        try
        {

            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this,R.raw.map));
            if(!success)
            {
                Log.e(TAG,"Style failed to load");
            }
        }
        catch(Resources.NotFoundException e)
        {
            Log.e(TAG,"not found. error: ",e);
        }


        map.setMinZoomPreference(12.0f);
        map.setMaxZoomPreference(20.0f);

        LatLngBounds adelaideBounds = new LatLngBounds(
                new LatLng(50.793279, 20.508825), // SW bounds
                new LatLng(50.9182227, 20.719866) // NE bounds
        );


        map.setLatLngBoundsForCameraTarget(adelaideBounds);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 16));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUerLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }
        }
        map.setOnMapLongClickListener(this);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                Dane.tag = (Integer) marker.getTag();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),description.class);
                startActivity(intent);
            }
        });


    }

    private void zoomToUser()
    {
//        Task<Location> locationTask = fusedLocationProviderClient
    }

    private void enableUerLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUerLocation();
//                zoomToUser();
            } else {
                //a
            }
        }
    }



        public void onInfoWindowClick(Marker marker) {

        }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }


    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        /*
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(),description.class);
        startActivity(intent);

         */

        return false;
    }



    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {

    }


    /**
     * metoda odpowiadająca za pobranie punktów z bazy
     */
    public void fetch() throws ExecutionException, InterruptedException {
        class JSONTask extends AsyncTask<String,String,String>
        {
            @Override
            protected String doInBackground(String... params) {
                HttpURLConnection connection = null;
                BufferedReader reader= null;
                try {

                    URL url= new URL(params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();
                    String line="";
                    while((line=reader.readLine())!=null)
                    {
                        buffer.append(line);
                    }
                    String finalJson = buffer.toString();


                    return finalJson;

                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch(IOException e)
                {
                    e.printStackTrace();
                } finally {
                    if(connection!=null) { connection.disconnect(); }
                    try {
                        if(reader!=null){ reader.close();}
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                markerList list = null;
                Log.d("JSONDATA",result);
                list.liczba = 20;

                try
                {
                    JSONObject parentObject = new JSONObject(result);
                    JSONArray parentArray = parentObject.getJSONArray("Points");

                    Log.d("logi", String.valueOf(parentArray.length()));

                    for(int i = 0;i<parentArray.length();i++){
                        JSONObject finalObject = parentArray.getJSONObject(i);

                        String Str_Latitude = finalObject.getString("PointLatitude");

                        String Str_Longitude = finalObject.getString("PointLongtude");
                        String Str_Title = finalObject.getString("Title");
                        String Str_Type = finalObject.getString("Type");
                        String Str_ID = finalObject.getString("ID");
                        Log.d("Title: ",Str_Title);
                        Log.d("JSONDATA",Str_Latitude);
                        Log.d("JSONDATA",Str_Longitude);


                        int ID = Integer.parseInt(Str_ID);
                        int type = Integer.parseInt(Str_Type);
                        double Latitude=Double.parseDouble(Str_Latitude);
                        double Longitude=Double.parseDouble(Str_Longitude);
                        Log.d("JSONDATA CONVERTED", String.valueOf(Latitude));
                        Log.d("JSONDATA CONVERTED", String.valueOf(Longitude));



                        LatLng pos = new LatLng(Latitude,Longitude);
                        switch(type)
                        {
                            //neutralne
                            case 1:
                                if(Dane.markers == 0 || Dane.markers == 1)
                                map.addMarker(new MarkerOptions().position(pos).title(Str_Title).icon(BitmapDescriptorFactory.defaultMarker(200))).setTag(ID);
                                break;
                            //srodowiskowe
                            case 2:
                                if(Dane.markers == 0 || Dane.markers == 2)
                                map.addMarker(new MarkerOptions().position(pos).title(Str_Title).icon(BitmapDescriptorFactory.defaultMarker(100))).setTag(ID);
                                break;
                            //niebezpieczne
                            case 3:
                                if(Dane.markers == 0 || Dane.markers == 3)
                                map.addMarker(new MarkerOptions().position(pos).title(Str_Title).icon(BitmapDescriptorFactory.defaultMarker(0))).setTag(ID);
                                break;
                            default:
                                map.addMarker(new MarkerOptions().position(pos).title(Str_Title)).setTag(ID);
                                break;


                        }



                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }

        JSONTask obj = new JSONTask();
        obj.execute("https://comayo.pl/ProjektJava/points.php").get();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    /**
     * metoda uruchamiająca się po przytrzymaniu kliknięcia na mapie. Dzięki niej możemy dodawać nowe punkty na mapie.
     */
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Log.d("siema","siema");
        if(Dane.Id == 0){
            Toast toast = Toast.makeText(getApplicationContext(),"Musisz się zalogować aby korzystać z tej funkcji",Toast.LENGTH_SHORT);
            toast.show();
            Log.d("siema","asd");
        }else{
            Log.d("siema","naura");
            Dane.lat = latLng.latitude;
            Dane.Lng = latLng.longitude;

            Log.d("siema", String.valueOf(Dane.lat));
            Log.d("siema", String.valueOf(Dane.Lng));

            Intent nowy = new Intent();
            nowy.setClass(getApplicationContext(),AddNewMarker.class);
            startActivity(nowy);

            try {
                fetch();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }




    }
}






