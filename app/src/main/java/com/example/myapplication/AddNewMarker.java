package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
 * klasa odpowiedzialna na dodawanie nowych punktów na mapę
 */
public class AddNewMarker extends AppCompatActivity {


    RadioGroup radioGroup;
    EditText tytul, opis;
    RadioButton radioButton;


    /**
     * funkcja wywoływana w momencie otwarcia arkusza do dodawania punktów
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_marker);

        radioGroup = findViewById(R.id.grupaRadio);
        tytul = findViewById(R.id.addTytul);
        opis = findViewById(R.id.addOpis);

        Button addButton = findViewById(R.id.addNewMarkerButoon);
        addButton.setOnClickListener(new View.OnClickListener() {
            /**
             * metoda wykonywana po kliknięciu w guzik wybory
             * @param v przekazuje widok
             */
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
                finish();
            }
        });
    }
    public void fetch() throws ExecutionException, InterruptedException {
        class JSONTask extends AsyncTask<String, String, String> {
            /**
             * realizuję połączenie z bazą danych
             * @return zwraca odpowiedz z bazy danych
             */
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

            /**
             * sprawdza czy punkt został prawidłowo utworzony
             * @param result w tym parametrze przekazywana jest odpowidz z bazy
             */
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.d("testlogin", result);

                int number = Integer.parseInt(result);

                if(number == 1) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Utworzono nowy punkt",Toast.LENGTH_SHORT);
                    toast.show();
                }


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