package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.time.Year;
import java.util.concurrent.ExecutionException;


/**
 * klasa odpowiedzialna za wyswietlania strony z opisem punktu
 */
public class description extends AppCompatActivity {

    TextView Title,Desciption,Author;

    /**
     * metoda uruchamiana przy tworzeniu stony. Pobiera dane z bazy, i wypisuje je na ekranie
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        try {
            fetch();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Title = (TextView) findViewById(R.id.markerTitle);
        Desciption = (TextView) findViewById(R.id.markerDescription);
        Author = (TextView) findViewById(R.id.markerAutor);




    }

    /**
     *metoda odpowiedzialna za połączenie się z bazą danych i pobraniem z niej informacji
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
                    JSONArray parentArray = parentObject.getJSONArray("Details");

                        JSONObject finalObject = parentArray.getJSONObject(0);

                        String Str_Title = finalObject.getString("Title");
                        String Str_Description = finalObject.getString("Description");
                        String Str_Author = finalObject.getString("login");

                        Title.setText(Str_Title);
                        Desciption.setText(Str_Description);
                        Author.setText(Str_Author);



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }

        JSONTask obj = new JSONTask();
        obj.execute("https://comayo.pl/ProjektJava/description.php?id=" + Dane.tag).get();

    }
}