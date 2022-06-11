package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * klasa przechowujące dane użytkowniak i jego czynności w aplikacji
 */
public class Dane {
    public static String Login;
    public static int Id = 0;
    public static int tag;
    public static Double lat;
    public static Double Lng;
    public static int typ;
    public static String tytul;
    public static String opis;
    public static String RegLogin;
    public static String RegPassword;
    public static int map = 1;
    public static int markers = 0;
    public static List<String> punkty = new ArrayList<>();
}
