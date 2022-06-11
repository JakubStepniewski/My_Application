package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.CaseMap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Klasa odpowiedzialnma za wyświetlenie listy punktów utworzonych przez użytkownika
 */
public class markers extends AppCompatActivity {


    /**
     * funkcja uruchamiana jest przy starcie strony. Dzięki niej inicjalizowane są elementy sceny.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markers);

        try {
            fetch();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ListView listMarkery = findViewById(R.id.listMarkers);

        Log.d("punkty", String.valueOf(Dane.punkty.size()));
        //Log.d("punkty",Dane.punkty.get(2));


        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,Dane.punkty);
        listMarkery.setAdapter(arrayAdapter);

        listMarkery.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Log.d("markery lista","siemanko");
                }else if(position==1){
                    Log.d("markery lista","siemanko");
                }
                else if(position==2){
                    Log.d("markery lista","siemanko");
                }
                else if(position==3){
                    Log.d("markery lista","siemanko");
                }
                else if(position==4){
                    Log.d("markery lista","siemanko");
                }
                else if(position==5){
                    Log.d("markery lista","siemanko");
                }
                else if(position==6){
                    Log.d("markery lista","siemanko");
                }
                else if(position==7){
                    Log.d("markery lista","siemanko");
                }
                else if(position==8){
                    Log.d("markery lista","siemanko");
                }
                else if(position==9){
                    Log.d("markery lista","siemanko");
                }
                else if(position==10){
                    Log.d("markery lista","siemanko");
                }
                return false;
            }
        });



    }

    /**
     * metoda łącząca się z bazą danych
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
                Log.d("JSONmarker",result);

                try
                {
                    JSONObject parentObject = new JSONObject(result);
                    JSONArray parentArray = parentObject.getJSONArray("userPoints");


                    for(int i = 0;i<parentArray.length();i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);

                        String Str_Title = finalObject.getString("Title");
                        String Str_Type = finalObject.getString("Type");
                        String Str_ID = finalObject.getString("ID");

                        Log.d("punkty",Str_Title);

                        Dane.punkty.add(Str_Title);


                        Log.d("punkty",Dane.punkty.get(i));

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }

        JSONTask obj = new JSONTask();
        obj.execute("https://comayo.pl/ProjektJava/getUserPoints.php?id=" + Dane.Id).get();

    }



}