package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * klasa początkowa uruchamiana przy starcie aplikacji
 */
public class Start extends AppCompatActivity {

    /**
     * metoda uruchamiająca się przy starcie aplikacji. inicjalizuje wszytkie elementy sceny.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button guestButton = (Button) findViewById(R.id.button2);

        guestButton.setOnClickListener(new View.OnClickListener() {
            /**
             * metoda uruchamiająca się przy kliknięciu w guzik wejdz jako gosc. przenosi on nas do mapy bez logowania ale nie mamy dostepu do wszytkich funkji
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        Button loginButton = (Button) findViewById(R.id.button1);

        loginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * metoda uruchamiająca się przy kliknięciu w guzik zaloguj się. przenosi nas do ekranu logowania
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),Login.class);
                startActivity(intent);
            }
        });

        Button registerButton = (Button) findViewById(R.id.button3);

        registerButton.setOnClickListener(new View.OnClickListener() {
            /**
             * metoda uruchamiająca się przy kliknięciu w guzik zarejestruj się. przenosi nas do strony rejestracji
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),register.class);
                startActivity(intent);
            }
        });
    }
}