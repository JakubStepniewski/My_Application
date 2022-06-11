package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class addMarker extends AppCompatActivity {

    EditText tytul, opis;
    RadioButton radioButton;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        radioGroup = findViewById(R.id.radioGroup);
        tytul = findViewById(R.id.Tytul);
        opis = findViewById(R.id.Opis);

        Button buttonPost = findViewById(R.id.buttonPost);
        buttonPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int radioId = radioGroup.getCheckedRadioButtonId();

                radioButton = findViewById(radioId);

                if(radioButton.getText().equals("informacyjne")){
                    Dane.typ = 1;
                }else if(radioButton.getText().equals("środowiskowe")){
                    Dane.typ = 2;
                }else
                    Dane.typ = 3;

                Dane.tytul = String.valueOf(tytul.getText());
                Dane.opis = String.valueOf(opis.getText());

                try {
                    fetch();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        finish();
    }

    public void fetch() throws ExecutionException, InterruptedException {
        class JSONTask extends AsyncTask<String, String, String> {
            @Override
            protected String doInBackground(String... params) {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {

                    URL url = new URL(params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    String finalJson = buffer.toString();


                    return finalJson;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.d("testlogin", result);



            }
        }
        JSONTask obj = new JSONTask();
        try {
            String URL = "https://comayo.pl/ProjektJava/insert.php?latitude="+Dane.lat+"&longitude="+Dane.Lng+"&title="+Dane.tytul+"&description="+Dane.opis+"&type="+Dane.typ+"&creator="+Dane.Id;
            obj.execute(URL).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}