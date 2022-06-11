package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


/**
 * Klasa odpowiedzialna za rejestracje nowych użytkowników i dodawania ich do bazy danych
 */
public class register extends AppCompatActivity {

    EditText Registerlogin, RegisterPassword;
    TextView RegisterText;


    /**
     * metoda uruchamiana jest przy starcie strony. inicjalizuje elementy sceny
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button RegisterButton = (Button) findViewById(R.id.buttonRegister);

        Registerlogin = (EditText) findViewById(R.id.registerLogin);
        RegisterText = (TextView) findViewById(R.id.RegisterText);
        RegisterPassword = (EditText) findViewById(R.id.registerPassword);



        RegisterButton.setOnClickListener(new View.OnClickListener() {
            /**
             * metoda uruchamiająca się przy kliknięciu w guzik zarejestruj. Po jego kliknięciu do bazy danych wysyłane są dane do utworzenia nowego użytkownika
             */
            @Override
            public void onClick(View v) {

                try {
                    fetch();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }

    /**
     * metoda odpowiedzialna za łączenię się z bazą danych
     */
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

                int number = Integer.parseInt(result);

                if(number == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Użytkownik o takim loginie juz istnieje",Toast.LENGTH_SHORT);
                    toast.show();
                }
                if(number != 0) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Udało ci się utworzyć użytkownika",Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        }





        Dane.RegPassword = String.valueOf(RegisterPassword.getText());
        Dane.RegLogin = String.valueOf(Registerlogin.getText());

        JSONTask obj = new JSONTask();
        try {
            String URL = "https://comayo.pl/ProjektJava/register.php?login="+Dane.RegLogin+"&password="+Dane.RegPassword;
            obj.execute(URL).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}