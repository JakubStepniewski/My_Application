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
 * Klasa odpowiedzilna za logowanie się użytkowniak do użytkownika do aplikacji. Wysyła ona zapytanie do bazy danych z danymu logowania użytkownika następnie odbiera od bazy czy dane podane przez użytkoniwka są prowidłowe
 */
public class Login extends AppCompatActivity {

    EditText login, password;
    TextView textView;


    /**
     * metoda uruchamiania przy tworzeniu strony
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button) findViewById(R.id.buttonLogin);

        login = (EditText) findViewById(R.id.Login);
        textView = (TextView) findViewById(R.id.LoginText);
        password = (EditText) findViewById(R.id.Password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * metoda uruchamiana przy kliknięciu w guzik. Wysyłanie informacji z pól formularza do bazy danych
             * @param v
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
            }
        });
    }

    /**
     * metoda łącząca się z bazą danych i wysyłająca dane logowania, odbiera wynik z bazy danych
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
                Log.d("testlogin", result);


                int number = Integer.parseInt(result);

                if(number > 0){
                    Dane.Login = String.valueOf(login.getText());
                    Dane.Id = number;
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else if(result.equals("0")){
                    Toast toast = Toast.makeText(getApplicationContext(),"Podano złe hasło",Toast.LENGTH_SHORT);
                    toast.show();
                } else if(result.equals("-1")){
                    Toast toast = Toast.makeText(getApplicationContext(),"Nie ma takiego użytkownika",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }

        JSONTask obj = new JSONTask();
        try {
            String URL = "https://comayo.pl/ProjektJava/login.php?login=" + login.getText() + "&password=" +password.getText();
            obj.execute(URL).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}